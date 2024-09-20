package com.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static com.example.CombineUniqueCompletedRoads.combineUniqueCompletedRoads;
import static com.example.GpxProcessor.getRoadName;
import static com.example.GpxProcessor.parseGPX;

public class App 
{

    public static void writeRoadNamesToFile(Set<String> roadNames, String inputFilePath) {
        // Get the file name without the extension
        String outputFileName = inputFilePath.substring(0, inputFilePath.lastIndexOf(".")) + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (String roadName : roadNames) {
                writer.write(roadName);
                writer.newLine(); // Write each road name on a new line
            }
            System.out.println("Road names written to file: " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        if (args.length < 1) {
            System.out.println("Usage: java -jar foo.jar COMMAND [options]");
            System.out.println("Commands:");
            System.out.println("  gpxToRoadNames <gpxFile>");
            System.out.println("  combineUniqueCompletedRoads <directory>");
            System.out.println("  compareRoads <completedRoads.csv> <allRoads.csv>");
            System.exit(1);
        }

        String command = args[0];

        try {
            switch (command) {
                case "gpxToRoadNames":
                    if (args.length != 2) {
                        System.out.println("Usage: gpxToRoadNames <gpxFile>");
                        System.exit(1);
                    }

                    double minDistance = 20.0; // Example configurable value between 4 and 10 meters
                    GpxProcessor processor = new GpxProcessor(minDistance);

                    List<Coordinate> coordinates = parseGPX(args[1]);

                    List<Coordinate> sampledCoordinates = processor.sampleCoordinates(coordinates);

                    // Store unique road names in a Set to automatically handle duplicates
                    Set<String> uniqueRoadNames = new HashSet<>();

                    // Reverse geocode the sampled coordinates
                    for (Coordinate coord : sampledCoordinates) {
                        String roadName = getRoadName(coord);
                        if (roadName != null && !roadName.isEmpty()) {
                            uniqueRoadNames.add(roadName);
                        }
                    }

                    writeRoadNamesToFile(uniqueRoadNames, args[1]);


                    break;
                case "combineUniqueCompletedRoads":
                    if (args.length != 2) {
                        System.out.println("Usage: combineUniqueCompletedRoads <directory of csv files");
                        System.exit(1);
                    }
                    combineUniqueCompletedRoads(args[1]);
                    break;

                case "compareRoads":
                    if (args.length != 3) {
                        System.out.println("Usage: compareRoads <allCompletedCombined.csv> <allRoads.csv>");
                        System.exit(1);
                    }
                    compareRoads(args[1], args[2]);
                    break;


                case "allGpxToRoads":
                    if (args.length != 2) {
                        System.out.println("Usage: allGpxToRoads <directory>");
                        System.exit(1);
                    }
                    processAllGpxToRoads(args[1]);
                    break;

                default:
                    System.out.println("Unknown command: " + command);
                    System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    private static void compareRoads(String completedRoadsFilePath, String allRoadsFilePath) throws IOException {
        // Implement comparison logic here
        System.out.println("Comparing completedRoads to allRoads and outputting what is left to do..");


        CompareRoadsProcessor.compareRoads(completedRoadsFilePath, allRoadsFilePath);

        System.out.println("Comparison completed, see 'remainingRoads.csv' for the remaining roads to travel.");
    }

    private static void processAllGpxToRoads(String directoryPath) throws IOException {
        System.out.println("This will not be implemented");

    }






}
