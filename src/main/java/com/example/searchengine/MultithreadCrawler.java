package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class MultithreadCrawler extends Crawler {
    private ThreadPoolTaskExecutor executorService;
    private final CopyOnWriteArraySet<String> visited;
    private CopyOnWriteArraySet<String[]> lines;

    public MultithreadCrawler(String indexFileName) {
        super(indexFileName);

        // Initialize thread pool
        int threadPoolSize = 8;
        executorService = new ThreadPoolTaskExecutor();
        executorService.setCorePoolSize(threadPoolSize);
        executorService.setMaxPoolSize(threadPoolSize);
        executorService.setThreadNamePrefix("CrawlerThread");
        executorService.initialize();

        // Initialize visited and lines
        visited = new CopyOnWriteArraySet<>();
        lines = new CopyOnWriteArraySet<>();

        // Create and submit the ObserveRunnable to monitor the crawling progress
        //ObserveRunnable observeRunnable = new ObserveRunnable(this);
        //executorService.execute(observeRunnable);
    }

    // Add getter methods for visited and lines
    public Set<String> getVisited() {
        return visited;
    }

    public CopyOnWriteArraySet<String[]> getLines() {
        return lines;
    }


    public void crawl(String startUrl) {
        double startTime = System.currentTimeMillis();

        // Start the crawling process from the initial URL
        CrawlerRunnable initialTask = new CrawlerRunnable(this, startUrl);
        executorService.execute(initialTask);

        while (executorService.getActiveCount() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Save lines to file (similar to the simple crawler)
        try (FileWriter fileWriter = new FileWriter(indexFileName); CSVWriter writer = new CSVWriter(fileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER, ' ', "\r\n")) {
            for (String[] line : lines) {
                writer.writeNext(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Wait for all tasks to finish
        executorService.shutdown();

        double endTime = System.currentTimeMillis();
        double duration = endTime - startTime;
        System.out.println("duration: " + duration / 1000 + " seconds");
    }


    class CrawlerRunnable implements Runnable {

        MultithreadCrawler crawler;

        String startUrl;

        public CrawlerRunnable(MultithreadCrawler crawler, String startUrl) {
            this.crawler = crawler;
            this.startUrl = startUrl;

        }

        @Override
        public void run() {
            visited.add(startUrl);
            try {
                Document doc = Jsoup.connect(startUrl).get();

                Elements keywords = doc.select("p");
                String[] line = new String[keywords.size() + 1];

                line[0] = (startUrl.substring(startUrl.lastIndexOf('/')));
                int i = 1;
                for (Element keyword : keywords) {
                    line[i++] = keyword.text();
                }
                //Add line to lines
                lines.add(line);


                Elements links = doc.select("a");

                //check if link starts with https://api.interactions.ics.unisg.ch/hypermedia-environment/ and add to queue if not yet visited
                for (Element link : links) {
                    String absoluteUrl = link.absUrl("href");
                    synchronized (visited) {
                        if (absoluteUrl.startsWith("https://api.interactions.ics.unisg.ch/hypermedia-environment/")) {
                            if (!visited.contains(absoluteUrl)) {
                                visited.add(absoluteUrl);
                                crawler.executorService.submit(new CrawlerRunnable(crawler, absoluteUrl));
                            }
                        }
                    }
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        class ObserveRunnable implements Runnable {
            private MultithreadCrawler crawler;


            public ObserveRunnable(MultithreadCrawler crawler) {
                this.crawler = crawler;
            }


            @Override
            public void run() {
                // Initialize variables to track the progress
                int totalURLs = 0;
                int visitedURLs = 0;
                int processedURLs = 0;

                // Set a timeout for waiting
                long timeoutMillis = 1000; // 1 second (adjust as needed)

                // Continue monitoring until the timeout is reached
                while (true) {
                    // Calculate the number of URLs in the queue
                    int queuedURLs = crawler.executorService.getThreadPoolExecutor().getQueue().size();

                    // Update progress statistics
                    totalURLs = visitedURLs + queuedURLs + processedURLs;
                    visitedURLs = crawler.getVisited().size();
                    processedURLs = crawler.getLines().size();

                    // Print or log the progress
                    System.out.println("Total URLs: " + totalURLs);
                    System.out.println("Visited URLs: " + visitedURLs);
                    System.out.println("Processed URLs: " + processedURLs);
                    System.out.println("Queued URLs: " + queuedURLs);
                    System.out.println();

                    // Check if the thread pool has terminated
                    if (crawler.executorService.getThreadPoolExecutor().isTerminated()) {
                        break; // Termination detected, exit the loop
                    }
                }
            }
        }
    }
}
