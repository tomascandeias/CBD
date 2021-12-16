import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AutocompleteB {
	// Docs
	// https://redis.io/commands/ZRANGEBYSCORE

	public static void getNamesFromFile(String fname, Jedis jedis) throws FileNotFoundException {
		Scanner scf = new Scanner(new File(fname));

		while (scf.hasNextLine()){
			String[] params = scf.nextLine().split(";");

			jedis.zadd("Names", 0, params[0]);
			jedis.zadd("ScoredNames", Integer.parseInt(params[1]), params[0]);
		}

		System.out.printf("Inserts: %d\n", jedis.zcount("Names", -Integer.MAX_VALUE, Integer.MAX_VALUE));
		System.out.printf("Inserts: %d\n", jedis.zcount("ScoredNames", -Integer.MAX_VALUE, Integer.MAX_VALUE));

	}

	public static void main(String[] args) throws FileNotFoundException {
		Jedis jedis = new Jedis("localhost");
		Scanner sc = new Scanner(System.in);

		getNamesFromFile("src/main/resources/nomes-pt-2021.csv", jedis);

		while (true) {
			System.out.print("Search for ('Enter' for quit): ");
			String search = sc.nextLine();

			if(search.trim().equals("")) break;


			List<String> results = new ArrayList<>();
			byte[] prefixByte = ("[" + search).getBytes();
			byte[] prefixByteExtended = Arrays.copyOf(prefixByte, prefixByte.length + 1);
			prefixByteExtended[prefixByte.length] = (byte) 0xFF;

			//key=Names, min=[search, max=[search0xFF
			for(String key : jedis.zrangeByLex("Names", "["+search, new String(prefixByteExtended))){
				results.add(key.replace("\n", ""));
			}

			Map<String, Double> resultsWithScore = new HashMap<>();
			Map<String, Double> finalResultsWithScore = resultsWithScore;
			jedis.zrangeByScoreWithScores("ScoredNames", -Integer.MAX_VALUE, Integer.MAX_VALUE).forEach(tuple ->{
				if (results.contains(tuple.getElement()) && !finalResultsWithScore.containsKey(tuple.getElement()))
					finalResultsWithScore.put(tuple.getElement(), tuple.getScore());

			});

			Map<String, Double> displayResultsWithScore = new HashMap<>();
			resultsWithScore.entrySet()
					.stream()
					.sorted(Map.Entry.comparingByValue())
					.forEachOrdered(x -> displayResultsWithScore.put(x.getKey(), x.getValue()));
			displayResultsWithScore.forEach((k, v)-> System.out.printf("%s: %.0f\n", k, v));
		}
	}
}
