package com.example.wlmap

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttHandler {
    private var client: MqttClient? = null
    var onMessageReceived: ((String) -> Unit)? = null
    private val okHttpClient = OkHttpClient()

    fun connect(brokerUrl: String?, clientId: String?) {
        if (brokerUrl.isNullOrEmpty() || clientId.isNullOrEmpty()) {
            println("Broker URL or Client ID cannot be null or empty")
            return
        }

        Thread {
            try {
                // Setup persistence
                val persistence = MemoryPersistence()

            //initialize MQTT client
            client = MqttClient(brokerUrl, clientId, persistence)

                // Setup connection options
                val connectOptions = MqttConnectOptions()
                connectOptions.isCleanSession = true

                // Connect to the broker
                client?.connect(connectOptions)

                // Subscribe to the topic
                client?.subscribe("test/topic")

                // Log successful connection
                println("Connected to broker: $brokerUrl")

            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }.start() // Run the connection in a background thread
    }

    fun disconnect() {
        try {
            client?.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, message: String) {
        if (client?.isConnected == true) {
            val mqttMessage = MqttMessage(message.toByteArray())
            client?.publish(topic, mqttMessage)
        } else {
            println("MQTT Client is not connected")
        }
    }

    fun subscribe(topic: String?) {
        try {
            client?.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}