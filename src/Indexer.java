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
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import ca.rmen.porterstemmer.PorterStemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.UnknownError;
import java.net.UnknownHostException;
import java.util.*; 

@SuppressWarnings("deprecation")
public class Indexer {
	
	public static void FileOrganizer()
	{
		Map <String, Vector<Integer>> hash = new HashMap<>();
		Vector<Integer> v = new Vector<Integer> ();
		v.add(12); 
		hash.get("ahmed"); 
		
		for(Map.Entry pairEntry : hash.entrySet() )
		{
			Vector<Integer> v1 = (Vector<Integer>)pairEntry.getValue(); 
			System.out.println(pairEntry.getKey() + " : " + v1.elementAt(0));
		}
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Hello");
//		
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
		
		
		//
		ArrayList<String> string_array=  Remove_tags(doc);
		// remove stop words from file
		// array of strings
		
		string_array = Remove_Stop_Words(string_array);
		System.out.println("------------------------------------");
		
		for(int i=0;i<string_array.size();i++)
		{
			//System.out.println(string_array.get(i));
		}
		string_array = Stemming(string_array);
		
		//Map<String, ArrayList<Integer>> hashtable = FileOrgan();
		//ArrayList<Document> listofdocs = createdocuments(hashtable,1);

		
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
//		Document doc1 = new Document("ahmed","sabry");
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

		//MongoClient mongoClient2 =  MongoClients.create("mongodb://localhost:27017");
		//MongoDatabase db = mongoClient2.getDatabase(databasename);	
		//return db;
		
				ConnectionString connectionString = new ConnectionString("mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
				MongoClientSettings settings = MongoClientSettings.builder()
		        	.applyConnectionString(connectionString)
		        	.build();
				MongoClient mongoClient = MongoClients.create(settings);
				MongoDatabase database = mongoClient.getDatabase("test");
		      return database
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
		String s2 =  doc.toString();
		//s2 +="احمد ثبري";
		//s1 +="احمد ثبري";
		//---------------------------------------------------------------
		
		// remove rags
		s1 = Jsoup.clean(s1, Whitelist.none());
		//s2 = Jsoup.clean(s2, Whitelist.none());
		//----------------------------------------------------------------
		// remove all characters except English alphabets & numbers
		// Note : should ask Eng. Ali about removing those characters
		//s1 = s1.replace(".", " ");
		//s1 = s1.replaceAll("\\p{Punct}", " ");
		//s2 = s2.replaceAll("[^a-zA-Z0-9]", " ");
		s1 = s1.replaceAll("[^\\p{InArabic}\\s]", " ");
		//s1 += s2;
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
				if(string_array.get(i).equals("احمد"))
				{
					break;
				}
			}
		}
		
		
		return string_array;
	}
	
	
	/////////////////

    public static ArrayList<String> filter() {
        ArrayList<String> Words = new ArrayList<String>();
        try {
            // here we should put the path of the file from which we read the input
            File myObj = new File(".\\test.txt");
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
    

    public static Map<String, ArrayList<Integer>> FileOrgan() {
        System.out.println("ezayk");
        // -------------------------------------------------------
        // Load file into list
        ArrayList<String> Words = filter();
        System.out.println("filter passed");

        Map<String, ArrayList<Integer>> hash = new HashMap<>();
        int pos = 0;
        for (String string : Words) {

            if (hash.containsKey(string)) { // exists before?
                hash.get(string).set(0, (Integer) hash.get(string).get(0) + 1); // increment the tf
                hash.get(string).add(pos); // add the position to the end of the list
                pos++; // increment the position
            } else { // first time ?
                ArrayList<Integer> A = new ArrayList<>(3);
                A.add(1); // set TF to 1
                A.add(0); // set IDF to 0
                A.add(pos); // add the position to the end
                pos++; // increment the pos
                hash.put(string, A);
            }
        }
        return hash ; 
    }
   ////////////////////////////////
    public static void setindexes(MongoCollection<Document> col)
    {
    	//col.drop();
		IndexOptions indexOptions = new IndexOptions().unique(true);
		String resultCreateIndex = col.createIndex(Indexes.ascending("Word", "DOC_ID"),indexOptions);
		System.out.println(String.format("Index created: %s", resultCreateIndex));
    }
    public static ArrayList<Document> createdocuments(Map<String, ArrayList<Integer>> inverteddocs,int doc_id)
	{
		ArrayList<Document> listofdocs = new ArrayList<Document>();
		
		for (String i : inverteddocs.keySet()) 
		{
			ArrayList<Integer> arr  =  inverteddocs.get(i);
			Document doc1 = new Document("Word",i);
			doc1.append("DOC_ID", doc_id);
			doc1.append("TF", arr.get(0));
			doc1.append("IDF", arr.get(1));
			arr.remove(0);
			arr.remove(0);
			doc1.append("POSITION", arr);
			listofdocs.add(doc1);
			System.out.println("key: " + i + " value: " + inverteddocs.get(i));
		}


		//listofdocs.add(document1);
		//listofdocs.add(document2);
		
		return listofdocs;
	}
	public static void insertdocs(MongoCollection<Document> col,ArrayList<Document> listofdocs )
	{
		try
		{
			col.insertMany(listofdocs);	
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}
