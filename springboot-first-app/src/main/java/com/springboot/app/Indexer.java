package com.springboot.app;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

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

//@SuppressWarnings("deprecation")
public class Indexer {

	static Map<String, Integer> HashForDf = new HashMap<>();

	final static String indexer_database_connection = "mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
	final static String crawler_database_connection = "mongodb+srv://Waer:RHhDdESAKY5HvzZ@cluster0.jafiz.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";

	public static void FileOrganizer() {
		Map<String, Vector<Integer>> hash = new HashMap<>();
		Vector<Integer> v = new Vector<Integer>();
		v.add(12);
		hash.get("ahmed");

		for (Map.Entry pairEntry : hash.entrySet()) {
			Vector<Integer> v1 = (Vector<Integer>) pairEntry.getValue();
			System.out.println(pairEntry.getKey() + " : " + v1.elementAt(0));
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Hello");
		
		run_indexer();
		
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
//		// ---------------------------------------------------------------
//		// step: 0
//		// try getting a document from jsoup for testing until crawler is ready
//
//		// -----------------------------uncomment------------------------------
//		MongoDatabase db = get_database("test", indexer_database_connection);
//		MongoCollection<Document> col = get_collection(db, "ahmed");
//		org.jsoup.nodes.Document doc = null;
//		try {
//			doc = Jsoup.connect("https://en.wikipedia.org/").get();
//
//		} catch (Exception e) {
//			System.out.println("Error in connection ");
//		}
//		// ------------------------------------------------------------------
//
//		// remove metadata & punctuation
//
//		//
//		String s1 = doc.toString();
//		ArrayList<String> string_array = Remove_tags(s1);
//		// remove stop words from file
//		// array of strings
//
//		string_array = Remove_Stop_Words(string_array);
//		System.out.println("------------------------------------");
//
//		for (int i = 0; i < string_array.size(); i++) {
//			// System.out.println(string_array.get(i));
//		}
//		string_array = Stemming(string_array);
//
//		// -----------------------------uncomment------------------------------
//		Map<String, ArrayList<Integer>> hashtable = FileOrgan(string_array);
//		ArrayList<Document> listofdocs = createdocuments(hashtable, 1);
//		col.insertMany(listofdocs);

	}

	public static void run_indexer() {
		// ------------------------------ get database of links & documents
		MongoDatabase Crawlerdb = get_database("Crawler", crawler_database_connection);	
		MongoCollection<Document> crawlercol = get_collection(Crawlerdb,"Crawler");
		int size_Docs = (int)crawlercol.countDocuments();
		System.out.println(crawlercol.countDocuments());
		//ArrayList<Document> docs = crawlercol.find();
		FindIterable<Document> iterDoc = crawlercol.find();
		MongoCursor<Document> it = iterDoc.iterator();
		//Document doc = it.next();
		//System.out.println(doc);
		
		// ------------------------------ get collection of documents & size

		// ------------------------------- loop on documents

		//MongoClient mongoClient2 = MongoClients.create("mongodb://localhost:27017");
		//MongoDatabase Indexerdb = mongoClient2.getDatabase("Search_index");
		MongoDatabase Indexerdb = get_database("Search_index", indexer_database_connection);
		MongoCollection<Document> indexercol = get_collection(Indexerdb, "invertedfile");
		setindexes(indexercol);
		boolean stop_indexeing = false;
		for (int i = 0; i < size_Docs; i++) {
			// retrieve document
			boolean canfetch = true;
			Document crawlerdoc =null;
//			//2471
//			while(i<0)
//			{
//				crawlerdoc = it.next();
//				i++;
//			}
			while(!it.hasNext())
			{
				stop_indexeing = true;
			}
			if(stop_indexeing)
			{
				break;
			}
			crawlerdoc = it.next();
			
			// check if document needed to be indexed or not from crawler

			
			org.jsoup.nodes.Document htmldoc = Jsoup.parse( crawlerdoc.getString("html"));
//			System.out.println("doc "+htmldoc);
//			try {
//				Thread.currentThread().sleep(5000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			String htmldoc_ID = crawlerdoc.getString("Url");
			


			if(canfetch)
			{
				//delete_doc_ID(indexercol,htmldoc_ID,0);
	
				// filter documents
				// detect tags for ranker
				//String s1 = htmldoc.toString();
				String s1 = htmldoc.select("*").text();
				ArrayList<String> string_array = Remove_tags(s1);
				String title = htmldoc.select("title").text();
				String headers = htmldoc.select("h1").text();
				headers += " " + htmldoc.select("h2").text();
				headers += " " + htmldoc.select("h3").text();
				headers += " " + htmldoc.select("h4").text();
				headers += " " + htmldoc.select("h5").text();
				headers += " " + htmldoc.select("h6").text();
				
				// String paragraphs = doc.select("p").text();
	
				ArrayList<String> headers_array = Remove_tags(headers);
				ArrayList<String> title_array = Remove_tags(title);
				// ArrayList<String> paragraphs_array = Remove_tags(paragraphs);
	
				// remove stop words from file
				// array of strings
				string_array = Remove_Stop_Words(string_array);
				headers_array = Remove_Stop_Words(headers_array);
				title_array = Remove_Stop_Words(title_array);
				// paragraphs_array = Remove_Stop_Words(paragraphs_array);
				System.out.println("------------------------------------------------------");
	
				for (int j = 0; j < string_array.size(); j++) {
					// System.out.println(string_array.get(j));
				}
				string_array = Stemming(string_array);
				headers_array = Stemming(headers_array);
				title_array = Stemming(title_array);
				// paragraphs_array = Stemming(paragraphs_array);
	
				Map<String, Integer> hash_headers = new HashMap<String, Integer>();
				Map<String, Integer> hash_title = new HashMap<String, Integer>();
				// Map<String, Integer> hash_paragraphs = new HashMap<String, Integer> ();
				hash_headers = hashtags(hash_headers, headers_array);
				hash_title = hashtags(hash_title, title_array);
				// hash_paragraphs = hashtags(hash_paragraphs,paragraphs_array);
	
				Map<String, ArrayList<Integer>> hashtable = FileOrgan(string_array);
				ArrayList<Document> listofdocs = createdocuments(hashtable,htmldoc_ID,hash_title,hash_headers,string_array.size());
				
				try {
					
					insertdocs(indexercol,listofdocs);
					 //indexercol.insertMany(listofdocs);
				} catch (Exception e) {
					System.out.println(e);
				}
				System.out.println("Document "+i+" is indexed , Remaining Documents = "+(size_Docs-i-1));
			}

		}
		UpdateIDF(HashForDf,indexercol,size_Docs);


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

		// s2 +="احمد ثبري";
		// s1 +="احمد ثبري";
		// ---------------------------------------------------------------

		// remove tags
		s1 = Jsoup.clean(s1, Whitelist.none());
		s1 = Jsoup.clean(s1, Whitelist.none());
		// s2 = Jsoup.clean(s2, Whitelist.none());
		// ----------------------------------------------------------------
		// remove all characters except English alphabets & numbers
		// Note : should ask Eng. Ali about removing those characters
		// s1 = s1.replace(".", " ");
		// s1 = s1.replaceAll("\\p{Punct}", " ");
		s1 = s1.replaceAll("[^a-zA-Z0-9]", " ");
		// s1 = s1.replaceAll("[^\\p{InArabic}\\s]", " ");
		// s1 += s2;
		// s1 = s1.replaceAll("[^a-zA-Z]", " ");
		// -----------------------------------------------------------------
		// test code here
//		String strArray[] = s1.split(" ");
//		System.out.println(strArray.length);
//		for(int i=0;i<strArray.length;i++)
//		{
//			System.out.println(strArray[i]);
//
//		}

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
		// String result = str.stream().collect(Collectors.joining(" "));
		// assertEquals(result, target);
//		for (int i = 0; i < str.size(); i++) {
//			System.out.println(str.get(i));
//
//		}
//		System.out.println(str.size());
//		System.out.println(x);
		return str;

	}

	// stemming words using port stemmer library
	public static ArrayList<String> Stemming(ArrayList<String> string_array) {
		PorterStemmer stemmer = new PorterStemmer();
		for (int i = 0; i < string_array.size(); i++) {
			String temp = new String(string_array.get(i));

			string_array.set(i, stemmer.stemWord(string_array.get(i)));
//			if (!temp.equals(string_array.get(i))) {
//				System.out.println(temp + "-" + string_array.get(i)); // print stemmed word & word
////				if(string_array.get(i).equals("احمد"))
////				{
////					break;
////				}
//			}
		}

		return string_array;
	}

	/////////////////


 
    
	public static ArrayList<String> filter(String path) {
		ArrayList<String> Words = new ArrayList<String>();
		try {
			// here we should put the path of the file from which we read the input
			File myObj = new File(path);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				// System.out.println(data);
				Words.add(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		ArrayList<String> string_array = new ArrayList<>();

		int size = Words.size();
		for (int i = 0; i < size; i++) {
			StringTokenizer str_tokenizer = new StringTokenizer(Words.get(i));
			// Add tokens to our array
			while (str_tokenizer.hasMoreTokens()) {
				string_array.add(str_tokenizer.nextToken());
			}
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
				if (HashForDf.containsKey(string)) // existed before in the IDF list
					HashForDf.put(string, HashForDf.get(string) + 1);
				else // first time
					HashForDf.put(string, 1);

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
		IndexOptions indexOptions = new IndexOptions().unique(true);
		String resultCreateIndex = col.createIndex(Indexes.ascending("Word", "DOC_ID"), indexOptions);
		System.out.println(String.format("Index created: %s", resultCreateIndex));
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
			//System.out.println("key: " + i + " value: " + inverteddocs.get(i));
		}

		// listofdocs.add(document1);
		// listofdocs.add(document2);

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
	public static void delete_doc_ID(MongoCollection<Document> col, String url, int doc_id) {
		// col.deleteMany(Filters.eq("DOC_ID",doc_id));
		col.deleteMany(Filters.eq("DOC_ID", url));
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

//---------------------------------------------------------------

//// Code Below is for connecting & testing database		
//		
////---------------------------------------------------------------
//
//// get connection with database server. should pass: Database Name in String
//MongoDatabase db = get_database("train");
//
////---------------------------------------------------------------
//
//// get collection from database , should send db & Collection Name String
//MongoCollection<Document> col = get_collection(db, "ahmed");
//
////---------------------------------------------------------------
//
//
//
////---------------------------------------------------------------
//
//Document doc1 = new Document("ahmed","sabry");
//FindIterable<Document> iterDoc = col.find();
//Iterator it = iterDoc.iterator();
////while(it.hasNext())
////{
////	
////	System.out.println(it.next());
////}
//String s1 = it.next().toString();
//Document doc2 = iterDoc.first();
////doc2 = iterDoc.;
//System.out.println(doc2.toString());
//
//System.out.println(s1);
////col.insertOne(doc1);
//System.out.println("bye");
//try
//{
//	col.insertOne(doc2);
//}
//catch(Exception e)
//{
//	System.out.println("Error in index ");
//}
