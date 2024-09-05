# WLMap
This is a navigational android app used to naviagate inside buildings to find classrooms, stairs, elevators, and bathrooms. 
User Guide: Contains necessary details and information about the project and tools required to install.

The directory, app/src/main, contains the following files:

1. assets
   - sprite_images
       - 1.1.1 exit-door-svgrepo-com.svg
   - style.json
2. java/com/example/wlmap
   - Graph.kt
   - LocationPermissionHelper.kt
   - MainActivity.kt
   - MqttHandler.kt
   - NavPoint.kt
      - A class constructor that designs navigation points consists of three variables: ID (String), nodePoint (Object named Point), and neighbors (Set of strings) 
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
   - This app is compatible with the pixel 7a API 35

2. Clone the appropriate repository

3. Allow Gradle to install and update the AGP (Android Gradle Plugin) if prompted
   - Update the AGP preferably to version 8.6

4. Select "Device Manager" on the right-side app bar and install the device emulator
   - Select the Pixel 7a (API 35)


   


