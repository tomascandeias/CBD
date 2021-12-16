import static com.mongodb.client.model.Filters.eq;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RestaurantsHandler {

	private static String uri = "mongodb://127.0.0.1:27017";
	private static MongoClient client = MongoClients.create(uri);
	private static MongoDatabase database = client.getDatabase("cbd");
	private static MongoCollection<Document> collection = database.getCollection("restaurants");

	public static FindIterable<Document> search(Document document){
		return collection.find(document);
	}

	public static void insert(Document document){
		collection.insertOne(document);
	}

	public static int updateMany(Document filter, Document d) {
		UpdateResult r = collection.updateMany(filter, d);
		return (int) r.getModifiedCount();
	}

	public static void main(String[] args) {
		//INSERT
		Document restaurant = new Document("address", new Document("building", "9322").append("coord", Arrays.asList(-1, 1))
				.append("rua", "Rua de Aveiro")
				.append("zipcode", "1234-123"))
				.append("localidade", "Aveiro")
				.append("gastronomia", "Portuguesa")
				.append("grades", Arrays.asList(new Document("date", "2021-11-18T00:00:00Z").append("grade", "A").append("score", 100) ))
				.append("nome", "Aveiro Bakery")
				.append("restaurant_id", "123456789");
		insert(restaurant);

		// SEARCH, can be confirmed that the doc was inserted
		FindIterable<Document> search_res = search(new Document("localidade", "Aveiro"));
		System.out.println(search_res.first());

		// EDIT
		System.out.println(updateMany(new Document("restaurant_id", "1"), new Document("$set", restaurant)));

		// SEARCH, can be confirmed that the doc was updated
		FindIterable<Document> search_res2 = search(new Document("localidade", "Aveiro"));
		System.out.println(search_res2.first());
	}

}
