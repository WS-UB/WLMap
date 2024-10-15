# WLMap

## Description

This is a navigational android app used to navigate inside buildings to find classrooms, stairs, elevators, and bathrooms. 

## Problem Definition

We aim to create a stable Android mobile application for indoor navigation and WiFi-based data collection. This semester, we will focus on resolving technical issues with the data collection app, enhancing the user interface, and integrating server-side data processing using AWS. Additionally, we will develop a functional navigational interface similar to Google Maps, enabling users to track their indoor location within large buildings like malls and airports. In the long term, we aspire to deploy a fully functional, scalable system that enables seamless indoor navigation by utilizing WiFi signals and real-time data collection. By leveraging machine learning models, we will enhance accuracy in indoor positioning, ensuring privacy and efficiency through the use of hashed user data. Our goal is to provide a robust and open-source platform that can be adapted for various large-scale indoor environments.

## Demographics

This application will be designed and used by University at Buffalo students and faculty, with the goal being that the application is made into an open-source platform that can be adapted for various large-scale indoor environments. 

## Goals and Challenges
Our current goals for this project include:
   - Resolving technical issues with the data collection app.
   - Developing a functional navigational interface.
   - Making the front-end interface easier to interact with.
   - Storing user/device data using server integration.
   - Integrating server-side data processing using AWS.
   - Retrieving server-side data to update user position.
   - Gather and store user navigation data in a database.
   - Use stored reliable data for an A.I. training model.


## Technology and Development Plans
Our current technology and development plans include:
   - Kotlin: Watch video tutorials and read documentation for better understanding.
   - UI/UX design: Watch video tutorials and refer to Figma UI outline as a reference for design features.
   - MQTT: Read MQTT documentation and refer to Dr. Roshan for better understanding and implementation.



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

## Project Roadmap

### User UI-UX
- [x] Enable the feature to fill in the information on the Share Data page.
- [x] Integrating the interactive map fragment into the Navigation and Data Collection button.
- [x] The Data Collection page can send the user's rate of confidence about their location on the map to the log.

### User readings
- [x] User can view and record gyroscope and accelerometer readings.

### User click-ability/user search navigation
- [x] User can select any point in Davis Hall and can get navigational directions.
- [x] The point the user selects is marked and displayed with a circle.
- [x] Latitude and longitude coordinates are displayed on the point that the user selects.
- [x] User can travel to any point within a room in Davis Hall.

### Data Readings sent to MQTT client
- [x] GPS data is sent to MQTT server.
- [x] Accelerometer data is sent to MQTT server.
- [x] Gyroscope data is sent to MQTT server.
