import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class QueryProcessor {

	public static int number_documents;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
	}
	public static HashMap<String,ArrayList<Document>> Query_Process (String query)
	{
		ArrayList<String> string_array = Indexer.Remove_tags(query);
		string_array = Indexer.Remove_Stop_Words(string_array);
		string_array = Indexer.Stemming(string_array);
		
		// connection with atlas
		MongoDatabase indexerdb = Indexer.get_database("Search_index", Indexer.indexer_database_connection);
		MongoCollection<Document> indexercol = Indexer.get_collection(indexerdb, "invertedfile");
		
		// connection with local
		
		//MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
		//MongoDatabase indexerdb = mongoClient2.getDatabase("Search_index");
		//MongoCollection<Document> indexercol = Indexer.get_collection(indexerdb, "invertedfile");
		
		//FindIterable<Document> iterDoc = indexercol.find(Filters.eq("Word",""));
		
		//HashMap<String,ArrayList<Document>> DocLists = new HashMap<String,ArrayList<Document>>();
		HashMap<String,ArrayList<Document>> DocLists = new LinkedHashMap<String,ArrayList<Document>>();
		for(int i=0;i<string_array.size();i++)
		{
			if(!DocLists.containsKey(string_array.get(i)))
			{
				
			
				FindIterable<Document> iterDoc = indexercol.find(Filters.eq("Word",string_array.get(i)));
				MongoCursor<Document> it = iterDoc.iterator();
				while(it.hasNext())
				{
					Document doc = it.next();
//					ArrayList<Document> list = DocLists.get(string_array.get(i));
//					list.add(doc);
//					DocLists.put(string_array.get(i), list);
					if(DocLists.get(string_array.get(i)) == null)
					{
						ArrayList<org.bson.Document> list = new ArrayList<org.bson.Document>();
						list.add(doc);
						DocLists.put(string_array.get(i), list);
					}
					else
					{
						ArrayList<org.bson.Document> list = DocLists.get(string_array.get(i));
						list.add(doc);
						DocLists.put(string_array.get(i), list);
					}
				}
			}
			
		}
		
		number_documents = Indexer.countUniqueDoc(indexercol);
		return DocLists;
	}
	
}


//String query = "";
//
//ArrayList<String> string_array = Indexer.Remove_tags(query);
//string_array = Indexer.Remove_Stop_Words(string_array);
//string_array = Indexer.Stemming(string_array);
//
//// connection with atlas
//MongoDatabase indexerdb = Indexer.get_database("Search_index", Indexer.indexer_database_connection);
//MongoCollection<Document> indexercol = Indexer.get_collection(indexerdb, "invertedfile");
//
//// connection with local
//
////MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
////MongoDatabase indexerdb = mongoClient2.getDatabase("Search_index");
////MongoCollection<Document> indexercol = Indexer.get_collection(indexerdb, "invertedfile");
//
////FindIterable<Document> iterDoc = indexercol.find(Filters.eq("Word",""));
//
//HashMap<String,ArrayList<Document>> DocLists = new HashMap<String,ArrayList<Document>>();
//for(int i=0;i<string_array.size();i++)
//{
//	if(!DocLists.containsKey(string_array.get(i)))
//	{
//		
//	
//		FindIterable<Document> iterDoc = indexercol.find(Filters.eq("Word",string_array.get(i)));
//		MongoCursor<Document> it = iterDoc.iterator();
//		while(it.hasNext())
//		{
//			Document doc = it.next();
//			ArrayList<Document> list = DocLists.get(string_array.get(i));
//			list.add(doc);
//			DocLists.put(string_array.get(i), list);
//		}
//	}
//	
//}