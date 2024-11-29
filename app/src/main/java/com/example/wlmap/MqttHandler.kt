package com.example.wlmap

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttHandler {

    private var client: MqttClient? = null
    private val subscribedTopics = mutableSetOf<String>() // Keep track of subscribed topics
    var onMessageReceived: ((String, String) -> Unit)? = null // Includes topic in callback

    fun connect(brokerUrl: String, clientId: String) {
        try {
            if (client?.isConnected == true) {
                println("MQTT client already connected.")
                return
            }

            // Setup persistent layer
            val persistence = MemoryPersistence()

            // Initialize MQTT client
            client = MqttClient(brokerUrl, clientId, persistence)

            // Setup connection options
            val connectOptions = MqttConnectOptions().apply {
                isCleanSession = true
                connectionTimeout = 10
                keepAliveInterval = 20
            }

            // Set callback
            client?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    println("Connection lost: ${cause?.message}")
                    attemptReconnect(brokerUrl, clientId)
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic != null && message != null) {
                        onMessageReceived?.invoke(topic, message.toString())
                        println("Message received on topic $topic: ${message.toString()}")
                    } else {
                        println("Received null topic or message.")
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    println("Message delivery completed.")
                }
            })

            // Connect the client
            client?.connect(connectOptions)
            println("Connected to broker: $brokerUrl")

        } catch (e: MqttException) {
            println("Error connecting to MQTT broker: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun attemptReconnect(brokerUrl: String, clientId: String) {
        println("Attempting to reconnect...")
        try {
            client?.reconnect()
        } catch (e: MqttException) {
            println("Reconnection failed. Retrying in 5 seconds...")
            Thread.sleep(5000)
            connect(brokerUrl, clientId) // Retry connection
        }
    }

    fun disconnect() {
        try {
            if (client?.isConnected == true) {
                client?.disconnect()
                println("MQTT client disconnected.")
            }
        } catch (e: MqttException) {
            println("Error during MQTT disconnection: ${e.message}")
            e.printStackTrace()
        }
    }

    fun publish(topic: String, message: String) {
        try {
            if (client?.isConnected == true) {
                val mqttMessage = MqttMessage(message.toByteArray()).apply {
                    qos = 1
                    isRetained = false
                }
                client?.publish(topic, mqttMessage)
                println("Message published to $topic: $message")
            } else {
                println("Client is not connected. Cannot publish message.")
            }
        } catch (e: MqttException) {
            println("Error publishing message: ${e.message}")
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String) {
        try {
            if (client?.isConnected == true) {
                if (!subscribedTopics.contains(topic)) {
                    client?.subscribe(topic)
                    subscribedTopics.add(topic)
                    println("Subscribed to topic: $topic")
                } else {
                    println("Already subscribed to topic: $topic")
                }
            } else {
                println("Client is not connected. Cannot subscribe to topic.")
            }
        } catch (e: MqttException) {
            println("Error subscribing to topic $topic: ${e.message}")
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            if (client?.isConnected == true) {
                if (subscribedTopics.contains(topic)) {
                    client?.unsubscribe(topic)
                    subscribedTopics.remove(topic)
                    println("Unsubscribed from topic: $topic")
                } else {
                    println("Not subscribed to topic: $topic")
                }
            } else {
                println("Client is not connected. Cannot unsubscribe from topic.")
            }
        } catch (e: MqttException) {
            println("Error unsubscribing from topic $topic: ${e.message}")
            e.printStackTrace()
        }
    }
}
