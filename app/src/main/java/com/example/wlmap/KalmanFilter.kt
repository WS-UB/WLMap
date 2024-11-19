package com.example.wlmap

import android.location.Location

class KalmanFilter {

    // State variables: [latitude, longitude, velocity_lat, velocity_lon]
    private var state = floatArrayOf(0f, 0f, 0f, 0f)  // Initial state

    // Covariance matrix (initial uncertainty estimate)
    private val covariance = Array(4) { FloatArray(4) { 1f } }

    // Process noise matrix (how much we trust the prediction model)
    private val processNoise = Array(4) { FloatArray(4) { 100f } }

    // Measurement noise matrix (how much we trust the GPS data)
    private val measurementNoise = Array(4) { FloatArray(4) { 0.000001f } }

    // Identity matrix for matrix calculations
    private val identity = Array(4) { FloatArray(4) { if (it == it) 1f else 0f } }

    // Prediction step (use accelerometer data)
    fun predict(acceleration: FloatArray, dt: Float) {
        // Update velocity based on acceleration (v = u + at)
        state[2] += acceleration[0] * dt  // velocity_lat
        state[3] += acceleration[1] * dt  // velocity_lon

        // Update position based on velocity (lat = lat + v * dt)
        state[0] += state[2] * dt  // latitude
        state[1] += state[3] * dt  // longitude

        // Predict the covariance (uncertainty)
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                covariance[i][j] += processNoise[i][j]
            }
        }
    }

    // Update step (correct with GPS measurements)
    fun update(measurement: FloatArray) {
        // Innovation (difference between predicted state and measurement)
        val y = FloatArray(2) { measurement[it] - state[it] }  // Only use lat, lon

        // Kalman gain calculation
        val kalmanGain = Array(4) { FloatArray(4) }
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                // Calculate Kalman gain based on covariance and measurement noise
                kalmanGain[i][j] = covariance[i][j] / (covariance[i][i] + measurementNoise[i][i])
            }
        }

        // Update state estimate using Kalman Gain and innovation
        for (i in 0 until 2) {  // Only update lat/lon (0,1 indices)
            state[i] += kalmanGain[i][i] * y[i]
        }

        // Update covariance (reduce uncertainty after correction)
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                covariance[i][j] = (identity[i][j] - kalmanGain[i][j]) * covariance[i][j]
            }
        }
    }

    // Return the filtered state (latitude, longitude)
    fun getState(): FloatArray {
        return state
    }
}



fun main() {
    val initialLatitude = 42.998709f
    val initialLongitude = -78.795684f

    val kalmanFilter = KalmanFilter()

    // Simulate movement: Accelerometer gives small changes (assume 1 second interval for simplicity)
    val acceleration = floatArrayOf(0.01f, 0.01f)  // Small accelerations in lat and lon directions
    val dt = 1f  // Time interval (1 second)

    // Run the Kalman filter for 10 steps, simulating small movements
    for (i in 1..10) {
        // Predict position based on acceleration (sensor data)
        kalmanFilter.predict(acceleration, dt)

        // Simulate GPS update: Slight change in position
        val newLatitude = initialLatitude + i * 0.0001f  // Increment latitude
        val newLongitude = initialLongitude + i * 0.0001f  // Increment longitude
        kalmanFilter.update(floatArrayOf(newLatitude, newLongitude))

        // Get the filtered position
        val filteredState = kalmanFilter.getState()
        println("Filtered Latitude: ${filteredState[0]}, Longitude: ${filteredState[1]}")
    }
}









