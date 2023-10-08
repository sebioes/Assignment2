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

@Component
public class IndexFlipper {

    public void flipIndex(String indexFileName, String flippedIndexFileName){
        try {
            CSVReader csvReader = new CSVReader(new FileReader(indexFileName));
            List<String[]> csvLines = csvReader.readAll();
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
                                    accLine[1] = accLine[1] + " " + line[1];
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
                //TODO clean up before storing
                lines.add(line);
            }



        CSVWriter writer = new CSVWriter(new FileWriter(flippedIndexFileName),',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\r\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

    }

}
