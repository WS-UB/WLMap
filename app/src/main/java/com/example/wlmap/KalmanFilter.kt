package com.example.wlmap


data class KalmanFilterState(
    var position: DoubleArray = DoubleArray(2), // x, y position
    var velocity: DoubleArray = DoubleArray(2), // x, y velocity
    var covariance: Array<DoubleArray> = Array(4) { DoubleArray(4) }
)

class KalmanFilter(
    private val processNoiseCovariance: Array<DoubleArray> = Array(4) { DoubleArray(4) },
    var measurementNoiseCovariance: Array<DoubleArray> = Array(2) { DoubleArray(2) },
    private val transitionMatrix: Array<DoubleArray> = Array(4) { DoubleArray(4) },
    private val measurementMatrix: Array<DoubleArray> = Array(2) { DoubleArray(4) }
) {
    val state = KalmanFilterState()

    init {
        // Initializing transition matrix for a simple constant velocity model
        transitionMatrix[0][0] = 1.0
        transitionMatrix[0][1] = 0.0
        transitionMatrix[1][0] = 0.0
        transitionMatrix[1][1] = 1.0
        transitionMatrix[2][2] = 1.0
        transitionMatrix[3][3] = 1.0

        // Measurement matrix for GPS (position x, y)
        measurementMatrix[0][0] = 1.0
        measurementMatrix[1][1] = 1.0

        // Initialize covariance (large initial uncertainty)
        for (i in 0..3) {
            for (j in 0..3) {
                state.covariance[i][j] = 1000.0
            }
        }

        // Set process noise (model's uncertainty)
        processNoiseCovariance[0][0] = 1e-4
        processNoiseCovariance[1][1] = 1e-4
        processNoiseCovariance[2][2] = 1e-4
        processNoiseCovariance[3][3] = 1e-4

        // Set measurement noise (GPS accuracy, etc.)
        measurementNoiseCovariance[0][0] = 1.0 // x measurement noise
        measurementNoiseCovariance[1][1] = 1.0 // y measurement noise
    }

    // Prediction step
    fun predict(accelX: Double, accelY: Double) {
        // Predict position based on previous state (constant velocity model)
        val velocityX = state.velocity[0] + accelX
        val velocityY = state.velocity[1] + accelY
        state.position[0] += velocityX
        state.position[1] += velocityY

        // Update the velocity
        state.velocity[0] = velocityX
        state.velocity[1] = velocityY

        // Update the covariance
        for (i in 0..3) {
            for (j in 0..3) {
                state.covariance[i][j] += processNoiseCovariance[i][j]
            }
        }
    }

    // Update step with measurement (GPS)
    fun update(gpsX: Double, gpsY: Double) {
        // Innovation (Measurement residual)
        val z = doubleArrayOf(gpsX, gpsY) // GPS measurement

        // The predicted measurement is just the position (state.position)
        val predictedMeasurement = doubleArrayOf(state.position[0], state.position[1])

        // Calculate residual: z - predictedMeasurement
        val y = doubleArrayOf(z[0] - predictedMeasurement[0], z[1] - predictedMeasurement[1])

        // Measurement matrix H (2x4) - maps the state space to the measurement space
        val H = Array(2) { DoubleArray(4) }
        H[0][0] = 1.0  // Mapping position x
        H[1][1] = 1.0  // Mapping position y

        // Calculate S = H * P * H^T + R (Measurement residual covariance)
        val H_P_Ht = Array(2) { DoubleArray(2) }
        for (i in 0..1) {
            for (j in 0..1) {
                H_P_Ht[i][j] = H[i][0] * state.covariance[0][j] + H[i][1] * state.covariance[1][j]
            }
        }

        // Add measurement noise covariance R
        for (i in 0..1) {
            for (j in 0..1) {
                H_P_Ht[i][j] += measurementNoiseCovariance[i][j]
            }
        }

        // Calculate Kalman Gain K = P * H^T * S^(-1)
        val K = Array(4) { DoubleArray(2) }
        val S_inv = inverse(H_P_Ht) // S^(-1)

        for (i in 0..3) {
            for (j in 0..1) {
                K[i][j] = state.covariance[i][0] * S_inv[0][j] + state.covariance[i][1] * S_inv[1][j]
            }
        }

        // Update state estimate (only position, velocity, not all states)
        for (i in 0..1) { // Only update the first 2 elements for position
            state.position[i] += K[i][0] * y[0] + K[i][1] * y[1]
        }

        for (i in 2..3) { // Only update the last 2 elements for velocity
            state.velocity[i - 2] += K[i][0] * y[0] + K[i][1] * y[1]
        }

        // Update the covariance estimate (only for position and velocity)
        val identity = Array(4) { DoubleArray(4) }
        for (i in 0..3) {
            identity[i][i] = 1.0
        }

        // P = (I - K * H) * P
        val KH = Array(4) { DoubleArray(4) }
        for (i in 0..3) {
            for (j in 0..3) {
                KH[i][j] = K[i][0] * H[0][j] + K[i][1] * H[1][j]
            }
        }

        // P = P - K * H * P
        for (i in 0..3) {
            for (j in 0..3) {
                state.covariance[i][j] -= KH[i][j]
            }
        }
    }

    fun inverse(matrix: Array<DoubleArray>): Array<DoubleArray> {
        // Check if the matrix is 2x2
        if (matrix.size != 2 || matrix[0].size != 2) {
            throw IllegalArgumentException("Matrix must be 2x2")
        }

        val a = matrix[0][0]
        val b = matrix[0][1]
        val c = matrix[1][0]
        val d = matrix[1][1]

        // Calculate the determinant
        val det = a * d - b * c

        // If the determinant is zero, the matrix is singular and cannot be inverted
        if (det == 0.0) {
            throw ArithmeticException("Matrix is singular and cannot be inverted")
        }

        // Calculate the inverse
        val invDet = 1.0 / det
        val invMatrix = Array(2) { DoubleArray(2) }
        invMatrix[0][0] = d * invDet
        invMatrix[0][1] = -b * invDet
        invMatrix[1][0] = -c * invDet
        invMatrix[1][1] = a * invDet

        return invMatrix
    }



    // Get current position estimate
    fun getPosition(): DoubleArray {
        return state.position
    }
}

fun main() {
    // Initialize the Kalman Filter
    val kalmanFilter = KalmanFilter()

    // Simulated sensor data
    var accelerometerData = doubleArrayOf(0.2, 0.2) // accelX, accelY
    var gpsData = doubleArrayOf(5.0, 5.0) // gpsX, gpsY

    // Loop to simulate filtering over time
    for (i in 1..100) {
        // Predict based on accelerometer data (for motion estimation)
        kalmanFilter.predict(accelerometerData[0], accelerometerData[1])

        // Update based on GPS measurement
        kalmanFilter.update(gpsData[0], gpsData[1])

        // Get filtered position
        val filteredPosition = kalmanFilter.getPosition()
        println("Filtered Position: x = ${filteredPosition[0]}, y = ${filteredPosition[1]}")
        println()

        accelerometerData[0] = accelerometerData[0] + 0.1
        accelerometerData[1] = accelerometerData[1] + 0.1

    }
}







