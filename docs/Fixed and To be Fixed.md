Completed Fixes and Items Tested:
- Map not loading on the Phone: This was fixed by using a stronger WIFI connection. See troubleshooting.md for more information 
- The Gyroscope Data not sending: This was fixed by changing the rate at which the gyrocope and accelerator readings were sent to the server. They are now sent in the OnSensorChanged Method. 
- Verified that the location is sent to the server when the SendLoc button is pressed 
- Verified that the query is sent to the server to be recieved in the form of the device id
- Verified Wifi Data is sent using the wiros node to the server and then to minio (Unreliable)
- Verified app loads on the phone and works and the data mentioned above is sent to the wiloc server 

Needs to be fixed in the future:
- The Gathering of Wifi data from the router still only works some of the time and is ultimatley very buggy and unreliable. In the futire the use of the router will need to br researched in order to more consistenly get wifi readings. This is most likely an issue with the router configurations and wifi connections 
- The RPI also displays an overheating symbol when the CSI_node is left running for too long this is probably because of the rate and amount of data that is sending. 
