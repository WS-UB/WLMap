# WLMap

## Description
WLMap (Wireless Localization Map) is an indoor navigation app that is used to determine the precise position or location of an object or device within an indoor environment, often using Wi-Fi, Bluetooth, or RFID. 

## Features
Currently, this app can provide accurate mapping of Davis Hall, real-time location tracking, ground truth survey, and optimal route navigation.

## Goals and Challenges
Our current goals for this project include:
- Devise a plan to provide an exact location and improve its precision based on students' cellular phones.
- Designing the front end of a navigational map application.
- Setup server-to-user connection for the facilitation of future location precision and incorporation of ML algorithms.
- Ground truth survey and storing the info in containers.

## Tools
For the front end, we use Kotlin and Figma to create the application on Android. For the back end, we use Python and Docker to process data. 

## Deployment Instructions
1. Download and install [Android Studio](https://developer.android.com/studio)
   - For Mac users: Download the appropriate installer based on your chipset (Intel Chip or Apple Chip).
   - Follow installer instructions during installation.
   - This app is compatible with the pixel 7a API 35

2. Clone the appropriate repository

3. Allow Gradle to install and update the AGP (Android Gradle Plugin) if prompted
   - Update the AGP preferably to version 8.6

4. Select "Device Manager" on the right-side app bar and install the device emulator
   - Select the Pixel 7a (API 35)

## Project Status
- [X] Enable the feature to fill in the information on the Share Data page.
- [ ] Integrating the interactive map fragment into the WLMap button.
