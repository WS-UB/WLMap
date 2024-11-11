package com.example.wlmap

class KalmanFilter(private val processNoiseCovariance: Double, private val measurementNoiseCovariance: Double) {
    // State estimate (x, y position)
    private var x: Double = 0.0
    private var y: Double = 0.0

    // Error covariance matrix
    private var P: Array<Array<Double>> = arrayOf(
        arrayOf(1.0, 0.0),
        arrayOf(0.0, 1.0)
    )

    // State transition matrix (Assumes constant velocity model)
    private val F: Array<Array<Double>> = arrayOf(
        arrayOf(1.0, 0.0),
        arrayOf(0.0, 1.0)
    )

    // Measurement matrix (Assumes direct measurement of x and y)
    private val H: Array<Array<Double>> = arrayOf(
        arrayOf(1.0, 0.0),
        arrayOf(0.0, 1.0)
    )

    // Measurement noise covariance
    private val R: Array<Array<Double>> = arrayOf(
        arrayOf(measurementNoiseCovariance, 0.0),
        arrayOf(0.0, measurementNoiseCovariance)
    )

    // Process noise covariance
    private val Q: Array<Array<Double>> = arrayOf(
        arrayOf(processNoiseCovariance, 0.0),
        arrayOf(0.0, processNoiseCovariance)
    )

    // Prediction step
    fun predict() {
        // Predicted state (no motion model, just a prediction of the current state)
        val predictedX = x
        val predictedY = y

        // Predicted error covariance (no motion model, but this could be adjusted based on velocity)
        P[0][0] += Q[0][0]
        P[1][1] += Q[1][1]

        x = predictedX
        y = predictedY
    }

    // Update step with new measurement (GPS x, y)
    fun update(measuredX: Double, measuredY: Double) {
        // Innovation or residual
        val innovationX = measuredX - x
        val innovationY = measuredY - y

        // Calculate the Kalman gain (K)
        val S = P[0][0] + R[0][0] // Simplified as measurement noise is scalar for each dimension
        val Kx = P[0][0] / S
        val Ky = P[1][1] / S

        // Update estimate with new measurement
        x += Kx * innovationX
        y += Ky * innovationY

        // Update error covariance
        P[0][0] -= Kx * P[0][0]
        P[1][1] -= Ky * P[1][1]
    }

    // Return the current state estimate (x, y)
    fun getState(): Pair<Double, Double> {
        return Pair(x, y)
    }
}

fun main() {
    // Create a Kalman filter with arbitrary process noise and measurement noise values
    val kalmanFilter = KalmanFilter(processNoiseCovariance = 10.0, measurementNoiseCovariance = 1.0)

    // Simulated GPS measurements (noisy)
    val gpsMeasurements = listOf(
        Pair(0.0, 0.0),
        Pair(1.0, 1.0),
        Pair(2.0, 2.1),
        Pair(3.0, 3.2),
        Pair(4.0, 3.9),
        Pair(5.0, 5.0)
    )

    for (measurement in gpsMeasurements) {
        // Predict the next state
        kalmanFilter.predict()

        // Update the filter with the new GPS measurement
        kalmanFilter.update(measurement.first, measurement.second)

        // Get the current estimate (smoothed position)
        val (estimatedX, estimatedY) = kalmanFilter.getState()
        println("Estimated Position: (x: $estimatedX, y: $estimatedY)")
    }
}









