import org.bson.Document;
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
				Map<String, ArrayList<Integer>> hashtable = FileOrgan();
				ArrayList<Document> listofdocs = createdocuments(hashtable,1);
				try
				{
					col.insertMany(listofdocs);	
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
				
				//HashMap<String, ArrayList<Integer>> inverteddocs = new HashMap<String, ArrayList<Integer>>();
				//createhashmap(inverteddocs);
				//createhashmap(inverteddocs);
				//ArrayList<Document> listofdocs = createdocuments(inverteddocs,1);
				//col.insertMany(listofdocs);
				//System.out.println(inverteddocs.get("compute"));
				
		 
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

}
