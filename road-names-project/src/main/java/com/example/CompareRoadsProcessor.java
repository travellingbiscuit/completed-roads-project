package com.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class CompareRoadsProcessor {

    public static void compareRoads(String completedRoadsFilePath, String allRoadsJsonFilePath) throws IOException {
        // Load completed roads from CSV
        Set<String> completedRoads = loadCompletedRoads(completedRoadsFilePath);

        // Load all roads from JSON
        Set<String> allRoads = loadAllRoadsFromJson(allRoadsJsonFilePath);

        // Find roads in 'allRoads' that are not in 'completedRoads'
        Set<String> roadsNotCompleted = new HashSet<>(allRoads);
        roadsNotCompleted.removeAll(completedRoads);

        // Output the roads that are not completed
        System.out.println("Roads not completed:");
        for (String road : roadsNotCompleted) {
            System.out.println(road);
        }

        File directory = new File("./");
        if (!directory.isDirectory()) {
            System.out.println("Error: The program is not running from a location where we can access the file system to save the road comparison");
            return;
        }

        File outputFile = new File("./", "remainingRoads.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String roadName : roadsNotCompleted) {
                writer.write(roadName);
                writer.newLine();
            }
            System.out.println("Remaining roads written to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing to remaining roads output file: " + outputFile.getName());
            e.printStackTrace();
        }
    }

    private static Set<String> loadCompletedRoads(String completedRoadsFilePath) throws IOException {
        Set<String> completedRoads = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(completedRoadsFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                completedRoads.add(line.trim());
            }
        }
        return completedRoads;
    }

    private static Set<String> loadAllRoadsFromJson(String allRoadsJsonFilePath) throws IOException {
        Set<String> allRoads = new HashSet<>();
        String content = new String(Files.readAllBytes(Paths.get(allRoadsJsonFilePath)));
        JSONObject json = new JSONObject(content);
        JSONArray elements = json.getJSONArray("elements");

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            if (element.has("tags")) {
                JSONObject tags = element.getJSONObject("tags");
                if (tags.has("name")) {
                    allRoads.add(tags.getString("name").trim());
                }
            }
        }
        return allRoads;
    }
}
