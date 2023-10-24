package com.example.searchengine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.lang.System.arraycopy;

@Component
public class IndexFlipper {

    public void flipIndex(String indexFileName, String flippedIndexFileName){
        try {
            CSVReader csvReader = new CSVReader(new FileReader(indexFileName));
            List<String[]> csvLines = csvReader.readAll();

            //to store keyword-link associations
            Map<String, List<String>> keywordMap = new HashMap<>();

            

            //populate keywordMap
            for (String[] line : csvLines) {
                String keyword = line[0];
                for (int i = 1; i < line.length; i++) {
                    keywordMap.computeIfAbsent(line[i], k -> new ArrayList<>()).add(keyword);
                }
            }

            //write flippedIndex CSV
            CSVWriter writer = new CSVWriter(new FileWriter(flippedIndexFileName),',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\r\n");
            keywordMap.forEach((link, keywords) -> {
                String[] line = new String[keywords.size() + 1];
                line[0] = link;
                for (int i = 0; i < keywords.size(); i++) {
                    line[i + 1] = keywords.get(i);
                }
                writer.writeNext(line);
            });

        } catch(Exception e){
            e.printStackTrace();
        }

    }

}
