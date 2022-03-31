import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ca.rmen.porterstemmer.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.UnknownError;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;


@SuppressWarnings("deprecation")
public class Indexer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Hello");
		
		//---------------------------------------------------------------
		// what to do
		//	1) get a documents from database
		//	2) remove metadata from document or string or array of strings
		//	3) remove stopwords from string
		//	4) count # of words in string or length of array of strings
		//	5) stemming words
		//	6) use hash map to insert data from strings about each word
		//	7) insert data in database 
		//	8) repeat process until you finish all documents

		
		//---------------------------------------------------------------
		// step: 0
		// try getting a document from jsoup for testing until crawler is ready
		org.jsoup.nodes.Document doc = null;
		try
		{
			doc = Jsoup.connect("https://en.wikipedia.org/").get();
			
		}
		catch(Exception e)
		{
			System.out.println("Error in connection ");
		}
		//------------------------------------------------------------------
		
		// remove metadata & punctuation
		ArrayList<String> string_array=  Remove_tags(doc);
		// remove stop words from file
		string_array = Remove_Stop_Words(string_array);
		System.out.println("------------------------------------");
		for(int i=0;i<string_array.size();i++)
		{
			//System.out.println(string_array.get(i));
		}
		string_array = Stemming(string_array);
		
		//---------------------------------------------------------------
		
//      // Code Below is for connecting & testing database		
//				
//		//---------------------------------------------------------------
//		
//		// get connection with database server. should pass: Database Name in String
//		MongoDatabase db = get_database("train");
//		
//		//---------------------------------------------------------------
//		
//		// get collection from database , should send db & Collection Name String
//		MongoCollection<Document> col = get_collection(db, "ahmed");
//		
//		//---------------------------------------------------------------
//		
//		
//		
//		//---------------------------------------------------------------
//
//		//Document doc1 = new Document("ahmed","sabry");
//		FindIterable<Document> iterDoc = col.find();
//		Iterator it = iterDoc.iterator();
////		while(it.hasNext())
////		{
////			
////			System.out.println(it.next());
////		}
//		String s1 = it.next().toString();
//		Document doc2 = iterDoc.first();
//		//doc2 = iterDoc.;
//		System.out.println(doc2.toString());
//		
//		System.out.println(s1);
//		//col.insertOne(doc1);
//		System.out.println("bye");
//		try
//		{
//			col.insertOne(doc2);
//		}
//		catch(Exception e)
//		{
//			System.out.println("Error in index ");
//		}
		
	}
	
	// function get database from mongodb server 
	// Note should be modified when connect to atlas
	
	public static  MongoDatabase get_database(String databasename) {

		MongoClient mongoClient2 =  MongoClients.create("mongodb://localhost:27017");
		MongoDatabase db = mongoClient2.getDatabase(databasename);	
		return db;
		
		//		ConnectionString connectionString = new ConnectionString("mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
		//		MongoClientSettings settings = MongoClientSettings.builder()
		//        	.applyConnectionString(connectionString)
		//        	.build();
		//		MongoClient mongoClient = MongoClients.create(settings);
		//		MongoDatabase database = mongoClient.getDatabase("test");
	}
	
	// function get collection from database 
	
	public static  MongoCollection<Document> get_collection(MongoDatabase db,String Collection_Name) {

		MongoCollection<Document> col = db.getCollection(Collection_Name);
		return col;
	}
	
	// Function to clean up the documents
	public static ArrayList<String> Remove_tags(org.jsoup.nodes.Document doc)
	{
		String s1 =  doc.toString();
		//---------------------------------------------------------------
		
		// remove rags
		s1 = Jsoup.clean(s1, Whitelist.none());
		
		//----------------------------------------------------------------
		// remove all characters except English alphabets & numbers
		// Note : should ask Eng. Ali about removing those characters
		//s1 = s1.replace(".", " ");
		//s1 = s1.replaceAll("\\p{Punct}", " ");
		s1 = s1.replaceAll("[^a-zA-Z0-9]", " ");
		//s1 = s1.replaceAll("[^a-zA-Z]", " ");
		//-----------------------------------------------------------------
		// test code here
//		String strArray[] = s1.split(" ");
//		System.out.println(strArray.length);
//		for(int i=0;i<strArray.length;i++)
//		{
//			System.out.println(strArray[i]);
//
//		}
		
		//-------------------------------------------------------------------
		// split string to array of strings
		StringTokenizer str_tokenizer = new StringTokenizer(s1);
	 
	    ArrayList<String> string_array = new ArrayList<String>(str_tokenizer.countTokens());
	        // Add tokens to our array

	    while (str_tokenizer.hasMoreTokens())
	    {
            string_array.add(str_tokenizer.nextToken());
	    }
	    // return array of strings
	    return string_array;
	}
	
	// function to remove stop words
	public static ArrayList<String> Remove_Stop_Words(ArrayList<String> str)
	{
		//-------------------------------------------------------
		// Load file into list 
		ArrayList<String> stoparr = new ArrayList<String>();
		try
		{
		      File myObj = new File("stop_words_english.txt");
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) 
		      {
		    	String data = myReader.nextLine();
		        //System.out.println(data);
		        stoparr.add(data);
		      }
		      myReader.close();
		} 
		catch (FileNotFoundException e)
		{
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		}
		//----------------------------------------------------------------------
		// seocnd remove stop words from String 
		// string should be filtered from tags & punctuation
		int x = str.size();
		str.removeAll(stoparr);
		//String result = str.stream().collect(Collectors.joining(" "));
	    //assertEquals(result, target);
		for(int i=0;i<str.size();i++)
		{
			System.out.println(str.get(i));
			
		}
		System.out.println(str.size());
		System.out.println(x);
		return str;
		
	}
	public static ArrayList<String> Stemming(ArrayList<String> string_array)
	{
		PorterStemmer stemmer = new PorterStemmer();
		for(int i=0;i<string_array.size();i++)
		{
			String temp = new String(string_array.get(i));
			
			string_array.set(i,stemmer.stemWord(string_array.get(i)));
			if(!temp.equals(string_array.get(i)))
			{
				System.out.println(temp+"-"+string_array.get(i));
			}
		}
		
		
		return string_array;
	}
	
}
