package com.example.searchengine;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;

public class SimpleCrawler extends Crawler {


    protected SimpleCrawler(String indexFileName) {
        super(indexFileName);
    }

    public void crawl(String startUrl){
        try {
            int duration = 0; //TODO: update the value in the code
            Set<String[]> lines = explore(startUrl, new HashSet<>(), new HashSet<>());
            FileWriter fileWriter = new FileWriter(indexFileName);
            CSVWriter writer = new CSVWriter(fileWriter,',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\r\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            System.out.println("duration simple crawler: "+duration);
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
        //
        System.out.println("startUrl: "+startUrl);
        //Creat Breathsearch queue
        Queue<String> queue = new LinkedList<>();

        //Add startUrl to queue
        queue.add(startUrl);

        //Add startUrl to visited
        visited.add(startUrl);

        //While queue is not empty
        while(!queue.isEmpty()){
            //Get first element of queue
            String url = queue.poll();

            //TODO : Get Jsoup from url

            //TODO : Get keywords from soup

            //TODO : Get hyperlinks from soup and add them to queue if not in visited

            //TODO : Add infos to lines

            //Add url to visited
            visited.add(url);
        }







        return lines;


    }

}
