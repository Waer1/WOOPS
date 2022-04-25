import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;

import ca.rmen.porterstemmer.PorterStemmer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.swing.text.html.HTMLDocument.Iterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CrawlerConnection {

	final static String crawler_database_connection = "mongodb+srv://Waer:RHhDdESAKY5HvzZ@cluster0.jafiz.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	final static String indexer_database_connection = "mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		MongoDatabase db = get_database("NewCrawlerDB", crawler_database_connection);	
//		MongoCollection<Document> crawlercol = get_collection(db,"Documents");
//		System.out.println(crawlercol.count());
//		//ArrayList<Document> docs = crawlercol.find();
//		FindIterable<Document> iterDoc = crawlercol.find();
//		MongoCursor<Document> it = iterDoc.iterator();
//		Document doc = it.next();
//		System.out.println(doc);
//		
//		org.jsoup.nodes.Document htmldoc = null;
//		try
//		{
//			htmldoc = Jsoup.connect(doc.getString("link")).get();
//			
//		}
//		catch(Exception e)
//		{
//			System.out.println(e);
//		}
//		//MongoDatabase admindb = get_database("admin", indexer_database_connection);
//		//System.out.println(admindb.stats);
//		
//		//doc = it.next();
//		//System.out.println(htmldoc);
//		MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
//		MongoDatabase Indexerdb = mongoClient2.getDatabase("Search_index");
//		//MongoDatabase Indexerdb = get_database("Search_index", indexer_database_connection);
//		MongoCollection<Document> indexercol = get_collection(Indexerdb, "invertedfile");
//		FindIterable<Document> iterDoc2 = indexercol.find(Filters.eq("Word","scout"));
//		System.out.println(indexercol.countDocuments(Filters.eq("Word","scot")));
//		//MongoCursor<Document> it = iterDoc.iterator();
		IDF();
		
	}
	public static void IDF()
	{
		MongoDatabase db = get_database("NewCrawlerDB", crawler_database_connection);	
		MongoCollection<Document> crawlercol = get_collection(db,"Documents");
		System.out.println(crawlercol.count());
		FindIterable<Document> iterDoc = crawlercol.find();
		MongoCursor<Document> it = iterDoc.iterator();
		//Document doc = it.next();
		
		
		MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
		MongoDatabase Indexerdb = mongoClient2.getDatabase("Search_index");
		//MongoDatabase Indexerdb = get_database("Search_index", indexer_database_connection);
		MongoCollection<Document> indexercol = get_collection(Indexerdb, "invertedfile");
		;
		//System.out.println(indexercol.countDocuments(Filters.eq("Word","scot")));
		
		while(it.hasNext())
		{
			Document doc = it.next();
			String link = doc.getString("link");
			FindIterable<Document> iterDoc2 = indexercol.find(Filters.eq("DOC_ID",link));
			MongoCursor<Document> it2 = iterDoc2.iterator();
			while(it2.hasNext())
			{
				Document doc2 = it2.next();
				String word = doc2.getString("Word");
				int count = (int)indexercol.countDocuments(Filters.eq("Word",word));
				indexercol.updateMany(Filters.eq("Word",word), Updates.set("IDF", count));
			}
		}
	}
	public static MongoDatabase get_database(String databasename, String connection) {

		// MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
		// MongoDatabase db = mongoClient2.getDatabase(databasename);
		// return db;

		ConnectionString connectionString = new ConnectionString(connection);
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase(databasename);
		return database;
	}

	// function get collection from database

	public static MongoCollection<Document> get_collection(MongoDatabase db, String Collection_Name) {

		MongoCollection<Document> col = db.getCollection(Collection_Name);
		return col;
	}
}
