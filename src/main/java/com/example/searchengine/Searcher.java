package com.example.searchengine;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;

@Component
public class Searcher {
    /**
     *
     * @param keyword to search
     * @param flippedIndexFileName the file where the search is performed.
     * @return the list of urls
     */
    public List<String> search(String keyword, String flippedIndexFileName) {
        long startTime = System.currentTimeMillis();
        List<String> urls = new ArrayList<>();

        //open file with csv reader
        File file = new File(flippedIndexFileName);
        CSVReader csvLines;
        try {
            csvLines = new CSVReader(new FileReader(file));
            for (String[] line : csvLines) {
                if (line[0].equals(keyword)) {
                    //add urls to list
                    for (int i = 1; i < line.length; i++) {
                        urls.add("https://api.interactions.ics.unisg.ch/hypermedia-environment" + line[i].trim());
                    }
                }
            }

        } catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // TODO : FIX time measurement
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        System.out.println("duration searcher flipped: " + duration);
        return urls;
    }


}
