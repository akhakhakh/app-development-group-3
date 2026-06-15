package com.group3.gyromaze

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorHandler(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    // fallback -> if there's no gravity sensor, use raw accelerometer
    private val fallbackSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var tiltX: Float = 0f
        private set
    var tiltY: Float = 0f
        private set

    private var baselineX: Float = 0f
    private var baselineY: Float = 0f
    private var isCalibrated = false
    val isAvailable: Boolean
        get() = this.gravitySensor != null || this.fallbackSensor != null

    fun start() {
        val sensor = gravitySensor ?: fallbackSensor
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun recalibrate() {
        isCalibrated = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_GRAVITY &&
            event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val gravityMS2 = 9.81f
        val rawX = event.values[0] / gravityMS2
        val rawY = event.values[1] / gravityMS2

        if (!isCalibrated) {
            baselineX = rawX
            baselineY = rawY
            isCalibrated = true
        }

        val relativeX = rawX - baselineX
        val relativeY = rawY - baselineY

        tiltX = (-relativeX).coerceIn(-1f, 1f)
        tiltY = (relativeY).coerceIn(-1f, 1f)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}