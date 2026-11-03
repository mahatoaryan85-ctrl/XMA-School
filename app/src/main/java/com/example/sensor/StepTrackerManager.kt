package com.example.sensor

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Real-time Step Counter & Motion Activity Tracker for Martial Arts training.
 * Utilizes hardware Step Counter / Step Detector if available, with a dedicated
 * 3-axis Accelerometer Peak-Detection Engine as fallback and real-time motion analyzer.
 */
class StepTrackerManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val stepCounterSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val accelerometerSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // State Flows
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    private val _sessionSteps = MutableStateFlow(0)
    val sessionSteps: StateFlow<Int> = _sessionSteps.asStateFlow()

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _sessionDurationSeconds = MutableStateFlow(0)
    val sessionDurationSeconds: StateFlow<Int> = _sessionDurationSeconds.asStateFlow()

    private val _cadenceSpm = MutableStateFlow(0)
    val cadenceSpm: StateFlow<Int> = _cadenceSpm.asStateFlow()

    private val _motionActivity = MutableStateFlow("Stationary")
    val motionActivity: StateFlow<String> = _motionActivity.asStateFlow()

    private val _activeSensorMode = MutableStateFlow("Checking Sensors...")
    val activeSensorMode: StateFlow<String> = _activeSensorMode.asStateFlow()

    private val _currentMagnitude = MutableStateFlow(9.81f)
    val currentMagnitude: StateFlow<Float> = _currentMagnitude.asStateFlow()

    private val _dailyGoal = MutableStateFlow(6000)
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    // Motion algorithm variables
    private var initialStepCounterValue: Float = -1f
    private var lastStepCounterReading: Float = -1f
    private var lastStepTimeNs: Long = 0
    private var recentStepTimestamps = mutableListOf<Long>()

    // Accelerometer peak detection filter
    private var gravityComp = 9.81f
    private val alpha = 0.8f
    private var lastAccStepTimeMs = 0L
    private val minStepIntervalMs = 270L // Max 220 steps/min to prevent false shakes
    private val stepThreshold = 2.1f // m/s^2 linear acceleration peak

    // Session timer job
    private var sessionTimerJob: Job? = null

    // Callback to persist steps
    var onStepCountUpdated: ((steps: Int, sessionSteps: Int, motionType: String) -> Unit)? = null

    init {
        determineBestSensorMode()
    }

    private fun determineBestSensorMode() {
        _activeSensorMode.value = when {
            stepCounterSensor != null -> "Hardware Step Counter & Motion Engine"
            stepDetectorSensor != null -> "Step Detector & Motion Engine"
            accelerometerSensor != null -> "Dojo Accelerometer Motion Engine"
            else -> "Simulated Motion Engine (No Hardware Sensor)"
        }
    }

    fun hasActivityRecognitionPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun setInitialSteps(steps: Int, goal: Int = 6000) {
        if (_todaySteps.value == 0 || steps > _todaySteps.value) {
            _todaySteps.value = steps
        }
        _dailyGoal.value = goal
    }

    fun setDailyGoal(goal: Int) {
        _dailyGoal.value = goal
    }

    fun startTracking() {
        if (_isTracking.value) return
        val sm = sensorManager ?: return

        var registeredAny = false

        // Register Step Counter if allowed
        if (hasActivityRecognitionPermission() && stepCounterSensor != null) {
            sm.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
            registeredAny = true
        }

        // Register Step Detector if allowed
        if (hasActivityRecognitionPermission() && stepDetectorSensor != null) {
            sm.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
            registeredAny = true
        }

        // Always register Accelerometer for live cadence, motion activity (walking vs kumite drills) and fallback
        if (accelerometerSensor != null) {
            sm.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
            registeredAny = true
        }

        _isTracking.value = registeredAny
        determineBestSensorMode()
    }

    fun stopTracking() {
        sensorManager?.unregisterListener(this)
        _isTracking.value = false
        _cadenceSpm.value = 0
        _motionActivity.value = "Stationary"
    }

    fun startWorkoutSession() {
        _isSessionActive.value = true
        _sessionSteps.value = 0
        _sessionDurationSeconds.value = 0
        startTracking()

        sessionTimerJob?.cancel()
        sessionTimerJob = coroutineScope.launch(Dispatchers.Default) {
            while (isActive && _isSessionActive.value) {
                delay(1000)
                _sessionDurationSeconds.value += 1

                // Recalculate cadence every second
                cleanOldStepTimestamps()
            }
        }
    }

    fun pauseWorkoutSession() {
        _isSessionActive.value = false
        sessionTimerJob?.cancel()
    }

    fun resumeWorkoutSession() {
        if (!_isSessionActive.value) {
            _isSessionActive.value = true
            sessionTimerJob?.cancel()
            sessionTimerJob = coroutineScope.launch(Dispatchers.Default) {
                while (isActive && _isSessionActive.value) {
                    delay(1000)
                    _sessionDurationSeconds.value += 1
                    cleanOldStepTimestamps()
                }
            }
        }
    }

    fun stopWorkoutSession() {
        _isSessionActive.value = false
        sessionTimerJob?.cancel()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalSinceBoot = event.values[0]
                if (initialStepCounterValue < 0) {
                    initialStepCounterValue = totalSinceBoot
                    lastStepCounterReading = totalSinceBoot
                } else {
                    val delta = (totalSinceBoot - lastStepCounterReading).toInt()
                    if (delta in 1..50) {
                        registerStep(delta)
                        lastStepCounterReading = totalSinceBoot
                    }
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                if (event.values[0] == 1.0f) {
                    registerStep(1)
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                handleAccelerometerMotion(event.values)
            }
        }
    }

    private fun handleAccelerometerMotion(values: FloatArray) {
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val mag = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        _currentMagnitude.value = mag

        // Low-pass filter for gravity isolation
        gravityComp = alpha * gravityComp + (1 - alpha) * mag
        val linearAcc = kotlin.math.abs(mag - gravityComp)

        val now = SystemClock.uptimeMillis()

        // If hardware step counter isn't registered or active, use accelerometer peak detection
        val useAccForSteps = (stepCounterSensor == null && stepDetectorSensor == null) || !hasActivityRecognitionPermission()

        if (linearAcc > stepThreshold && (now - lastAccStepTimeMs) > minStepIntervalMs) {
            lastAccStepTimeMs = now
            if (useAccForSteps) {
                registerStep(1)
            } else {
                recordStepTimestamp(now)
            }
        }

        // Classify motion activity
        updateMotionActivity(linearAcc)
    }

    private fun registerStep(count: Int) {
        _todaySteps.value += count
        if (_isSessionActive.value) {
            _sessionSteps.value += count
        }

        val now = SystemClock.uptimeMillis()
        recordStepTimestamp(now)

        onStepCountUpdated?.invoke(_todaySteps.value, _sessionSteps.value, _motionActivity.value)
    }

    private fun recordStepTimestamp(nowMs: Long) {
        synchronized(recentStepTimestamps) {
            recentStepTimestamps.add(nowMs)
            cleanOldStepTimestamps()
        }
    }

    private fun cleanOldStepTimestamps() {
        val cutoff = SystemClock.uptimeMillis() - 10_000 // Last 10 seconds
        synchronized(recentStepTimestamps) {
            recentStepTimestamps.removeAll { it < cutoff }
            val stepsInWindow = recentStepTimestamps.size
            // Extrapolate to steps per minute (SPM)
            val spm = (stepsInWindow * 6).coerceAtMost(240)
            _cadenceSpm.value = spm
        }
    }

    private fun updateMotionActivity(linearAcc: Float) {
        val spm = _cadenceSpm.value
        _motionActivity.value = when {
            spm == 0 && linearAcc < 0.6f -> "Stationary / Rest"
            spm in 1..40 -> "Gentle Pacing / Warmup"
            spm in 41..105 -> "Brisk Walk / Dojo Stance Work"
            spm in 106..150 -> "Running / Conditioning Drills"
            spm > 150 || linearAcc > 4.5f -> "Martial Arts Footwork / Kumite"
            else -> "Active Movement"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    // Helper calculations
    fun calculateDistanceMeters(steps: Int): Double = steps * 0.762
    fun calculateCalories(steps: Int): Double = steps * 0.042
    fun calculateActiveMinutes(steps: Int): Int = (steps / 100).coerceAtLeast(0)
}
