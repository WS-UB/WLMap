package com.example.wlmap

import android.location.Location

class KalmanFilter(initialLat: Double, initialLon: Double) {

    private var latitude = initialLat
    private var longitude = initialLon
    private var latitudeError = 100.0 // Initial error estimate for latitude
    private var longitudeError = 100.0 // Initial error estimate for longitude
    private val processNoise = 1e-5 // Process noise (how much you trust the model)
    private val measurementNoise = 1e-3 // Measurement noise (how much you trust the GPS readings)

    // Update the Kalman Filter with a new measurement (latitude and longitude)
    fun update(lat: Double, lon: Double) {
        // Update for latitude
        val kalmanGainLat = latitudeError / (latitudeError + measurementNoise)
        latitude += kalmanGainLat * (lat - latitude) // Correct the estimate
        latitudeError = (1 - kalmanGainLat) * latitudeError + Math.abs(latitude - lat) * processNoise

        // Update for longitude
        val kalmanGainLon = longitudeError / (longitudeError + measurementNoise)
        longitude += kalmanGainLon * (lon - longitude) // Correct the estimate
        longitudeError = (1 - kalmanGainLon) * longitudeError + Math.abs(longitude - lon) * processNoise
    }

    // Get the filtered latitude and longitude
    fun getFilteredLocation(): Location {
        val location = Location("Kalman")
        location.latitude = latitude
        location.longitude = longitude
        return location
    }
}


//fun main() {
//    // Create a Kalman filter with arbitrary process noise and measurement noise values
//    val kalmanFilter = KalmanFilter(42.998715,-78.795676)
//
//    // Simulated GPS measurements (noisy)
//
//
//    kalmanFilter.update(measurement.first, measurement.second)
//
//        // Get the current estimate (smoothed position)
//        val (estimatedX, estimatedY) = kalmanFilter.getState()
//        println("Estimated Position: (x: $estimatedX, y: $estimatedY)")
//    }
//}









