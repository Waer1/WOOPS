package Crawler;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;


public class mongoHandler {

    // NewCrawlerDB is the current working db
    // LastCrawlerDB its the last DB get from crawling
    private MongoDatabase NewCrawlerDB,LastCrawlerDB;
    MongoCollection<Document> NewLinks , LastLinks, NewDocuments, LastDocuments;
    AtomicInteger NumberOfLinks , Count, NumberOfDocs;
    /* the host of the url and the disallows urls from robot.txt  */
    public static HashMap<String, ArrayList<String>> LinksVisited = new HashMap<String, ArrayList<String>>();

    static final int MAX_PAGES = 10;
    boolean status = true;

    public static  MongoDatabase get_database(String databasename) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://Waer:RHhDdESAKY5HvzZ@cluster0.jafiz.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase(databasename);
        return database;
    }

    private Document getLinkDocument(String link)
    {
        Document documentLink = new Document();
        documentLink.append("count",NumberOfLinks.getAndIncrement());
        documentLink.append("link",link);
        return documentLink;
    }

    // HTML Document
    /*
        Link
        Html
        last change
    * */

    private Document getHTMLDocument(String link,String HTML)
    {
        //TODO: check in old database for same link to see if changes or not
        // if no change increment changes else set it zero
        Document documentLink = new Document();
        documentLink.append("link",link);
        documentLink.append("HTML",HTML);
        Document linkinlastcrawling = LastDocuments.find(Filters.eq("link",link)).first();
        if(linkinlastcrawling != null && linkinlastcrawling.getString("HTML") == HTML) {
                documentLink.append("Changes", linkinlastcrawling.getInteger("Changes"));
        }else{
            documentLink.append("Changes", 0);
        }

        return documentLink;
    }


    public mongoHandler()
    {
        NumberOfLinks = new AtomicInteger(0);
        Count = new AtomicInteger(0);
        NumberOfDocs = new AtomicInteger(0);
        //TODO: copy the original collection to our code
        NewCrawlerDB = get_database(new String("NewCrawlerDB") );
        NewLinks = NewCrawlerDB.getCollection("Links");
        NewDocuments = NewCrawlerDB.getCollection("Documents");
        //TODO: copy the last collection to our code
        LastCrawlerDB = get_database(new String("LastCrawlerDB") );
        LastLinks = LastCrawlerDB.getCollection("Links");
        LastDocuments = LastCrawlerDB.getCollection("Documents");
        LinksVisited = new HashMap<String, ArrayList<String>>();
        CleanCollections(NewLinks);
        CleanCollections(NewDocuments);
    }

    public void Xchg()
    {
        if(status){
            NewLinks = LastCrawlerDB.getCollection("Links");
            NewDocuments = LastCrawlerDB.getCollection("Documents");
            //TODO: copy the last collection to our code
            LastLinks = NewCrawlerDB.getCollection("Links");
            LastDocuments = NewCrawlerDB.getCollection("Documents");
            CleanCollections(NewLinks);
            CleanCollections(NewDocuments);
        }else
        {
            NewLinks = NewCrawlerDB.getCollection("Links");
            NewDocuments = NewCrawlerDB.getCollection("Documents");
            //TODO: copy the last collection to our code
            LastLinks = LastCrawlerDB.getCollection("Links");
            LastDocuments = LastCrawlerDB.getCollection("Documents");
            CleanCollections(NewLinks);
            CleanCollections(NewDocuments);
        }
        NumberOfLinks.set(0);
        Count.set(0);
        NumberOfDocs.set(0);
        LinksVisited.clear();
        status = ! status;
    }


    public void CleanCollections(MongoCollection<Document> col){
// Delete All documents from collection Using blank BasicDBObject
        col.deleteMany(new BasicDBObject());
    }

    public void intialDB(List<String> initialLinks){
        for(String s : initialLinks)
            insertNewLink(s);
    }

    // Function to detect if the inserted link is repeated on database or not
    // must be used before every insert
    public boolean isRepeated(String newlink)
    {
        return LinksVisited.containsKey(newlink);
    }

    public int insertNewLink(String newlink)
    {
        boolean s = isRepeated(newlink);
        if(!s){
            try {
                NewLinks.insertOne( getLinkDocument(newlink) );
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return -1;
    }

    public int insertNewDocument(String HTML, String link)
    {
            //TODO: insert new link in the database

            try {
                NewDocuments.insertOne( getHTMLDocument(link,HTML) );
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            return  NumberOfDocs.incrementAndGet();
    }

    public String getNextLink()
    {
        //TODO: get the next link to get crawled

        //TODO: what should happen before crawler the link is to search in last DB to check if he static page or not
        // that happen with integer we increment it each time we found the page changed from last one
        // let limit 10 if we found page not changed in 10 next crawler we consider it static

        int c = Count.getAndIncrement();
        if(c > NumberOfLinks.get()) {
            System.out.println("no more links " + c + " " + NumberOfLinks.get() );
            exit(1);
        }
        Document Doc = NewLinks.find(Filters.eq("count", c)).first();
        if(Doc == null) {
            System.out.println("get number " + c + " and availabe is " + NumberOfLinks.get());
            return new String("");
        }
        return Doc.getString("link");
    }

    // link : url ,



    public boolean CheckLink(URL url)
    {
        try
        {
            /* check if the same link already fetched before but with / or # or \ in the end */
            String s,stringURL = url.toString();
            if(stringURL.endsWith("/*$") ||stringURL.endsWith("#*$")||stringURL.endsWith("//*$"))
            {
                s = stringURL.replaceFirst("/*$", "");
                s = stringURL.replaceFirst("#*$", "");
                url = new URL(s);
            }
            else if (url.toString().endsWith("/#"))
            {
                s = stringURL.replaceFirst("/#", "");
                url = new URL(s);
            }

            if(isRepeated(url.getHost().replace("www","")))
            {
                if(!Allowed(url)) {
                    //System.out.println("\n not Allowed to download "+url);
                    return false;
                }
            }
            else
            {
                //System.out.println("Not checked yet !! checking the link ....");
                if(robotSafe(stringURL)) {
                    if (!Allowed(url)){
                        //System.out.println("\n Allowed to download " + url);
                        return false;
                    }
                }
            }

        }
        catch (IOException e)
        {
            System.out.println("\n Error while try to check for robots.txt "+ e);
            return false;
        }
        return true;
    }

    /* this class is the main class of the robots checker --> access the robots.txt of the host and save it
    return false if there is no disallowed
     */
    public boolean robotSafe(String link)  {
        URL url;
        String host;
        try {
            url = new URL(link);
            String protocol = url.getProtocol(); /* such as : http / https / ftp */
            host = url.getHost();         /* such as : if we have url : https:// www.geeksforgeeks.org --> host : www.geeksforgeeks.org */

            /* accessing robots.txt from the url */
            url = new URL(protocol + "://" + host + "/robots.txt");
        }
        catch (MalformedURLException e) {
            // error while access robot.txt
            System.out.println(e);
            return false;
        }
        /* divide robot to part to read it */
        if (url != null) {
            String RobotOrders;
            try {
                InputStream urlRobotStream = url.openStream();
                byte b[] = new byte[1000];
                int numRead = urlRobotStream.read(b);
                RobotOrders = new String(b, 0, numRead);
                while (numRead != -1) {
                    numRead = urlRobotStream.read(b);
                    if (numRead != -1) {
                        String newCommands = new String(b, 0, numRead);
                        RobotOrders += newCommands;
                    }
                }
                urlRobotStream.close();
            }
            catch (Exception e) {
                System.out.println("error while trying to extract disallows from the url " + url);
                return false; /* if there is no robots.txt file, it is okay to download the page */
            }
            if (RobotOrders.contains("Disallow"))
            {
                String[] split = RobotOrders.split("\n"); // split each individual command with line
                String typeOfUserAgent = null;
                ArrayList<String> disallows = new ArrayList<String>();
                boolean userAgent = false;
                for (int i = 0; i < split.length; i++) {
                    String line = split[i].trim();
                    if (line.toLowerCase().startsWith("user-agent")) {
                        int start = line.indexOf(":") + 1;
                        int end = line.length();
                        typeOfUserAgent = line.substring(start, end).trim();
                        if (typeOfUserAgent.equalsIgnoreCase("*")) {
                            userAgent = true;
                        } else userAgent = false;

                    } else if (line.toLowerCase().startsWith("disallow") && userAgent) {
                        if (typeOfUserAgent != null) {
                            int start = line.indexOf(":") + 1;
                            int end = line.length();
                            String s = line.substring(start, end).trim();
                            disallows.add(s);

                        }
                    }
                }
                LinksVisited.put(host, disallows);
                return true;

            }
        }
        return true;
    }

    /* avoiding not necessary links stackoverflow/users/teams (robot.text) ) */
    boolean Allowed(URL link)
    {
        ArrayList<String> disallows = LinksVisited.get(link.getHost().replace("www",""));
        if (disallows != null) {
            for (String pattern : disallows) {
                String regex = pattern;

                regex = regex.replaceAll("\\*", ".*");

                regex = ".*" + regex + ".*";
                Pattern p = Pattern.compile(regex);
                Matcher matcher = p.matcher(link.toString());
                if (matcher.matches()) return false;
            }
        }
        return true;
    }
}
