# WLMap Feature Branch: send-IMU-GPS-Data

## Description

This branch implements the sending of IMU (gyroscope and accelerometer) and GPS data to the WILOC MQTT server for the WLMap application. The goal of this feature is to send IMU and GPS data along with a timestamp (Year-Month-Day, Hour-Minute-Second.Millisecond --> i.e. 2024-10-6 18:58:31.297) and device MAC address, which the data will be sent to the WILOC MQTT server. In the backend, the IMU and GPS data that was sent will be paired and synchronized with incoming WiFi data within 100ms of each other, which will be used for data collection.

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


## Key Changes
This feature introduces the following changes:

   - A user location observer that obtains GPS location information (latitude and longitude).
   - GPS data alongside a timestamp (Year-Month-Day, Hour-Minute-Second.Millisecond --> i.e. 2024-10-6 18:58:31.297) is sent to the WILOC MQTT server.
   - IMU (accelerometer and gyroscope) data alongside a timestamp (Year-Month-Day, Hour-Minute-Second.Millisecond --> i.e. 2024-10-6 18:58:31.297) is sent to the WILOC MQTT server.
   - The device MAC address is sent to the WILOC MQTT server (Helps differentiate different devices during data collection).


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
   git checkout feature/send-IMU-GPS-data
```

6. To begin collecting GPS and IMU data, open and run the WLMap application in Android Studio, selecting the "Data Collection" option on the home screen of WLMap.

7. To confirm that data is being streamed to the WILOC MQTT server, open LogCat in Android Studio and observe if GPS, accelerometer, and gyroscope data is being sent.

8. If the MQTT server is not creating a connection, restart the WLMap application on Android Studio.


## Project Roadmap

### User UI-UX
- [x] Enable the feature to fill in the information on the Share Data page.
- [x] Integrating the interactive map fragment into the Navigation and Data Collection button.
- [x] The Data Collection page can send the user's rate of confidence about their location on the map to the log.

### User readings
- [x] User can view and record gyroscope and accelerometer readings.
- [x] User gyroscope, accelerometer, and GPS data are streamed to the MQTT Wiloc server.

### User click-ability/user search navigation
- [x] User can select any point in Davis Hall and can get navigational directions.
- [x] The point the user selects is marked and displayed with a circle.
- [x] Latitude and longitude coordinates are displayed on the point that the user selects.
- [x] User can travel to any point within a room in Davis Hall.
