package com.example.wlmap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class SendDataFragment : Fragment() {
    private lateinit var serverIdInput: EditText
    private lateinit var userIdInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var commentInput: EditText
    private lateinit var sendDataButton: Button
    private lateinit var mqttHandler: MqttHandler  // Declare as lateinit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_send_data, container, false)

        // Initialize views using the inflated layout
        serverIdInput = view.findViewById(R.id.fill_server_id)
        userIdInput = view.findViewById(R.id.fill_user_id)
        passwordInput = view.findViewById(R.id.fill_password)
        commentInput = view.findViewById(R.id.fill_comment)
        sendDataButton = view.findViewById(R.id.send_data_button)

        // Initialize MQTT handler and connect to the broker
        mqttHandler = MqttHandler()
        mqttHandler.connect(
            brokerUrl = "tcp://128.205.218.189:1883", // or "tcp://<IP>:1883"
            clientId = "001000",
        )

        sendDataButton.setOnClickListener {
            // Get the input values
            val serverID = serverIdInput.text.toString()
            val userID = userIdInput.text.toString()
            val password = passwordInput.text.toString()
            val comment = commentInput.text.toString()

            // Create a message string for the input data
            val message = "server_id: $serverID\nuser_id: $userID\npassword: $password\ncomment: $comment"

            // Publish the message to the MQTT topic
            mqttHandler.publish("test/topic", message)

            // Log to confirm the message was sent
            Log.i("MQTTPublish", "Message Sent:\n\n$message")
        }

        return view
    }
}