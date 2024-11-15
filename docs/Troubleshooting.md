Common Issues and Troubleshooting 

Issues when Running the app on the phone: 

- The app is not getting Gyroscope readings
  - This issue was fixed by changing the rate at which the sensor readings occur
  - The sensor readings are read and sent to the server in the OnSensorChanged Method This method sends the values to the server whenever a reading is sent. The accelerator readings occur more often than the gyroscope readings
  - This may cause issues when progressing further when synchronizing the data and as we progress further into Data Collection and training the model but as of now there are no known no issues.
 
- Issues With Map freezing/or not showing up on the phone:
  - If the map isn't showing up on the phone you must connect to a stronger more stable Wifi. At UB this means you must be connected to Eduoram.
  - The map will freeze when running the app on the phone through android studio. You must build and download the app on android studio while your phone is connected to your computer. Then discconect the phone from the computer and run the app seperatly from android studio.
  - If you have to debug test the app in the emulator and use logcat. You won't be able to see errors and logs when the phone is disconnected to android studio.
 
Issues With Router and Getting Wifi Data: 

Before trying these tips read the wiros node repo and ensure you set up everything correctly according to it. https://github.com/ucsdwcsng/wiros_csi_node

- "Refued SSH Access" and "host did not respond to a ping" when starting Wiros Node
  - Ensure the router is turned on and ping the router on the rpi and ensure it is able to ping.
  - Run the ifconfig command and ensure your Eth0 value is set to the ip address of the router
  - if its not set correct the command is sudo ifconfig eth0 <ip_address> netmask 255.255.255.0
  - Ensure that the ip address in the launch script is set to the value of the device you want to get wifi data from
  - note that you must run catkin build everytime the launch script is changed in order for the changes to be applied
  - If the launch node is still not starting consult the wiros_csi_node repo and ensure that you followed every step correctly
 
- Not recieving any wifi data through the wiros csi node
  - This means that there is no wifi activity on the channel that the wiros node is listening to
  - To fix this you must make sure your devices wifi matches the paramenters in the launch script, ie; change the bandwidth frequecy and channel to match that of your devices
  - Consult the Wiros_csi_node repo for information about each parameter in the launch script to double check your launch script is listening for the correct device parameters
  - If your unable to connect the phone and recieve wifi data from it you can connect the rpi to UB Wifi and receive wifi data from it for testing purposes. 
