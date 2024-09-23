# WLMap

## Description
WLMap (Wireless Localization Map) is an Android app used to navigate inside buildings to find classrooms, stairs, elevators, and bathrooms. The goal is to create a stable Android mobile application for indoor navigation and WiFi-based data collection. 

## Features
This app can provide the following:
   - Accurate mapping of Davis Hall.
   - Navigational directions to any point within Davis Hall.
   - Record gyroscopic and accelerometer information.
   - An interactable UI.
   - Navigational map option.
   - Data collection map option.

## Goals and Challenges
Our current goals for this project include:
   - Resolving technical issues with the data collection app.
   - Developing a functional navigational interface.
   - Enhancing the the front-end user interface.
   - Integrating server-side data processing using AWS.

## Tools
Kotlin and Android Studio are used to create the application on the Android platform, more specifically, a Google Pixel 7A.

## Directory Files and Locations
The directory, app/src/main, contains the following files:

1. assets
   - sprite_images
       - 1.1.1 exit-door-svgrepo-com.svg
   - style.json
2. java/com/example/wlmap
   - Graph.kt
      - Create a graph where the nodes are the navigation points (ID, longitude, latitude, and set of neighboring nodes) on the map and the edge is the Haversine distance between the two nodes. Afterward, we use Dijkstra's Algorithm to navigate the shortest path between two points and return a list of points that needed to take.
   - LocationPermissionHelper.kt
   - MainActivity.kt
   - MqttHandler.kt
   - NavPoint.kt
      - An abstract data class that works as a constructor to navigate the path between two points.
3. res
   - drawable
   - layout
   - mipmap-anydpi-v26
   - mipmap-hdpi
   - mipmap-mdpi
   - mipmap-xhdpi
   - mipmap-xxhdpi
   - mipmap-xxxhdpi
   - values-night
   - values
   - xml
4. AndroidManiest.xml

## Deployment Instructions

1. Download and install [Android Studio](https://developer.android.com/studio)
   - For Mac users: Download the appropriate installer based on your chipset (Intel Chip or Apple Chip).
   - Follow installer instructions during installation.
   - This app is compatible with the Pixel 7a (API 35).

2. Clone the appropriate repository.

3. Allow Gradle to install and update the AGP (Android Gradle Plugin) to version 8.6 if prompted
   - If you are not prompted to update the AGP, follow these instructions:
     - Select the "Tools" drop-down menu on the top of the IDE.
     - Select "AGP Upgrade Assistant."
     - Select version 8.6.
     - Select "Run selected steps."
     - After the update is complete, select "Refresh."

4. Select "Device Manager" on the right-side app bar and install the device emulator.
   - If there are any other created devices, end their processes and remove them.
   - Click the "+" to and select "Create Virtual Device."
   - Select the "Phone" category and select the Pixel 7a (API 35).
   - Press "Finish."
   - After the device is installed, select the "play" button next to the installed device to begin running it.

## Project Roadmap

### User UI-UX
- [x] Enable the feature to fill in the information on the Share Data page.
- [x] Integrating the interactive map fragment into the WLMap button.

### User readings
- [x] User can view and record gyroscope and accelerometer readings.

### User click-ability/user search navigation
- [x] User can select any point in Davis Hall and can get navigational directions.
- [x] The point the user selects is marked and displayed with a circle.
- [x] Latitude and longitude coordinates are displayed on the point that the user selects.
- [x] User can travel to any point within a room in Davis Hall.

