package com.springboot.app;
import java.util.*;

public class CrawlerMain {
    public static void main(String[] args) throws Exception {
        List<String> seedSet;
        final int numOfThreads = 7;
        MongoDB.MongoHandler mdb = new MongoDB.MongoHandler();

        // Connect to MongoDB
        mdb.ConnecttoDB();
        // Get the number of crawled pages from previous run
        int pages = mdb.getPagesNum();
        System.out.println("Now " + pages);
        //if the number of pages >= 5000 or = 0  then the last run was completed successfully
        //So we should start from our seed set
        if (pages >= 5000 || pages == 0) {
            pages = 0;
            seedSet = Arrays.asList(
                    "https://bleacherreport.com/",
                    "https://www.javatpoint.com/",
                    "https://www.seat.com/",
                    "https://www.edx.org/",
                    "https://www.bbc.com/news",
                    "https://www.npr.org/",
                    "https://en.wikipedia.org/wiki/Main_Page",
                    "https://www.bbc.com/sport",
                    "https://www.nytimes.com/",
                    "https://www.reddit.com/"
            );
            mdb.dropCollection();
            mdb.InsertList(seedSet);   // add seedset to database
        } else {
            //if the number of pages from last run is less than 5000
            //So the last run was interrupted and we need to complete it
            //So we will retrieve the links from database that the last
            // run stopped at and continue from them
            System.out.println("Starting from " + pages);
            seedSet = mdb.getLinks();
        }
        //send the current number of pages to initialize crawler
        //And also send to them the links that it should start from
        InitializeCrawler initializeCrawler = new InitializeCrawler(pages);
        initializeCrawler.seedSet(seedSet);
        Thread threads[] = new Thread[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
            threads[i] = new Thread(initializeCrawler);
            threads[i].start();
        }
        for (int i = 0; i < numOfThreads; i++) {
            threads[i].join();
        }


    }



}