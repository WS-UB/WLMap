package com.example.wlmap

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

    fun connect(brokerUrl: String?, clientId: String?) {
        try {
            //setup persistent layer
            val persistence = MemoryPersistence()

            //initialize MQTT client
            client = MqttClient(brokerUrl, clientId, persistence)

            //setup connection options
            val connectOptions = MqttConnectOptions()
            connectOptions.isCleanSession = true

            // Set callback
            client?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    // Handle connection loss
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // Handle incoming messages
                    onMessageReceived?.invoke(message.toString())
                    println("Message received: ${message?.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Handle completed delivery
                }
            })

            client!!.connect()

        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            client!!.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String?, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            client!!.publish(topic, mqttMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String?) {
        try {
            client!!.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}