# WLMap

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
- Leveraging the A.I. training model’s predictions to continuously update and display predicted user location on the map.

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
- Updating the prediction user location consistency
- Showing the user location with the phone gps

## Tools

Kotlin and Android Studio are used to create the application on the Android platform, more specifically, a Google Pixel 7A.

## Deployment Instructions

1. Download and install [Android Studio](https://developer.android.com/studio)

   - For Mac users: Download the appropriate installer based on your chipset (Intel Chip or Apple Chip).
   - Follow installer instructions during installation.
   - This app is compatible with the Pixel 7a (API 35).

2. Clone the appropriate repository.

3. Allow Gradle to install and update the AGP (Android Gradle Plugin) to the lastest version (**version 8.8 as of 05/01/25**) if prompted

   - If you are not prompted to update the AGP, follow these instructions:
   - Select the "Tools" drop-down menu on the top of the IDE.
   - Select "AGP Upgrade Assistant."
   - Select the latest version.
   - Select "Run selected steps."
   - After the update is complete, select "Refresh."

4. Select **_Device Manager_** on the right-side app bar and install the device emulator.

   - If there are any other created devices, end their processes and remove them.
   - Click the "+" to and select "Create Virtual Device."
   - Select the "Phone" category and select the Pixel 7a (API 35).
   - Press "Finish."
   - After the device is installed, select the "play" button next to the installed device to begin running it.

**OPTIONAL**: Instead of utiilzing the **_Device Manager_**, you can utilize either the Google Pixel 7a or Google Pixel 8a Android Phones that are avaliable in Davis 113X. Make a direct connection from the phone to the computer running Android Studio via USB-C.
**Make sure Dr. Roshan Ayyalasomayajula knows you are in possession of the phones before taking them out of the lab for testing**.

6. Open and run the WLMap application in Android Studio, selecting the "Data Collection" option on the home screen of WLMap.

7. Once running the application, open LogCat and observe that GPS and IMU data is being streamed from the phone via MQTT.

## Branch READMEs

For specific inquiries on the specific feature branches, check below for the following links.

1. [debugging-Harry.md](/docs/debugging-Harry.md)
1. [Fixed and To be Fixed.md](/docs/Fixed%20and%20To%20be%20Fixed.md)
1. [Harry_Kalman_Filtering_readme.md](/docs/Harry_Kalman_Filtering_readme.md)
1. [random_deviceID.md](/docs/random_deviceID.md)
1. [Troubleshooting.md](/docs/Troubleshooting.md)
1. [Update-Location-documentation.md](/docs/Update-Location-documentation.md)
1. [Prediction_gps_Yufeng,md](/docs/Prediction_gps_Yufeng.md)

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
- [x] User's GPS data is cleaned/filtered using Kalman Filtering
- [x] User's GPS data updates consistently and accurately.

### Data Readings sent to MQTT client

- [x] GPS data is sent to MQTT server.
- [x] Accelerometer data is sent to MQTT server.
- [x] Gyroscope data is sent to MQTT server.

### Data Receiving from MQTT server
- [x] Prediction GPS data is receive from MQTT server

