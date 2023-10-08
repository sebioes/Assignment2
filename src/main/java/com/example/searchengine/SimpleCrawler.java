package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


import java.io.*;
import java.util.*;

public class SimpleCrawler extends Crawler {


    protected SimpleCrawler(String indexFileName) {
        super(indexFileName);
    }

    public void crawl(String startUrl){
        try {
            long startTime = System.currentTimeMillis();
            Set<String[]> lines = explore(startUrl, new HashSet<>(), new HashSet<>());
            FileWriter fileWriter = new FileWriter(indexFileName);
            CSVWriter writer = new CSVWriter(fileWriter,',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\r\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000;
            System.out.println("duration simple crawler: "+duration + "s");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     *
     * @param startUrl the url where the crawling operation starts
     * @param lines stores the lines to print on the index file
     * @param visited stores the urls that the program has already visited
     * @return the set of lines to print on the index file
     */
    public Set<String[]> explore(String startUrl, Set<String[]> lines, Set<String> visited){
        //Creat Breathsearch queue
        Queue<String> queue = new LinkedList<>();
        //Add startUrl to queue
        queue.add(startUrl);

        //While queue is not empty
        while(!queue.isEmpty()){
            //Get first element of queue
            String url = queue.poll();
            //Add url to visited
            if (visited.contains(url)) {
                System.out.println("url in visited: " + url);
                continue;
            }

            visited.add(url);

            // Get Jsoup from url
            Document doc;

            try {
                doc = Jsoup.connect(url).get();

                //Get hyperlinks from soup and add them to queue if not in visited
                Elements links = doc.select("a");

                //check if link starts with https://api.interactions.ics.unisg.ch/hypermedia-environment/ and add to queue if not yet visited
                for (Element link : links ){
                    String absoluteUrl = link.absUrl("href");
                    if (absoluteUrl.startsWith("https://api.interactions.ics.unisg.ch/hypermedia-environment/")){
                        if (!visited.contains(absoluteUrl)){
                            queue.add(absoluteUrl);
                        }
                    }
                }

                //Add infos to lines -> /dffbabe7ab6d1 , three , amused , sheep
                //Create String[] line
                //todo: don't hardcode the size of the array
                String[] line = new String[4];

                //Add url to line
                line[0] = (url.substring( url.lastIndexOf('/')));

                //Get keywords from soup
                Elements keywords = doc.select("p");

                //add elements to String[] line
                int i = 1;
                for (Element keyword : keywords){
                    line[i++] = keyword.text();
                }
                //Add line to lines
                lines.add(line);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

}
