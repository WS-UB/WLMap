# WLMap Feature Branch: Update Location

## Description

This branch updates the user's location on the map. The goal of this feature is to retrieve the information sent from MinIO, parse it into accessible information, and send the information back to the application so that the map can update the latest GPS coordinates based on the coordinates from MinIO database. 

## Key Changes
This feature introduces the following changes:
- Reveal the blue dot at specific coordinates on Davis Hall to show that the information is successfully retrieved from MinIO and processed thoroughly
- A toggle button in Data Collection that allows the user to continue sending information to the server or not.

## Problem Definition

We aim to create a stable Android mobile application for indoor navigation and WiFi-based data collection. This semester, we will focus on resolving technical issues with the data collection app, enhancing the user interface, and integrating server-side data processing using AWS. Additionally, we will develop a functional navigational interface similar to Google Maps, enabling users to track their indoor location within large buildings like malls and airports. In the long term, we aspire to deploy a fully functional, scalable system that enables seamless indoor navigation by utilizing WiFi signals and real-time data collection. By leveraging machine learning models, we will enhance accuracy in indoor positioning, ensuring privacy and efficiency through the use of hashed user data. Our goal is to provide a robust and open-source platform that can be adapted for various large-scale indoor environments.

## Demographics

This application will be designed and used by University at Buffalo students and faculty, with the goal being that the application is made into an open-source platform that can be adapted for various large-scale indoor environments. 

## Goals and Challenges
Our current goals for this project include:
   - Resolving technical issues with the data collection app.
   - Developing a functional navigational interface.
   - Making the front-end interface easier to understand and more user-friendly.
   - Storing user/device data using server integration.
   - Integrating server-side data processing using PySpark.
   - Retrieving server-side data to update user position.
   - Gather and store user navigation data in a database.
   - Use stored reliable data for an A.I. training model.


## Technology and Development Plans
Our current technology and development plans include:
   - Kotlin: Watch video tutorials and read documentation for better understanding.
   - UI/UX design: Watch video tutorials and refer to Figma UI outline as a reference for design features.
   - MQTT: Read MQTT documentation.
   - MinIO: Read MinIO documentation.



## Features
This app can provide the following:
   - Accurate mapping of Davis Hall.
   - Navigational directions to any point within Davis Hall.
   - Record gyroscopic and accelerometer information.
   - An interactable UI.
   - Navigational map option.
   - Data collection map option.


## Tools
Kotlin and Android Studio are used to create the application on the Android platform, more specifically, a Google Pixel 7A.

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
  
5. In your terminal, cd into the directory of your WLMap repository and type the following command.

```
   git checkout feature/Update-Location
```

6. Open and run the WLMap application in Android Studio, selecting the "Navigation" option on the home screen of WLMap.

7. Once running the application, open LogCat and observe the following:
   - The user's GPS location is being printed on the server log.
   - The log indicates if the MacAddress, timestamp, GPS, accelerometer, and gyroscope are being sent to MQTT
   - The log also shows if the GPS location is being sent back to the app
  
8. Alternatively, click on the menu icon on the top left corner of the app, return to Home, and click on Data Collection.

9. Once running the application, click on the green Send Loc button to stop sending info to the server and vice versa. For clarification, observe the following:
   - The user's GPS location is being printed on the server log.
   - The log indicates if the MacAddress, timestamp, GPS, accelerometer, and gyroscope are being sent to MQTT
     

## Project Roadmap

### User UI-UX
- [x] Enable the feature to fill in the information on the Share Data page.
- [x] Integrating the interactive map fragment into the Navigation and Data Collection button.
- [x] The Data Collection page can send the user's rate of confidence about their location on the map to the log.
- [x] The Send Loc button in the Data Collection page can now be toggled to enable user to send data to the server or not.

### User readings
- [x] User can view and record gyroscope and accelerometer readings.

### User click-ability/user search navigation
- [x] User can select any point in Davis Hall and can get navigational directions.
- [x] The point the user selects is marked and displayed with a circle.
- [x] Latitude and longitude coordinates are displayed on the point that the user selects.
- [x] User can travel to any point within a room in Davis Hall.
- [x] User's GPS data is cleaned/filtered using Kalman Filtering
- [ ] User's GPS data updates consistently and accurately.

### Data Readings sent to MQTT client
- [x] GPS data is sent to MQTT server.
- [x] Accelerometer data is sent to MQTT server.
- [x] Gyroscope data is sent to MQTT server.
- [x] User ID is sent to MQTT server.
- [x] All the information above is stored in the MinIO database as parquet files
- [x] Read parquet files from MinIO and send GPS coordinates back to the app
- [x] Update the position on the map based on the GPS coordinates retrieved from the parquet files in MinIO
