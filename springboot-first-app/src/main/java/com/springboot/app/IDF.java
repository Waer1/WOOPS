package com.springboot.app;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Sorts.ascending;

public class IDF {
	
	static HashMap<String,Integer> MetaData = new HashMap<String,Integer>();
	final static String crawler_database_connection = "mongodb+srv://Waer:RHhDdESAKY5HvzZ@cluster0.jafiz.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	final static String indexer_database_connection = "mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
		//MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
		//MongoDatabase Indexerdb = mongoClient2.getDatabase("Search_index");
		MongoDatabase Indexerdb = get_database("Search_index", indexer_database_connection);	
		MongoCollection<Document> indexercol = get_collection(Indexerdb,"invertedfile");
		
		//MongoDatabase Indexerdb = get_database("Search_index", indexer_database_connection);
		//MongoCollection<Document> indexercol = get_collection(Indexerdb, "invertedfile");
		//System.out.println(indexercol.countDocuments(Filters.eq("Word","scot")));
//		String s1 = "\"name\"";
//		Document doc1 = new Document("ahmed",s1);
//		indexercol.insertOne(doc1);
		FindIterable<Document> iterDoc = indexercol.find(Filters.eq("DOC_ID","https://www.javatpoint.com/java-main-method")).sort(ascending("Word"));
		MongoCursor<Document> it = iterDoc.iterator();
//		if(it.hasNext())
//		{
//			doc1 = it.next();
//			System.out.println(doc1.getString("ahmed"));
//		}
		String html = "<!doctype html> <html> <body bgcolor='#f0f0f0' align='center'> shit "
        		+"<h1>ahmed <div ahmed> <p> sabry </p></div></h1>"+"  </body></html>";
		int count =0;
		while(it.hasNext())
		{
//			if(count++ >200)
//			{
//				break;
//			}
			Document db_doc = it.next();
			//org.jsoup.nodes.Document doc = null;
			
			List<Integer> html2 = db_doc.getList("POSITION",Integer.class);
			//html2 = Jsoup.clean(html2, Whitelist.none());
			//if(db_doc.getString("Word") == "jvm" || db_doc.getString("Word") == "execut" || db_doc.getString("Word") == "static" || db_doc.getString("Word") == "block")
			System.out.println("word : "+db_doc.getString("Word") );
			System.out.println("positions : "+html2);
			
		}
		
	
		
		
		//System.out.println(Jsoup.clean(html, Whitelist.none()));
		//System.out.println(doc.text());
	       
//		DistinctIterable<String> doc = crawlercol.distinct("link", String.class);
//		MongoCursor<String> results = doc.iterator();
//		int count =0 ;
//		
//		while(results.hasNext())
//		{
//			results.next();
//			count++;
//		}
		//System.out.println("count = "+count);
		//int counter =(int)indexercol.countDocuments(Filters.eq("DOC_ID",word));
		
//		int counter =0 ;
//		while(it.hasNext())
//		{
//			
//			//iterDoc = indexercol.find(Filters.eq("IDF",1));
//			//it = iterDoc.iterator();
//			if(!it.hasNext())
//			{
//				break;
//			}
//			Document doc = it.next();
//			String word = doc.getString("Word");
//			if (MetaData.get(word) == null) {
//				MetaData.put(word, 1);
//			} else
//			{
//				int count = (int)indexercol.countDocuments(Filters.eq("Word",word));
//				indexercol.updateMany(Filters.eq("Word",word), Updates.set("IDF", count));
//				
//				
//				System.out.println("count "+counter);
//			}
//			counter++;
//;
//		}
	}
	public static MongoDatabase get_database(String databasename, String connection) {

		 MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
		 MongoDatabase db = mongoClient2.getDatabase(databasename);
		 return db;

//		ConnectionString connectionString = new ConnectionString(connection);
//		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
//		MongoClient mongoClient = MongoClients.create(settings);
//		MongoDatabase database = mongoClient.getDatabase(databasename);
//		return database;
	}

	// function get collection from database

	public static MongoCollection<Document> get_collection(MongoDatabase db, String Collection_Name) {

		MongoCollection<Document> col = db.getCollection(Collection_Name);
		return col;
	}

}
