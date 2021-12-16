import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Scanner;

public class MessagingSystem {

	public static Jedis jedis = new Jedis("localhost");

	// Create a new user
	public static void createUser(String user, String password){
		jedis.hset("users:data:"+user, "username", user);
		jedis.hset("users:data:"+user, "password", password);
		jedis.hset("users:data:"+user, "subs", "");
	}

	// Check if the user and password are valid
	public static boolean checkLogin(String user, String password){
		try{
			return jedis.hgetAll("users:data:" + user).get("username").equals(user) &&
				jedis.hgetAll("users:data:" + user).get("password").equals(password);
		}catch(Exception e){
			return false;
		}
	}

	// Check if a certain user exists
	public static boolean checkUser(String user){
		try{
			return jedis.hgetAll("users:data:"+user).get("username").equals(user);
		}catch(Exception e){
			return false;
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		String login = null;    // control var for login
		int option;             // control var for selected option from the messaging system prompt

		// Variables that are used multiple times
		String user, password;
		List<String> loginSubs;

		while (true){
			if (login == null){
				// Messaging System prompt
				System.out.print("\nMessaging System options:"
									+ "\n" + "1) Login"
									+ "\n" + "2) Create new user"
									+ "\n" + "3) Exit"
									+ "\n" + "-> ");
				option = sc.nextInt();
				System.out.println();

				switch (option){
					// Login
					case 1:
						System.out.print("Username: ");
						sc.nextLine();
						user = sc.nextLine();

						System.out.print("Password: ");
						password = sc.nextLine();

						if (checkLogin(user, password)){
							System.out.println("Logged in successfully!");
							login = user;
						}else{
							System.out.println("Login failed...");
						}

						break;
					// Create new user
					case 2:
						System.out.print("Username: ");
						sc.nextLine();
						user = sc.nextLine();

						System.out.print("Password: ");
						password = sc.nextLine();

						if(!checkLogin(user, password)){
							createUser(user, password);
							System.out.println("New user created successfully!");
						}else{
							System.out.println("The username is taken, please choose a new one");
						}

						break;
					default:
						System.out.println("Goodbye!");
						System.exit(1);
				}
			}else{
				// Messaging System prompt
				System.out.print("\nMessaging System options:"
						+ "\n" + "1) Post Message"
						+ "\n" + "2) Subscribe to user"
						+ "\n" + "3) Unsubscribe to user"
						+ "\n" + "4) Read posted messages"
						+ "\n" + "5) Read posted messages from a subscription"
						+ "\n" + "6) Sign out"
						+ "\n" + "7) Exit"
						+ "\n" + "-> ");
				option = sc.nextInt();
				System.out.println();

				switch (option){
					// Post msg
					case 1:
						System.out.print("Write the message: ");
						sc.nextLine();
						String msg = sc.nextLine();

						jedis.rpush("users:msg:"+login, msg);
						System.out.println("Your message was successfully posted!");

						break;

					// Sub to user
					case 2:
						System.out.print("Write the username to subscribe: ");
						sc.nextLine();
						String sub_user = sc.nextLine();
						loginSubs = jedis.lrange("users:subs:"+login, 0, -1);

						if (!checkUser(sub_user)){
							System.out.printf("Username %s doesn't exist\n", sub_user);
						}else if(loginSubs.contains(sub_user)){
							System.out.printf("You're already subscribed to %s\n", sub_user);
						}else{
							jedis.rpush("users:subs:"+login, sub_user);
							loginSubs.add(sub_user);
							jedis.hset("users:data:"+login, "subs", String.valueOf(loginSubs));
						}

						break;

					// Unsub to user
					case 3:
						System.out.print("Write the username to unsubscribe: ");
						sc.nextLine();
						String unsub_user = sc.nextLine();
						loginSubs = jedis.lrange("users:subs:"+login, 0, -1);

						if(loginSubs.contains(unsub_user)){
							jedis.lrem("users:subs:"+login, 1, unsub_user);
							System.out.println("Successfully unsubscribed to " + unsub_user);
						}else{
							System.out.println("You are not subscribed to " + unsub_user);
						}

						break;

					// Read msgs
					case 4:
						System.out.println("Your posted messages");
						jedis.lrange("users:msg:"+login, 0, -1).forEach(elem -> System.out.printf("-> %s\n", elem));

						break;

					// Read msgs from SUB
					case 5:
						loginSubs = jedis.lrange("users:subs:"+login, 0, -1);

						for (String u : loginSubs){
							System.out.printf("%s's messages:\n", u);

							for(String m : jedis.lrange("users:msg:"+u, 0, -1)){
								System.out.printf("-> %s\n", m);
							}
						}

						break;

					// Sign out
					case 6:
						System.out.println("Goodbye " + login);
						login = null;
						break;
					default:
						System.out.println("Goodbye!");
						System.exit(1);
				}
			}
		}

	}
}
