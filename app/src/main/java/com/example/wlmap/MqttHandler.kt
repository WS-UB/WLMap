package com.example.wlmap

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
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

    fun connect(brokerUrl: String?, clientId: String?, esUrl: String, esIndex: String) {
        if (brokerUrl.isNullOrEmpty() || clientId.isNullOrEmpty()) {
            println("Broker URL or Client ID cannot be null or empty")
            return
        }

        Thread {
            try {
                // Setup persistence
                val persistence = MemoryPersistence()

                // Initialize MQTT client
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


    // Method to send the message to Elasticsearch
    private fun sendToElasticsearch(esUrl: String, esIndex: String, message: String) {
        // Create a JSON object with the message
        val jsonObject = JSONObject().put("message", message)

        // Create the request body
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())

        // Build the HTTP request to Elasticsearch
        val request = Request.Builder()
            .url("$esUrl/$esIndex/_doc")  // Posting to the index in Elasticsearch
            .post(requestBody)
            .build()

        // Execute the request asynchronously
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to send data to Elasticsearch: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Use 'val body = response.body' to avoid using 'body()'
                val body = response.body
                if (body != null) {
                    println("Data sent to Elasticsearch, response: ${body.string()}")
                } else {
                    println("Response body is null")
                }
            }
        })
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
