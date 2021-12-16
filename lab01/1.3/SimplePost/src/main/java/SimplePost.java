import java.util.HashSet;
import java.util.Set;
import redis.clients.jedis.Jedis;

public class SimplePost {
	public static class SimplePostSet{
		private Jedis jedis;

		public static String USERS = "users"; // Key set for users' name
		public SimplePostSet() {
			this.jedis = new Jedis("localhost");
		}
		public void saveUser(String username) {
			jedis.sadd(USERS, username);
		}
		public Set<String> getUser() {
			return jedis.smembers(USERS);
		}
		public Set<String> getAllKeys() {
			return jedis.keys("*");
		}
	}

	static class SimplePostList{
		private Jedis jedis;

		public static String USERS = "utilizadores"; // Key set for users' name
		public SimplePostList() {
			this.jedis = new Jedis("localhost");
		}
		public void saveUserLeft(String username) {
			jedis.lpush(USERS, username);
		}
		public void saveUserRight(String username) {
			jedis.rpush(USERS, username);
		}
		public Set<String> getUser() {
			Set<String> ret = new HashSet<>();
			for (int i=0; i<this.jedis.llen(USERS); i++){
				ret.add(String.valueOf(this.jedis.lindex(USERS, i)));
			}
			return ret;
		}
		public Set<String> getAllKeys() {
			return jedis.keys("*");
		}
	}

	static class SimplePostHash{
		private Jedis jedis;

		public static String USERS = "people:users:"; // Key set for users' name
		public static int counter;

		public SimplePostHash() {
			this.jedis = new Jedis("localhost");
			counter = -1;
		}
		public void saveUser(String username) {
			counter++;
			//key=people, fieldID=people:users:COUNTER, value=given username
			this.jedis.hset("people", USERS + counter, username);
		}
		public Set<String> getUser() {
			Set<String> ret = new HashSet<>();
			for (int i=0; i<counter+1; i++){
				ret.add(String.valueOf(this.jedis.hget("people", USERS + i)));
			}
			return ret;
		}
		public Set<String> getAllKeys() {
			return jedis.keys("*");
		}
	}




	public static void main(String[] args) {
		// set some users
		String[] users = { "Ana", "Pedro", "Maria", "Luis" };

		System.out.println("### SET ###");

		SimplePostSet board = new SimplePostSet();
		for (String user: users)
			board.saveUser(user);
		board.getAllKeys().stream().forEach(System.out::println);
		board.getUser().stream().forEach(System.out::println);

		System.out.println("### LIST ###");

		SimplePostList boardList = new SimplePostList();
		for (String user: users) {
			boardList.saveUserRight(user);
		}
		boardList.getAllKeys().stream().forEach(System.out::println);
		boardList.getUser().stream().forEach(System.out::println);


		System.out.println("### HASH ###");

		SimplePostHash boardHash = new SimplePostHash();
		for (String user: users)
			boardHash.saveUser(user);
		boardHash.getAllKeys().stream().forEach(System.out::println);
		boardHash.getUser().stream().forEach(System.out::println);

	}
}
