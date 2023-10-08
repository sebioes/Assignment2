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
@Component
public class IndexFlipper {

    public void flipIndex(String indexFileName, String flippedIndexFileName){
        try {
            CSVReader csvReader = new CSVReader(new FileReader(indexFileName));
            List<String[]> csvLines = csvReader.readAll();
            Set<String[]> lines = new HashSet<>();

            //to ckeck if keyword has already been added
            Set<String[]> temp = new HashSet<>();


            // loop trough csvLines
            for (String[] line : csvLines) {
                //loop trough keywords from line[i]:
                for (int i = 1; i < line.length; i++) {
                    //check if line[i] is already in lines[0]:
                    for (String[] strings : temp) {
                        if (strings[0].equals(line[i])) {
                            strings[1] = strings[1] + " , " + line[0];
                            break;
                        }
                    }
                    //if not, add add keyword to Temp[0] and link to Temp[1]
                    String[] newLine = new String[2];
                    newLine[0] = line[i];
                    newLine[1] = line[0];
                    temp.add(newLine);

                }
            }

            //add temp to lines
            for (String[] strings : temp) {
                //split links by comma
                String[] links = strings[1].split(" , ");
                //add strings[0] to links
                String[] newLine = new String[links.length + 1];

                newLine[0] = strings[0];

                for (int i = 0; i < links.length; i++) {
                    newLine[i + 1] = links[i];
                }
                lines.add(newLine);
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
