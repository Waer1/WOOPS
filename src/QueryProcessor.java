import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.Map;

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
		Map <String , ArrayList<Integer>> mp = new HashMap<>() ; 
		mp.put("Ahmed" , new ArrayList<Integer>());
		mp.get("Ahmed").add(1); 
		System.out.print(mp);
		return ;
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
	
	
	public static ArrayList<String> phraseSearch(HashMap<String,ArrayList<Document>>myMap){
		
		// 34an ana msh 3auzo ye3dl fl trteb
		HashMap<String , ArrayList<Document>> UrlMap =  new LinkedHashMap<String,ArrayList<Document>>() ;
		
		Iterator<Entry<String , ArrayList<Document>>> myIterator = myMap.entrySet().iterator() ;
		
		//iterating over the main map 
		while(myIterator.hasNext())
		{
			// accessing the element which the iterator points at
			Map.Entry<String, ArrayList<Document>> newMap = myIterator.next(); 
			
			//iterating over its whole documents and storing it in the new hashmap
			for(int i = 0 ; i < newMap.getValue().size(); i ++)
			{
				// check if the url exists before in our map 
				var check = UrlMap.get(newMap.getValue().get(i).getString("DOC_ID")); 
				if(check != null) // if yes add this document to it
					UrlMap.get(newMap.getValue().get(i).getString("DOC_ID")).add(newMap.getValue().get(i)); 
				else // create new place to it and add the document in the List 
				{
					UrlMap.put(newMap.getValue().get(i).getString("DOC_ID"),new ArrayList<Document>()); 
					UrlMap.get(newMap.getValue().get(i).getString("DOC_ID")).add(newMap.getValue().get(i)); 
				}
			}			
		}
		
		// to store the positions and work on them 
		// brdu msh 3auz a3dl fl trteb
		HashMap<String , ArrayList<Integer>> Positions =  new LinkedHashMap<String,ArrayList<Integer>>(); 
		
		
		// iterate over the URLMap
		myIterator = UrlMap.entrySet().iterator(); 
		
		
		ArrayList<String> URLContainer = new ArrayList<> (); // hya de el ha7ot feha elly l2eto 
		
		while (myIterator.hasNext())
		{
			Map.Entry<String, ArrayList<Document>> newMap = myIterator.next(); 
			int size = newMap.getValue().size() ; 
			if(size== myMap.size()) // if the URL has number of document = number of elements which we search on it 
			{
				// iterating over all elements and store their positions
				for(int i = 0 ; i < size ; i ++)
				{
					// ana msh mot2kd mn mwdo3 el casting da bs atmna eno yeshtghl
					Positions.put(newMap.getValue().get(i).getString("Word"), 
							(ArrayList<Integer>)newMap.getValue().get(i).getList("POSITION",
									Integer.class));					
				}
				
				Iterator<Entry<String , ArrayList<Integer>>> IT = Positions.entrySet().iterator() ;
				Map.Entry<String, ArrayList<Integer>> FirstRow = IT.next(); 
				
				// hena b5ly shoghly dayman 3la awl kelma fl searching  w bdwr 3ala el sequance menha baa
				for(int j = 0 ; j < size ; j++) {
					if(compare (size , FirstRow.getValue().get(j) , IT, 2)); // el lvlIndc = 2 34an lw el size = 4 ab2a akny shaghal 1 based fa na habd2 mn level 2
						{
							// check for at least 1 lw l2et 
							// add the URl to the founded
							URLContainer.add(newMap.getKey());
							break  ; 
						}
				}
			}
		}
		
		return URLContainer; 
	}
	
	public static Boolean compare (int size , Integer number , Iterator<Entry<String , ArrayList<Integer>>> IT, int LvlInd)
	{
		// base case 
		if(LvlInd == size) { // last level 
			Map.Entry<String, ArrayList<Integer>> FirstRow = IT.next(); 
			for(int i = 0 ; i < FirstRow.getValue().size(); i++)
			{
				if(FirstRow.getValue().get(i) == number + 1 )
					return true ; // found the last index 
				else if (FirstRow.getValue().get(i) > number ) return false ; 
			}
			return false ;
		}
		
		// found the sequence 
		Boolean found = false ;
		Map.Entry<String, ArrayList<Integer>> FirstRow = IT.next(); 
		for(int i = 0 ; i < FirstRow.getValue().size(); i++)
		{
			if(FirstRow.getValue().get(i) == number + 1 )
			{
				// ana msh 3arf 7war el hasnext de hya el bt5ly el iterator yet7rk wla a lw hya fa yeb2a keda el shoghl tmaam
				IT.hasNext();
				found = compare(size , number + 1 , IT,LvlInd + 1) ; // found the last index 
			}
			else if (FirstRow.getValue().get(i) > number ) return false ; 
			
			if(found == true) return true;
		}
		

		
		return found;
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