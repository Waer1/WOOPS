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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MongoAtlas {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

				ConnectionString connectionString = new ConnectionString("mongodb+srv://ahmedsabry:searchengine@searchengine.tnuaa.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
				MongoClientSettings settings = MongoClientSettings.builder()
		        	.applyConnectionString(connectionString)
		        	.build();
				MongoClient mongoClient = MongoClients.create(settings);
				MongoDatabase database = mongoClient.getDatabase("test");
				
				Document doc1 = new Document("ahmed","sabry");
				Document doc2 = new Document("ahmed","ahmed");
				
				
				MongoCollection<Document> col = get_collection(database, "ahmed");
				
				
				org.jsoup.nodes.Document doc = null;
				try
				{
					doc = Jsoup.connect("https://en.wikipedia.org/").get();
					
				}
				catch(Exception e)
				{
					System.out.println("Error in connection ");
				}
				String title = doc.select("head").text();
				String headers = doc.select("h1").text();
				headers +=" "+ doc.select("h2").text();
				headers +=" "+ doc.select("h3").text();
				headers +=" "+ doc.select("h4").text();
				headers +=" "+ doc.select("h5").text();
				headers +=" "+ doc.select("h6").text();
				String paragraphs = doc.select("p").text();
				
				ArrayList<String> headers_array = Remove_tags(headers);
				ArrayList<String> title_array = Remove_tags(title);
				ArrayList<String> paragraphs_array = Remove_tags(paragraphs);
				
				headers_array = Remove_Stop_Words(headers_array);
				title_array = Remove_Stop_Words(title_array);
				paragraphs_array = Remove_Stop_Words(paragraphs_array);
				
				headers_array = Stemming(headers_array);
				title_array = Stemming(title_array);
				paragraphs_array = Stemming(paragraphs_array);
				
				Map<String, Integer> hash_headers = new HashMap<String, Integer> ();
				Map<String, Integer> hash_title = new HashMap<String, Integer> ();
				Map<String, Integer> hash_paragraphs = new HashMap<String, Integer> ();
				hash_headers = hashtags(hash_headers,headers_array);
				hash_title = hashtags(hash_title,title_array);
				hash_paragraphs = hashtags(hash_paragraphs,paragraphs_array);
				
				System.out.println("----------------------------");
				//System.out.println(headers);
				//String s1 =  doc.toString();
				//s1 = Jsoup.clean(s1, Whitelist.none());
				
				//col.drop();
				//IndexOptions indexOptions = new IndexOptions().unique(true);
				//String resultCreateIndex = col.createIndex(Indexes.ascending("Word", "DOC_ID"),indexOptions);
				//System.out.println(String.format("Index created: %s", resultCreateIndex));
				//String resultCreateIndex2 = col.createIndex(Indexes.("Word", "DOC_ID"));
				//System.out.println(String.format("Index created: %s", resultCreateIndex2));
				
				//col.insertOne(doc1);
				//col.insertOne(doc2);
				//col.updateMany(Filters.eq("ahmed","ahmed"), Updates.set("ahmed", 150));
				//System.out.println("bye");
				//Map<String, ArrayList<Integer>> hashtable = FileOrgan();
				//ArrayList<Document> listofdocs = createdocuments(hashtable,2);
//				try
//				{
//					//col.insertMany(listofdocs);
//					delete_doc_ID(col,"",2);
//				}
//				catch(Exception e)
//				{
//					System.out.println(e);
//				}
//				
				
				//HashMap<String, ArrayList<Integer>> inverteddocs = new HashMap<String, ArrayList<Integer>>();
				//createhashmap(inverteddocs);
				//createhashmap(inverteddocs);
				//ArrayList<Document> listofdocs = createdocuments(inverteddocs,1);
				//col.insertMany(listofdocs);
				//System.out.println(inverteddocs.get("compute"));
				
		 
		}

	
	public static  Map<String,Integer> hashtags (Map<String, Integer> MetaData,ArrayList<String> arr )
	{
		for(int i = 0;i<arr.size();i++)
		{
			if(MetaData.get(arr.get(i)) == null)
			{
				MetaData.put(arr.get(i), 1);
			}
			else
			{
				int x = MetaData.get(arr.get(i));
				x++;
				MetaData.put(arr.get(i), x);
			}

		}
		return MetaData;
		
	}
	public static  MongoCollection<Document> get_collection(MongoDatabase db,String Collection_Name) {

		MongoCollection<Document> col = db.getCollection(Collection_Name);
		return col;
	}
	public static void createhashmap(Map<String, ArrayList<Integer>> inverteddocs )
	{
		
		System.out.println("Start Creating Documents");
		//System.out.println(inverteddocs.get("compute"));

		if(inverteddocs.get("compute") ==null)
		{
			System.out.println("not found");
			ArrayList<Integer> arr = new ArrayList<Integer>();
			arr.add(1);
			arr.add(0);
			arr.add(0);
			
			inverteddocs.put("compute", arr);
			
		}
		else
		{
			System.out.println("found");
			inverteddocs.get("compute").add(1);
		}
	}
	public static ArrayList<String> Remove_tags(String s1)
	{
		//String s1 =  doc.toString();
		//String s2 =  doc.toString();

		//---------------------------------------------------------------
		
		// remove tags
		s1 = Jsoup.clean(s1, Whitelist.none());
		//s2 = Jsoup.clean(s2, Whitelist.none());
		//----------------------------------------------------------------
		// remove all characters except English alphabets & numbers
		// Note : should ask Eng. Ali about removing those characters
		//s1 = s1.replace(".", " ");
		//s1 = s1.replaceAll("\\p{Punct}", " ");
		s1 = s1.replaceAll("[^a-zA-Z0-9]", " ");
		//s1 = s1.replaceAll("[^\\p{InArabic}\\s]", " ");
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
    public static void setindexes(MongoCollection<Document> col)
    {
    	//col.drop();
		IndexOptions indexOptions = new IndexOptions().unique(true);
		String resultCreateIndex = col.createIndex(Indexes.ascending("Word", "DOC_ID"),indexOptions);
		System.out.println(String.format("Index created: %s", resultCreateIndex));
    }
    public static void delete_doc_ID(MongoCollection<Document> col,String url,int doc_id)
    {
    	col.deleteMany(Filters.eq("DOC_ID",doc_id));
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
				System.out.println(temp+"-"+string_array.get(i)); // print stemmed word & word
//				if(string_array.get(i).equals(""))
//				{
//					break;
//				}
			}
		}
		
		
		return string_array;
	}
	
}
