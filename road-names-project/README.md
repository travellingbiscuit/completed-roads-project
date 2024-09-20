# Completed roads project
#### By Paul Drage (travellingbiscuit)

## What/why
If you wanted to travel all of the roads in an area, how would you know which roads you have already travelled?

This project aims to solve that problem by comparing the roads you have travelled with a reference GEOJSON file of all the roads in the area.


## 3 primary functions:
1. To process Strava exported GPX files (activities with GPS data) and extract the unique road names from the GPS data using Google Geocoder.
2. To combine many unique road name files (csv files) into a single csv file.
3. To compare the unique road names in the combined csv file with a reference GEOJSON file and output the unique road names that are remaining.

## Pre-setup
- You need a terminal - 5 mins
- You will need java installed - 10 mins
- You will need a google cloud account and a project with the Geocoding API enabled. - 10 mins
- You will need to enable billing on your google cloud project. - 5 mins
- You will need to generate an api key for the geocoding api - 5 mins
- I recommend adding a quota/limit to the api calls for geocoding so you dont end up spending $$$$ - I limited my own to around 5000 calls a day - 5 mins
- You will need to set your api key into an env var - 2 mins
  - Run this on your terminal with your api key in place of 'your_api_key_here':
    - `export GOOGLE_API_KEY=your_api_key_here`
- You need a strava account - 5 mins
- You have to go and do some activities using gps data and save them on strava - ~hours?
- You have to export your strava account history to get the GPX files - 15 mins (export is emailed to you)
- Build the project: mvn install, mvn package

## How to use
1. Export your Strava account / download account zip file and export the GPX files.
2. Run the gpxToRoadNames for each of your GPX files you want to geocode:
   - `java -jar target/road-names-project-1.0-SNAPSHOT-jar-with-dependencies.jar gpxToRoadNames 12267637657.gpx`
   - This will output a road names csv file of the same name as the GPX file. (but with .csv extension)
   - You might like to run this in a small batch using bash, an example is in the 'runGpxToRoadNamesBatch.sh' file.
     - OR You can run this using `./runGpxToRoadNamesBatch.sh` provided your gpx files are in a directory called 'gpx'.
3. Combine all the road names csv files into a single csv file:
   - Place all of your completed road name csv files into a directory, say 'inputFiles'.
   - `java -jar target/road-names-project-1.0-SNAPSHOT-jar-with-dependencies.jar combineUniqueCompletedRoads inputFiles`
     - This will output a combined unique road names csv file of all the roads you've travelled: `allCompletedCombined.csv`
4. You will need a reference GEOJSON file of all the roads in the area you are interested in:
   - You should be able to use https://overpass-turbo.eu/ to query for your data, an example query:
       ```
       [out:json];
       area["name"="Oxford"]->.searchArea;
       (
       way["highway"](area.searchArea);
       );
       out tags;
        ```
   - Copy all of the json output to a file like 'oxford.geojson'
5. Finally, let's find out what you have left to travel:
   - `java -jar target/road-names-project-1.0-SNAPSHOT-jar-with-dependencies.jar compareRoads allCompletedCombined.csv banbury.geojson`
     - This will output a csv file of the unique road names you have left to travel called 'remainingRoads.csv'
6. You can now use the 'remainingRoads.csv' file to plan your next adventure!


## Building source
- mvn install
- mvn package