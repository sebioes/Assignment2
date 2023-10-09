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




           /* //we'll use this later
            Set<String[]> lines = new HashSet<>();






            Set<String[]> map = new HashSet<>();

            // implementing a weird version of mapReduce where String[0] equals keyword and String[1] a link
            for (String[] line : csvLines) {
                for (int i = 1; i < line.length; i++) {
                    String[] newLine = new String[2];
                    newLine[0] = line[i];
                    newLine[1] = line[0];
                    map.add(newLine);
                }
            }

            Set<String[]> reduce = map
                    .stream()
                    .reduce(
                    new HashSet<>(),
                    (acc, line) -> {
                        if (acc.isEmpty()) {
                            acc.add(line);
                        } else {
                            boolean found = false;
                            for (String[] accLine : acc) {
                                if (accLine[0].equals(line[0])) {
                                    accLine[1] = accLine[1] + "," + line[1];
                                    found = true;
                                }
                            }
                            if (!found) {
                                acc.add(line);
                            }
                        }
                        return acc;
                    },
                    (acc1, acc2) -> {
                        acc1.addAll(acc2);
                        return acc1;
                    }
            );

            // print reduce
            for (String[] line : reduce) {
                //split line[1] in String[] links
                String[] links = line[1].split(",");
                // create a String[] newLine, with length links + 1
                String[] newLine = new String[links.length + 1];
                // add line[0] to newLine[0]
                newLine[0] = line[0];
                // add links to newLine[1:]
                arraycopy(links, 0, newLine, 1, newLine.length - 1);

                //System.out.println(Arrays.toString(newLine));
                // add newLine to lines
                lines.add(newLine);
            }



        CSVWriter writer = new CSVWriter(new FileWriter(flippedIndexFileName),',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\r\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }*/

        } catch(Exception e){
            e.printStackTrace();
        }

    }

}
