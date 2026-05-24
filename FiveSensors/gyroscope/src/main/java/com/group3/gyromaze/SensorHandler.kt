package com.group3.gyromaze

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorHandler(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // this sensor fuses gyroscope and accelerometer for stable tilt readings
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    // current tilt values -> other classes read these
    var tiltX: Float = 0f   // Left/right tilt → controls marble X movement
        private set
    var tiltY: Float = 0f   // Forward/backward tilt → controls marble Y movement
        private set

    val isAvailable: Boolean
        get() = rotationSensor != null

    fun start() {
        rotationSensor?.let {
            // SENSOR_DELAY_GAME -> ~50 readings per second, tuned for games
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    // called by Android every time a new sensor reading is available
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        // convert the rotation vector into a rotation matrix
        // a rotation matrix describes the phone's full 3D orientation
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        // extract tilt angles from the rotation matrix
        // rotationMatrix[1] -> how much the phone tilts left/right (X axis)
        // rotationMatrix[7] -> how much the phone tilts forward/backward (Y axis)
        // these values are between -1.0 and 1.0
        tiltX = rotationMatrix[1]   // Positive -> tilt right
        tiltY = rotationMatrix[7]   // Positive -> tilt forward (away from you)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Not needed for this game
    }
}