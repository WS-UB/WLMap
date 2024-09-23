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
      - Create a graph where the nodes are the navigation points (ID, longitude, latitude, and set of neighboring nodes) on the map and the edge is the Haversine distance between the two nodes. Afterward, we use Dijkstra's Algorithm to navigate the shortest path between two points and return a list of points that needed to take.
   - LocationPermissionHelper.kt
   - MainActivity.kt
   - MqttHandler.kt
      - In order to use the MqttHandler to send data to the server you must first start the mqtt broker in the server you wish to send data to and then be sure to change the ServerUri variable in MainActivty.Kt to the server address. 
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
   - This app is compatible with the pixel 7a API 35.

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
   - Select the "Phone" category and select the Pixel 7a.
   - Press "Finish."
   - After the device is installed, select the "play" button next to the installed device to begin running it.


   


