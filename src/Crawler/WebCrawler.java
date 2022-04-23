package Crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static Crawler.Crawler.MyDB;

public class WebCrawler implements Runnable {
    public Thread thread;
    private int ID;

    @Override
    public void run() {
        System.out.println("WebCrawler Strarted " + ID);
        try {
            StartCrawl(1);
        } catch (MalformedURLException e) {
            System.out.println(e);
        }
    }

    public WebCrawler(int num) {
        try{
            ID = num;
            thread = new Thread(this);
        }
        catch (Exception e) {
            System.out.println("kak");
        }
    }


    public static boolean isUrlValid(String url) {
        try {
            if(url == "") return false;

            URL obj = new URL(url);
            obj.toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private void StartCrawl(int level) throws MalformedURLException {
        while(MyDB.NumberOfDocs.get() < MyDB.MAX_PAGES){
            String url = MyDB.getNextLink();
            if(isUrlValid(url)) {
                Document doc = request(url);
                if(doc != null) {
                    MyDB.insertNewDocument(doc.html() , url);

                    if(!(MyDB.NumberOfLinks.get() >= MyDB.MAX_PAGES + 10)) {
                        Elements ham = doc.select("a[href]");
                        for (Element link : ham) {
                            if (MyDB.NumberOfLinks.get() >= MyDB.MAX_PAGES + 10) break;
                            String next_link = link.absUrl("href");
                            if (MyDB.CheckLink(new URL(next_link))) {
                                MyDB.insertNewLink(next_link);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Finish Crawling " +  ID);

    }

    private Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() == 200) {
                return doc;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }


    public Thread getThread() {
        return thread;
    }
}


