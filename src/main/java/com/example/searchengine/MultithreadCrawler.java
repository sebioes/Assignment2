package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MultithreadCrawler extends Crawler {

    private ThreadPoolTaskExecutor executorService;



    private CopyOnWriteArraySet<String> visited;

    private CopyOnWriteArraySet<String[]> lines;



    private ObserveRunnable observeRunnable;

    private boolean done = false;

    public MultithreadCrawler(String indexFileName){
        //todo: initialize

        super(indexFileName);

        //initialise thread pool
        int threadPoolSize = 8;
        executorService = new ThreadPoolTaskExecutor();
        executorService.setCorePoolSize(threadPoolSize);
        executorService.setMaxPoolSize(threadPoolSize);
        executorService.setThreadNamePrefix("CrawlerThread");
        executorService.initialize();


        //initialise visited and lines
        visited = new CopyOnWriteArraySet<>();
        lines = new CopyOnWriteArraySet<>();
    }

    public void crawl(String startUrl){
        double startTime = System.currentTimeMillis();
        //todo: complete
        double endTime = System.currentTimeMillis();
        double duration = endTime - startTime;
        System.out.println("duration: "+duration);

    }


    /*
      todo: complete class.
      The purpose of this runnable is to do two tasks:
      1. Process the page at the given url (startUrl).
      2. Create new jobs for the hyperlinks found in the page.
      The instances of this class are used as input to the executorService.submit method.
       */
    class CrawlerRunnable implements Runnable{

        MultithreadCrawler crawler;

        String startUrl;

        public CrawlerRunnable(MultithreadCrawler crawler, String startUrl){
            this.crawler = crawler;
            this.startUrl = startUrl;

        }

        @Override
        public void run() {

        }
    }

    class ObserveRunnable implements Runnable {
        private MultithreadCrawler crawler;


        public ObserveRunnable(MultithreadCrawler crawler) {
            this.crawler = crawler;
        }

        @Override
        public void run() {
            //todo: complete
        }
    }
}
