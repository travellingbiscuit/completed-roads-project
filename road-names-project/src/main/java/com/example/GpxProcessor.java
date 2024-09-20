package com.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GpxProcessor {

    private static final double EARTH_RADIUS = 6371e3; // Earth radius in meters
    private double minDistance;

    public GpxProcessor(double minDistance) {
        this.minDistance = minDistance;
    }



    public double calculateDistance(Coordinate coord1, Coordinate coord2) {
        double lat1 = Math.toRadians(coord1.getLatitude());
        double lon1 = Math.toRadians(coord1.getLongitude());
        double lat2 = Math.toRadians(coord2.getLatitude());
        double lon2 = Math.toRadians(coord2.getLongitude());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // distance in meters
    }

    public List<Coordinate> sampleCoordinates(List<Coordinate> coordinates) {
        List<Coordinate> sampledCoordinates = new ArrayList<>();

        if (coordinates.isEmpty()) return sampledCoordinates;

        // Add the first coordinate to start
        sampledCoordinates.add(coordinates.get(0));

        Coordinate lastSampled = coordinates.get(0);

        for (int i = 1; i < coordinates.size(); i++) {
            Coordinate current = coordinates.get(i);
            double distance = calculateDistance(lastSampled, current);

            if (distance >= minDistance) {
                sampledCoordinates.add(current);
                lastSampled = current;
            }
        }

        return sampledCoordinates;
    }


    public static List<Coordinate> parseGPX(String filePath) {
        List<Coordinate> coordinates = new ArrayList<>();
        try {
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            // Retrieve all the track point (trkpt) elements
            NodeList nList = doc.getElementsByTagName("trkpt");

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                double lat = Double.parseDouble(element.getAttribute("lat"));
                double lon = Double.parseDouble(element.getAttribute("lon"));

                // retrieve the timestamp if it's available in your GPX
                String timeString = element.getElementsByTagName("time").item(0).getTextContent();
                LocalDateTime timestamp = LocalDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME);


                coordinates.add(new Coordinate(lat, lon, timestamp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coordinates;
    }

    public static String getRoadName(Coordinate coord) {
        // Implement reverse geocoding logic here, making API calls
        String apiKey = System.getenv("GOOGLE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Google API key not set in environment variables, see README");
        }

        // Geocode the latitude and longitude to get the road name from google geocode API
        // This costs actual money

        String urlString = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=%s",
                coord.getLatitude(), coord.getLongitude(), apiKey
        );

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            JSONArray results = json.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                JSONArray addressComponents = firstResult.getJSONArray("address_components");
                for (int i = 0; i < addressComponents.length(); i++) {
                    JSONObject component = addressComponents.getJSONObject(i);
                    JSONArray types = component.getJSONArray("types");
                    for (int j = 0; j < types.length(); j++) {
                        if (types.getString(j).equals("route")) {
                            return component.getString("long_name");
                        }
                    }
                }
            } else {
                System.out.println("No results found for this coordinate: " + coord.getLatitude() + ", " + coord.getLongitude());
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        // fallthrough
        return null;

    }


}
