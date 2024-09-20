package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CombineUniqueCompletedRoads {

    public static void combineUniqueCompletedRoads(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("The provided path is not a directory.");
            return;
        }

        Set<String> uniqueRoadNames = new HashSet<>();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".csv"));

        if (files == null || files.length == 0) {
            System.out.println("No CSV files found in the directory.");
            return;
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    uniqueRoadNames.add(line.trim());
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + file.getName());
                e.printStackTrace();
            }
        }

        File outputFile = new File(directoryPath, "allCompletedCombined.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String roadName : uniqueRoadNames) {
                writer.write(roadName);
                writer.newLine();
            }
            System.out.println("Unique road names have been written to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing to output file: " + outputFile.getName());
            e.printStackTrace();
        }
    }

}
