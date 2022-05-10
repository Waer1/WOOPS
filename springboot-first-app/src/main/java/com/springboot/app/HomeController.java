package com.springboot.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Evaluator.ContainsText;
import org.jsoup.select.Selector;
//import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import ca.rmen.porterstemmer.PorterStemmer;

@RestController
public class HomeController {

	private static String in = "computer architecture";
	static HashMap<String,ArrayList<Document>> ReterivedDocuments;
	//static HashMap.Entry<String,ArrayList<Document>> entry;
	static ArrayList<Document> docs;
	private static ArrayList<Double> tf_idf = new ArrayList<Double>();
	private static HashMap<String,Double>out = new HashMap<String,Double>();
	private static int TotalPages = 1000;
	//public static int number_documents;
//	@RequestMapping(value="/")
//	public String greeting()
//	{
//		return "index.html";
//	}
	@RequestMapping(value="/page/")
	public String mainpage()
	{
		return  "index.html";
	}
//	@GetMapping(value="/{name}")
//	public String greetingwithname(@PathVariable String name)
//	{
//		//MongoDatabase db
//		//MongoCollection<Document> col;
//		return "Helloooo "+name;
//	}
//	@GetMapping(value="/test/{query}")
//	public ArrayList<String> test(@PathVariable String query)
//	{
//		ArrayList<String> string_array = Remove_tags(query);
//		string_array = Remove_Stop_Words(string_array);
//		string_array = Stemming(string_array);
//		System.out.println("no Error sabry");
//		return string_array;
//	}
	@GetMapping(value="/data/{query}")
	public ArrayList<Document> DatabaseResponse(@PathVariable String query)
	{
		String Copy_query = new String (query);
		ArrayList<String> Copy_String_list= copy(Copy_query);
		ArrayList<String> urls = new ArrayList<String>();
		//urls.add("https://en.wikipedia.org/");
		//urls.add("https://en.wikipedia.org/");
		//HashMap<String,ArrayList<Document>> ReterivedDocuments = Query_Process (query,urls);
		ReterivedDocuments = Query_Process (query,urls);
		ArrayList<Document> JSON_Data = new ArrayList<Document>();
		// call phrase search
		
		
		
		// call ranker 
		long start_ranker = System.currentTimeMillis();
		urls = TF_IDF();
		long end_ranker = System.currentTimeMillis();
		System.out.println("Time of ranker = "+(end_ranker-start_ranker));
		
		
		
		
		// merge urls
		
		
		// get  title and description for each document ------ get from database or parse .
		// we have array list of urls(strings)

		
		int no_urls = urls.size();
		System.out.println("urls retreived = "+no_urls);
		if(no_urls>20)
		{
			no_urls = 20;
		}
		long start = System.currentTimeMillis();
		// some time passes
		
		for(int i =0;i<no_urls;i++)
		{
			long start_1 = System.currentTimeMillis();
			// get data of url
			long start_jsoup = System.currentTimeMillis();
			org.jsoup.nodes.Document doc = null;
			boolean continue_execution = true;
			try {
				doc = Jsoup.connect(urls.get(i)).get();
	
			} catch (Exception e) {
				System.out.println("Error in connection ");
				continue_execution = false;
			}
			long end_jsoup = System.currentTimeMillis();
			System.out.println("Time of jsoup = "+(end_jsoup-start_jsoup));
			//String temp = doc.select("*").toString();
			//temp = Jsoup.clean(temp, Whitelist.none());
			//System.out.println(temp);
			// get document title
			if(!continue_execution)
			{
				continue;
			}
			String TITLE = doc.select("title").toString();
			
			
			TITLE = Jsoup.clean(TITLE, Whitelist.none());

			// get description 
			String description = "no description";
			for(int j =0;j<Copy_String_list.size();j++)
			{
				
				// check headers & paragraphs
				if( doc.select("h1:contains("+Copy_String_list.get(j)+")").toString() != "")
				{
					description = doc.select("h1:contains("+Copy_String_list.get(j)+")").toString();
					
					break;
				}
				else if(doc.select("h2:contains("+Copy_String_list.get(j)+")").toString() != "")
				{
					description = doc.select("h2:contains("+Copy_String_list.get(j)+")").toString();		
					break;
				}
				else if(doc.select("h3:contains("+Copy_String_list.get(j)+")").toString() != "")
				{
					description = doc.select("h3:contains("+Copy_String_list.get(j)+")").toString();
					break;
				}
				else if(doc.select("h4:contains("+Copy_String_list.get(j)+")").toString() != "")
				{
					description = doc.select("h4:contains("+Copy_String_list.get(j)+")").toString();
					break;
				}
				else if(doc.select("h5:contains("+Copy_String_list.get(j)+")").toString() != "")
				{
					description = doc.select("h5:contains("+Copy_String_list.get(j)+")").toString();
					break;
				}
				else if(doc.select("body:contains("+Copy_String_list.get(j)+")").toString() != "")
				{
					description = doc.select("body:contains("+Copy_String_list.get(j)+")").first().toString();
					System.out.println("body");
					break;
				}
			}
			description = Jsoup.clean(description, Whitelist.none());
			
			//description = doc.select("p:contains("+"dagbanli"+")").toString();
			//description = doc.select("p:first-of-type").toString();
			//System.out.println("Des: "+description);
			//description = doc.select("free").toString();
			
			Document json_doc  = new Document("URL",urls.get(i));
			json_doc.append("TITLE",TITLE);
			if(description.length() > 100)
			{
				json_doc.append("DESCRIPTION",description.substring(0, 99));
			}
			else
			{
				json_doc.append("DESCRIPTION",description);
			}
			
			JSON_Data.add(json_doc);
			long end_1 = System.currentTimeMillis();
			System.out.println("Time of 1 documents = "+(end_1-start_1));
		}
		
		long end = System.currentTimeMillis();
		long elapsedTime = end - start;
		System.out.println("Time of 20 documents = "+elapsedTime);
		return JSON_Data;
	}
	public ArrayList<String>copy(String query)
	{
		ArrayList<String> string_array = Indexer.Remove_tags(query);
		System.out.println("no Error sabry");
		string_array = Indexer.Remove_Stop_Words(string_array);
		return string_array;	
	}
	public HashMap<String,ArrayList<Document>> Query_Process (String query,ArrayList<String> urls)
	{
		
		ArrayList<String> string_array = Indexer.Remove_tags(query);
		//System.out.println("no Error sabry");
		string_array = Indexer.Remove_Stop_Words(string_array);
		//System.out.println("no Error sabry");
		string_array = Indexer.Stemming(string_array);
		//System.out.println("no Error sabry");
		// connection with atlas
		MongoDatabase indexerdb = get_database("Search_index", "mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
		MongoCollection<Document> indexercol = get_collection(indexerdb, "invertedfile");
		//number_documents = Indexer.countUniqueDoc(indexercol);
		TotalPages = Indexer.countUniqueDoc(indexercol);
		
		// connection with local
		
		//MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
		//MongoDatabase indexerdb = mongoClient2.getDatabase("Search_index");
		//MongoCollection<Document> indexercol = Indexer.get_collection(indexerdb, "invertedfile");
		
		//FindIterable<Document> iterDoc = indexercol.find(Filters.eq("Word",""));
		
		//HashMap<String,ArrayList<Document>> DocLists = new HashMap<String,ArrayList<Document>>();
		HashMap<String,ArrayList<Document>> DocLists = new LinkedHashMap<String,ArrayList<Document>>();
		for(int i=0;i<string_array.size();i++)
		{
			//System.out.println("no Error sabry");
			if(!DocLists.containsKey(string_array.get(i)))
			{
				
			
				FindIterable<Document> iterDoc = indexercol.find(Filters.eq("Word",string_array.get(i)));
				MongoCursor<Document> it = iterDoc.iterator();
				//System.out.println("no Error sabry");
				while(it.hasNext())
				{
					Document doc = it.next();
					
					if(doc == null)
					{
						//System.out.println("Error sabry");
					}
					if(DocLists.get(string_array.get(i)) == null)
					{
						ArrayList<Document> list = new ArrayList<Document>();
						list.add(doc);
						DocLists.put(string_array.get(i), list);
						urls.add(doc.getString("DOC_ID"));
					}
					else
					{
						ArrayList<Document> list = DocLists.get(string_array.get(i));
						list.add(doc);
						DocLists.put(string_array.get(i), list);
						urls.add(doc.getString("DOC_ID"));
					}

					//System.out.println("no Error sabry");
				}
				System.out.println("size document list"+DocLists.get(string_array.get(i)).size());
			}
			else
			{
				
			}
		}
		return DocLists;
	}
	// Ranker Code 
	public static double IDF()
	{
		return Math.log10((double)TotalPages/docs.size());
	}
	
	public static ArrayList<String> TF_IDF()
	{	
			//entry = map.entrySet().iterator().next();
		for (ArrayList<Document> entry : ReterivedDocuments.values())
		{	
			docs = entry;
			for (Document it : docs)		
			{
				//TODO:Calculate the tf of each doc by the formula: tf = 10*title + 5*header + body
				int title = it.getInteger("Title");
				int headers = it.getInteger("Headers");
				int body = it.getInteger("Other");
				String s = it.getString("DOC_ID");
				
				int tf = 10*title + 5*headers + body;
				//TODO:Calculate the normalized tf
				Double ntf = 1.0/tf;
				
				//TODO:Calculate TF*IDF
				double temp = ntf*IDF();
				Double d = out.get(s);
				if (d != null)
				{
					temp += d;
					out.remove(s);
				}
				out.put(s, temp);
			}
		}
		// Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
               new LinkedList<Map.Entry<String, Double> >(out.entrySet());
 
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
         
        // put data from sorted list to array of strings
        ArrayList<String> finale = new ArrayList<String>();
        for (Map.Entry<String, Double> aa : list) {
            //System.out.println(aa.getKey() + " | " + aa.getValue());
        	finale.add(aa.getKey());
        }
        return finale;
	}
	public MongoDatabase get_database(String databasename, String connection) {

//		 MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
//		 MongoDatabase db = mongoClient2.getDatabase(databasename);
//		 return db;

		ConnectionString connectionString = new ConnectionString(connection);
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase(databasename);
		return database;
	}

	// function get collection from database

	public  MongoCollection<Document> get_collection(MongoDatabase db, String Collection_Name) {

		MongoCollection<Document> col = db.getCollection(Collection_Name);
		return col;
	}
//	public static ArrayList<String> Remove_tags(String s1) {
//
//		// s2 +="احمد ثبري";
//		// s1 +="احمد ثبري";
//		// ---------------------------------------------------------------
//
//		// remove tags
//		s1 = Jsoup.clean(s1, Whitelist.none());
//		// s2 = Jsoup.clean(s2, Whitelist.none());
//		// ----------------------------------------------------------------
//		// remove all characters except English alphabets & numbers
//		// Note : should ask Eng. Ali about removing those characters
//		// s1 = s1.replace(".", " ");
//		// s1 = s1.replaceAll("\\p{Punct}", " ");
//		s1 = s1.replaceAll("[^a-zA-Z0-9]", " ");
//		// s1 = s1.replaceAll("[^\\p{InArabic}\\s]", " ");
//		// s1 += s2;
//		// s1 = s1.replaceAll("[^a-zA-Z]", " ");
//		// -----------------------------------------------------------------
//		// test code here
////		String strArray[] = s1.split(" ");
////		System.out.println(strArray.length);
////		for(int i=0;i<strArray.length;i++)
////		{
////			System.out.println(strArray[i]);
////
////		}
//
//		// -------------------------------------------------------------------
//		// split string to array of strings
//		StringTokenizer str_tokenizer = new StringTokenizer(s1);
//
//		ArrayList<String> string_array = new ArrayList<String>(str_tokenizer.countTokens());
//		// Add tokens to our array
//
//		while (str_tokenizer.hasMoreTokens()) {
//			string_array.add(str_tokenizer.nextToken());
//		}
//		// return array of strings
//		return string_array;
//	}
//
//	// function to remove stop words
//	public static ArrayList<String> Remove_Stop_Words(ArrayList<String> str) {
//		// -------------------------------------------------------
//		// Load file into list
//		ArrayList<String> stoparr = new ArrayList<String>();
//		try {
//			File myObj = new File("stop_words_english.txt");
//			Scanner myReader = new Scanner(myObj);
//			while (myReader.hasNextLine()) {
//				String data = myReader.nextLine();
//				// System.out.println(data);
//				stoparr.add(data);
//			}
//			myReader.close();
//		} catch (FileNotFoundException e) {
//			System.out.println("An error occurred.");
//			e.printStackTrace();
//		}
//		// ----------------------------------------------------------------------
//		// seocnd remove stop words from String
//		// string should be filtered from tags & punctuation
//		int x = str.size();
//		str.removeAll(stoparr);
//		// String result = str.stream().collect(Collectors.joining(" "));
//		// assertEquals(result, target);
////		for (int i = 0; i < str.size(); i++) {
////			System.out.println(str.get(i));
////
////		}
////		System.out.println(str.size());
////		System.out.println(x);
//		return str;
//
//	}
//
//	// stemming words using port stemmer library
//	public static ArrayList<String> Stemming(ArrayList<String> string_array) {
//		PorterStemmer stemmer = new PorterStemmer();
//		for (int i = 0; i < string_array.size(); i++) {
//			String temp = new String(string_array.get(i));
//
//			string_array.set(i, stemmer.stemWord(string_array.get(i)));
////			if (!temp.equals(string_array.get(i))) {
////				System.out.println(temp + "-" + string_array.get(i)); // print stemmed word & word
//////				if(string_array.get(i).equals("احمد"))
//////				{
//////					break;
//////				}
////			}
//		}
//
//		return string_array;
//	}


}
