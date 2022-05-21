package com.springboot.app;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator.ContainsText;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
//import com.mongodb.MongoClientURI;
import com.mongodb.client.DistinctIterable;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.UnknownError;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//@SuppressWarnings("deprecation")
public class Indexer implements Runnable{

	/////////////////////////////////////////////////////////////////////////
	// some global data that will be used in all functions 
	static Map<String, Integer> HashForDf = new HashMap<>();
	static HashMap<String,Integer> url_indexer = new HashMap<String, Integer>();
	final static String indexer_database_connection = "mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	final static String crawler_database_connection = "mongodb+srv://Waer:RHhDdESAKY5HvzZ@cluster0.jafiz.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	final static MongoDatabase Crawlerdb = get_database("Crawler", crawler_database_connection);	
	final static MongoCollection<Document> crawlercol = get_collection(Crawlerdb,"Crawler");
	final static int size_Docs = (int)crawlercol.countDocuments();
	final static MongoDatabase Indexerdb = get_database("Search_index", indexer_database_connection);
	static MongoCollection<Document> indexercol = get_collection(Indexerdb, "invertedfile");
	/////////////////////////////////////////////////////////////////////////

	static int NumberOfThreads = 8 ; 
	public void run() {
		// todo -> to be implemented to multiThreading
//		System.out.println();

		System.out.println("Thread " + Thread.currentThread().getName() + "is now Running");
		run_indexer(NumberOfThreads); 
	}
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//Logger mongoLogger = Logger.getLogger( "com.mongodb.driver" ); mongoLogger.setLevel(Level.SEVERE);
		long start_1 = System.currentTimeMillis();
		System.out.println(crawlercol.countDocuments());
		// should be done only once so the main thread should be the one who done it 
		setindexes(indexercol);
		url_inverted(indexercol);
		/////////////////////////////
		ArrayList<Thread> Threads= new ArrayList<>() ; 
		for(Integer i = 0 ; i < 8 ; i++)
		{
			Thread t = new Thread( new Indexer()); 
			t.setName(i.toString());
			Threads.add(t); 
		}
		
		System.out.println("Starting");
		for(int i = 0 ; i < Threads.size() ; i ++)
			Threads.get(i).start();


		System.out.println("Joining");
		for(int i = 0 ; i < Threads.size() ; i ++)
			Threads.get(i).join();
		
		// lazm a3ml join 3la kol el threads b7es en el main bs hya el te3ml call lel function de
		System.out.println("Updating the IDF");
		UpdateIDF(HashForDf,indexercol,size_Docs);

		System.out.println("End Main");
		long end_1 = System.currentTimeMillis();
		System.out.println("Time of Indexer "+(end_1-start_1));


////		
//		// ---------------------------------------------------------------
//		// what to do
//		// 1) get a documents from database
//		// 2) remove metadata from document or string or array of strings
//		// 3) remove stopwords from string
//		// 4) count # of words in string or length of array of strings
//		// 5) stemming words
//		// 6) use hash map to insert data from strings about each word
//		// 7) insert data in database
//		// 8) repeat process until you finish all documents
//



	}

	public static void run_indexer(int numberofThreads) {
		// ------------------------------ get database of links & documents
		FindIterable<Document> iterDoc = crawlercol.find();
		MongoCursor<Document> it = iterDoc.iterator();
		boolean stop_indexeing = false;
		int SizeForEachThread= size_Docs / numberofThreads ; 
		int start = Integer.parseInt(Thread.currentThread().getName()); 
		int begin = 0;
		int end = 0;
		
		if(start != 7)
		{
			begin = start * SizeForEachThread ;
			end = (start + 1) * SizeForEachThread;
		}
		else
		{
			begin = start * SizeForEachThread;
			end = size_Docs;
		}
		
		// moving the iterator to the beginning position for each thread 
		for(int i=0;i<begin;i++)
			it.next();
		
		
		for (int i = begin ; i < end  ; i++) {
			// retrieve document
			boolean canfetch = true;
			Document crawlerdoc =null;
			while(!it.hasNext())
				stop_indexeing = true;
			
			if(stop_indexeing)
				break;
			
			crawlerdoc = it.next();
			
			// check if document needed to be indexed or not from crawler

			
			org.jsoup.nodes.Document htmldoc = Jsoup.parse( crawlerdoc.getString("html"));
			String htmldoc_ID = crawlerdoc.getString("Url");
				// if url found in hashmap delete it to store new link else store directly
				if(url_indexer.containsKey(htmldoc_ID))
				{
					delete_doc_ID(indexercol,htmldoc_ID);
				}
				else
				{
					url_indexer.put(htmldoc_ID, 1);
				}
				// filter documents
				// detect tags for ranker
				String s1 = htmldoc.select("*").text();
				ArrayList<String> string_array = Remove_tags(s1);
				String title = htmldoc.select("title").text();
				String headers = htmldoc.select("h1").text();
				headers += " " + htmldoc.select("h2").text();
				headers += " " + htmldoc.select("h3").text();
				headers += " " + htmldoc.select("h4").text();
				headers += " " + htmldoc.select("h5").text();
				headers += " " + htmldoc.select("h6").text();
	
				ArrayList<String> headers_array = Remove_tags(headers);
				ArrayList<String> title_array = Remove_tags(title);
	
				// remove stop words from file
				// array of strings
				string_array = Remove_Stop_Words(string_array);
				headers_array = Remove_Stop_Words(headers_array);
				title_array = Remove_Stop_Words(title_array);
				
				string_array = Stemming(string_array);
				headers_array = Stemming(headers_array);
				title_array = Stemming(title_array);
				Map<String, Integer> hash_headers = new HashMap<String, Integer>();
				Map<String, Integer> hash_title = new HashMap<String, Integer>();
				hash_headers = hashtags(hash_headers, headers_array);
				hash_title = hashtags(hash_title, title_array);
	
				Map<String, ArrayList<Integer>> hashtable = FileOrgan(string_array);
				ArrayList<Document> listofdocs = createdocuments(hashtable,htmldoc_ID,hash_title,hash_headers,string_array.size());
				
				try {
					System.out.println("------------------------------------------------------");
					insertdocs(indexercol,listofdocs);
				} catch (Exception e) {
					System.out.println(e);
				}
				System.out.println("Document "+i+" is indexed , Remaining Documents = "+(end-i-1));
			

		}
	}

	// function get database from mongodb server
	// Note should be modified when connect to atlas

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

	// Function to clean up the documents
	public static ArrayList<String> Remove_tags(String s1) {

		// remove tags
		s1 = Jsoup.clean(s1, Whitelist.none());
		s1 = Jsoup.clean(s1, Whitelist.none());
		// ----------------------------------------------------------------
		// remove all characters except English alphabets & numbers
		// Note : should ask Eng. Ali about removing those characters

		s1 = s1.replaceAll("[^a-zA-Z0-9]", " ");
		// s1 = s1.replaceAll("[^\\p{InArabic}\\s]", " ");
		// s1 += s2;
		// s1 = s1.replaceAll("[^a-zA-Z]", " ");
		// -----------------------------------------------------------------
		// -------------------------------------------------------------------
		// split string to array of strings
		StringTokenizer str_tokenizer = new StringTokenizer(s1);

		ArrayList<String> string_array = new ArrayList<String>(str_tokenizer.countTokens());
		// Add tokens to our array

		while (str_tokenizer.hasMoreTokens()) {
			string_array.add(str_tokenizer.nextToken());
		}
		// return array of strings
		return string_array;
	}

	// function to remove stop words
	public static ArrayList<String> Remove_Stop_Words(ArrayList<String> str) {
		// -------------------------------------------------------
		// Load file into list
		ArrayList<String> stoparr = new ArrayList<String>();
		try {
			File myObj = new File("stop_words_english.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				// System.out.println(data);
				stoparr.add(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		// ----------------------------------------------------------------------
		// seocnd remove stop words from String
		// string should be filtered from tags & punctuation
		int x = str.size();
		str.removeAll(stoparr);
		return str;

	}

	// stemming words using port stemmer library
	public static ArrayList<String> Stemming(ArrayList<String> string_array) {
		PorterStemmer stemmer = new PorterStemmer();
		for (int i = 0; i < string_array.size(); i++) {
			String temp = new String(string_array.get(i));
			string_array.set(i, stemmer.stemWord(string_array.get(i)));
		}

		return string_array;
	}

	public static Map<String, ArrayList<Integer>> FileOrgan(ArrayList<String> Words) {
		// -------------------------------------------------------
		Map<String, ArrayList<Integer>> hash = new HashMap<>(); // String : [TF ,IDF ,positions]
		// here we put the path of the folder which contains file pathes
		int pos = 0; // to count the position of the Word in the Document
		for (String string : Words) {

			if (hash.containsKey(string)) { // exists before?
				hash.get(string).set(0, (Integer) hash.get(string).get(0) + 1); // increment the tf
				hash.get(string).add(pos); // add the position to the end of the list
				pos++; // increment the position
			} else { // first time ?
				// increment it in the hash2
				synchronized(HashForDf)
				{
					if (HashForDf.containsKey(string)) // existed before in the IDF list
						HashForDf.put(string, HashForDf.get(string) + 1);
					else // first time
						HashForDf.put(string, 1);					
				}

				ArrayList<Integer> A = new ArrayList<>(3);
				A.add(1); // set TF to 1
				A.add(1); // set IDF to 1
				A.add(pos); // add the position to the end
				pos++; // increment the pos
				hash.put(string, A);
			}
		}
		return hash;
	}
	
	   public static void UpdateIDF(Map<String,Integer> hash, MongoCollection<Document> col ,int size) {
		   System.out.println("Update IDF ");
	        for (String s : hash.keySet())
	        {
	            //Double idf =  Math.log(size / HashForDf.get(s)) ;
	            int idf = HashForDf.get(s);
	            col.updateMany(Filters.eq("Word",s), Updates.set("IDF", idf));
	        }
	        System.out.println("Finished Update IDF ");
	    }
	    
	////////////////////////////////
	// create indexes on "WORD" & "DOC_ID" -> like composite key & ascending order
	public static void setindexes(MongoCollection<Document> col) {
		// col.drop();
		try {
		IndexOptions indexOptions = new IndexOptions().unique(true);
		String resultCreateIndex = col.createIndex(Indexes.ascending("Word", "DOC_ID"), indexOptions);
		System.out.println(String.format("Index created: %s", resultCreateIndex));
		}
		catch(Exception e)
		{
			System.out.println("Collection already indexed");
		}
	}

	// convert from hashmap to document to be stored in database
	public static ArrayList<Document> createdocuments(Map<String, ArrayList<Integer>> inverteddocs,
			String doc_id, Map<String, Integer> hash_title, Map<String, Integer> hash_headers,int size_doc) {
		ArrayList<Document> listofdocs = new ArrayList<Document>();

		for (String i : inverteddocs.keySet()) {
			ArrayList<Integer> arr = inverteddocs.get(i);
			Document doc1 = new Document("Word", i);
			doc1.append("DOC_ID", doc_id);
			doc1.append("TF", arr.get(0)/(double)size_doc);
			doc1.append("IDF", arr.get(1));
			arr.remove(0);
			arr.remove(0);
			doc1.append("POSITION", arr);
			int no_headers = 0;
			if (hash_headers.get(i) != null) {
				no_headers = hash_headers.get(i);
			}
			int no_titles = 0;
			if (hash_title.get(i) != null) {
				no_titles = hash_title.get(i);
			}
			doc1.append("Headers", no_headers);
			doc1.append("Title", no_titles);
			doc1.append("Other", arr.size() - no_headers - no_titles);
			listofdocs.add(doc1);
		}
		return listofdocs;
	}

	// insert array of documents that belong to template(document1)
	public static void insertdocs(MongoCollection<Document> col, ArrayList<Document> listofdocs) {
		try {
			col.insertMany(listofdocs);
		} catch (Exception e) {
			System.out.println("Handled Expection");
			System.out.println(e);
		}
	}

	// delete updated document in crawler from search_index to insert new one
	public static void delete_doc_ID(MongoCollection<Document> col, String url) {
		// col.deleteMany(Filters.eq("DOC_ID",doc_id));
		col.deleteMany(Filters.eq("DOC_ID", url));
	}

	// gets unique urls stored in indexer to know if crawler urls is new or pre-stored in database
	public static void  url_inverted(MongoCollection<Document> col)
	{
		//HashMap<String,Integer> url_indexer = new HashMap<String, Integer>();
		DistinctIterable<String> doc = col.distinct("DOC_ID", String.class);
		MongoCursor<String> unique_urls = doc.iterator();
			while(unique_urls.hasNext())
			{
				String str = unique_urls.next();
				System.out.println(str);
				url_indexer.put(str, 1);
			}
		//return url_indexer;
	}
	public static Map<String, Integer> hashtags(Map<String, Integer> MetaData, ArrayList<String> arr) {
		for (int i = 0; i < arr.size(); i++) {
			if (MetaData.get(arr.get(i)) == null) {
				MetaData.put(arr.get(i), 1);
			} else {
				int x = MetaData.get(arr.get(i));
				x++;
				MetaData.put(arr.get(i), x);
			}

		}
		return MetaData;

	}
	public static int countUniqueDoc(MongoCollection<Document> col)
	{
		DistinctIterable<String> doc = col.distinct("DOC_ID", String.class);
		MongoCursor<String> results = doc.iterator();
		int count =0;
		while(results.hasNext())
		{
			results.next();
			count++;
		}
		return count ;
	}

}


//public static ArrayList<String> filter(String path) {
//	ArrayList<String> Words = new ArrayList<String>();
//	try {
//		// here we should put the path of the file from which we read the input
//		File myObj = new File(path);
//		Scanner myReader = new Scanner(myObj);
//		while (myReader.hasNextLine()) {
//			String data = myReader.nextLine();
//			// System.out.println(data);
//			Words.add(data);
//		}
//		myReader.close();
//	} catch (FileNotFoundException e) {
//		System.out.println("An error occurred.");
//		e.printStackTrace();
//	}
//	ArrayList<String> string_array = new ArrayList<>();
//
//	int size = Words.size();
//	for (int i = 0; i < size; i++) {
//		StringTokenizer str_tokenizer = new StringTokenizer(Words.get(i));
//		// Add tokens to our array
//		while (str_tokenizer.hasMoreTokens()) {
//			string_array.add(str_tokenizer.nextToken());
//		}
//	}
//
//	return string_array;
//}

//public static void FileOrganizer() {
//Map<String, Vector<Integer>> hash = new HashMap<>();
//Vector<Integer> v = new Vector<Integer>();
//v.add(12);
//hash.get("ahmed");
//
//for (Map.Entry pairEntry : hash.entrySet()) {
//	Vector<Integer> v1 = (Vector<Integer>) pairEntry.getValue();
//	System.out.println(pairEntry.getKey() + " : " + v1.elementAt(0));
//}
//}