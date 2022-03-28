import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.lang.UnknownError;
import java.net.UnknownHostException;

public class Indexer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello");
		System.out.println("Hello");
		

		ConnectionString connectionString = new ConnectionString("mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
        	.applyConnectionString(connectionString)
        	.build();
		MongoClient mongoClient = MongoClients.create(settings);
		//MongoDatabase database = mongoClient.getDatabase("test");

        MongoDatabase database = mongoClient.getDatabase("Search_index");
        System.out.println(database.getName());
        System.out.println("hello ahmed");
        MongoCollection<Document> dbCollection = database.getCollection("First_Collection");
        dbCollection.insertOne(new Document("ss","sss"));
	
		MongoClient mongoClient2 =  MongoClients.create("mongodb://localhost:27017");
		MongoDatabase db = mongoClient2.getDatabase("ahmed");
		//Collation col = db.getCollection("p2");
		Document doc1 = new Document("ahmed","sabry");
		MongoCollection<Document> col = db.getCollection("p8");
		col.insertOne(doc1);
		System.out.println("bye");
	}

}
