#!/bin/bash

# Directory containing the GPX files
GPX_DIR="./gpx"

# Path to the JAR file
JAR_PATH="target/road-names-project-1.0-SNAPSHOT-jar-with-dependencies.jar"

# Loop through all .gpx files in the directory
for gpx_file in "$GPX_DIR"/*.gpx;
do
  # Run the Java program with the GPX file as the input argument
  java -jar "$JAR_PATH" gpxToRoadNames "$gpx_file"
done