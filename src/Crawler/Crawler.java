package Crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private static List<String> initialLinks = Arrays.asList("https://www.npr.org/" , "https://www.wikipedia.org/", "https://www.nytimes.com/international/" , "https://bleacherreport.com/" , "https://www.javatpoint.com/" , "https://www.bbc.com/", "https://stackoverflow.com/questions/31577715/mpj-express-summing-an-array-using-mpi" , "https://www.physio-pedia.com/home/" , "https://www.seat.com/", "https://www.transfermarkt.com/" , "https://www.dailymail.co.uk/home/index.html" , "https://www.reddit.com/" , "https://www.foxnews.com/" );
    private final int NumberofThreads = 7;
    ArrayList<WebCrawler> Threads = new ArrayList<>();
    static mongoHandler MyDB;

    public Crawler() throws Exception {
        MyDB = new mongoHandler();

        while(true){
            MyDB.intialDB(initialLinks);
            Threads.clear();
            for(int i = 1; i <= NumberofThreads ; i++){
                WebCrawler w = new WebCrawler(i);
                w.thread.start();
                Threads.add(w);
            }

            for ( WebCrawler w : Threads) {
                try {
                    w.getThread().join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Done ya negm!");
            TimeUnit.SECONDS.sleep(15);
            MyDB.Xchg();
        }
    }


}

