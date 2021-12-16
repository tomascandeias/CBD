import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Autocomplete {
	// Docs
	// https://redis.io/commands/zrangebylex

	public static void getNamesFromFile(String fname, Jedis jedis) throws FileNotFoundException {
		Scanner scf = new Scanner(new File(fname));

		while (scf.hasNextLine()){
			String name = scf.nextLine();
			jedis.zadd("Names", 0, name);
		}

		System.out.printf("Inserts: %d\n", jedis.zcount("Names", -Integer.MAX_VALUE, Integer.MAX_VALUE));

	}

	public static void main(String[] args) throws FileNotFoundException {
		Jedis jedis = new Jedis("localhost");
		Scanner sc = new Scanner(System.in);

		getNamesFromFile("src/main/resources/names.txt", jedis);

		while (true) {
			System.out.print("Search for ('Enter' for quit): ");
			String search = sc.nextLine();

			if(search.trim().equals("")) break;


			List<String> results = new ArrayList<>();
			byte[] prefixByte = ("[" + search).getBytes();
			byte[] prefixByteExtended = Arrays.copyOf(prefixByte, prefixByte.length + 1);
			prefixByteExtended[prefixByte.length] = (byte) 0xFF; // 0xFF -> escape sequence

			//key=Names, min=[search, max=[search0xFF
			for(String key : jedis.zrangeByLex("Names", "["+search, new String(prefixByteExtended))){
				results.add(key.replace("\n", ""));
				System.out.println(results.get(results.size()-1));
			}
		}
	}
}
