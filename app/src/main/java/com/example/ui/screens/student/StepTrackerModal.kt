package com.example.ui.screens.student

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyStepEntity
import com.example.sensor.StepTrackerManager
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSuccess
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import java.util.Locale

@Composable
fun StepTrackerModal(
    stepTrackerManager: StepTrackerManager,
    recentStepsList: List<DailyStepEntity>,
    onClose: () -> Unit,
    onUpdateDailyGoal: (Int) -> Unit
) {
    val context = LocalContext.current

    val isTracking by stepTrackerManager.isTracking.collectAsState()
    val todaySteps by stepTrackerManager.todaySteps.collectAsState()
    val dailyGoal by stepTrackerManager.dailyGoal.collectAsState()
    val sessionSteps by stepTrackerManager.sessionSteps.collectAsState()
    val isSessionActive by stepTrackerManager.isSessionActive.collectAsState()
    val sessionSeconds by stepTrackerManager.sessionDurationSeconds.collectAsState()
    val cadenceSpm by stepTrackerManager.cadenceSpm.collectAsState()
    val motionActivity by stepTrackerManager.motionActivity.collectAsState()
    val activeSensorMode by stepTrackerManager.activeSensorMode.collectAsState()
    val currentMagnitude by stepTrackerManager.currentMagnitude.collectAsState()

    var permissionGranted by remember {
        mutableStateOf(stepTrackerManager.hasActivityRecognitionPermission())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            stepTrackerManager.startTracking()
        }
    }

    // Calculations
    val distanceKm = stepTrackerManager.calculateDistanceMeters(todaySteps) / 1000.0
    val calories = stepTrackerManager.calculateCalories(todaySteps)
    val activeMinutes = stepTrackerManager.calculateActiveMinutes(todaySteps)

    val progress = if (dailyGoal > 0) {
        (todaySteps.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val progressPercent = (progress * 100).toInt()

    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "modal_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "modal_pulseScale"
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF090A0D)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(XmaSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(XmaRedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DOJO STEP & MOTION TRACKER",
                                color = XmaTextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Real-time Ashi-Sabaki Footwork & Step Engine",
                                color = XmaGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = XmaTextWhite
                        )
                    }
                }

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Permission Prompt if on Android 10+ and not granted
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !permissionGranted) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B220B)),
                                border = BorderStroke(1.dp, XmaGold)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = null,
                                        tint = XmaGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Hardware Sensor Permission",
                                            color = XmaGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Allow activity recognition for precision hardware pedometer tracking.",
                                            color = XmaTextWhite,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            permissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = XmaGold),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("ENABLE", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }

                    // 2. Main Step Gauge Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = XmaSurface),
                            border = BorderStroke(1.5.dp, if (progressPercent >= 100) XmaGold else XmaOutline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Status Pill
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "TODAY'S ACTIVITY",
                                        color = XmaTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(if (isTracking) Color(0xFF132A13) else Color(0xFF261E1E))
                                            .border(1.dp, if (isTracking) XmaSuccess else XmaOutline, RoundedCornerShape(20.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .scale(if (isTracking) pulseScale else 1f)
                                                    .clip(CircleShape)
                                                    .background(if (isTracking) XmaSuccess else XmaTextMuted)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = if (isTracking) "TRACKING ACTIVE" else "PAUSED",
                                                color = if (isTracking) XmaSuccess else XmaTextMuted,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Big Progress Ring
                                Box(
                                    modifier = Modifier.size(170.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { 1f },
                                        modifier = Modifier.size(170.dp),
                                        color = XmaSurfaceVariant,
                                        strokeWidth = 12.dp,
                                    )
                                    CircularProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.size(170.dp),
                                        color = if (progressPercent >= 100) XmaGold else XmaRedPrimary,
                                        strokeWidth = 12.dp,
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsWalk,
                                            contentDescription = null,
                                            tint = if (progressPercent >= 100) XmaGold else XmaRedPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = String.format(Locale.getDefault(), "%,d", todaySteps),
                                            color = XmaTextWhite,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "/ ${String.format(Locale.getDefault(), "%,d", dailyGoal)} STEPS",
                                            color = XmaTextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$progressPercent% COMPLETE",
                                            color = if (progressPercent >= 100) XmaGold else XmaRedPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                if (progressPercent >= 100) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF261D00))
                                            .border(1.dp, XmaGold, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "🏆 DAILY GOAL ACCOMPLISHED! Osu!",
                                            color = XmaGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // 3 Metric Badges
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = String.format(Locale.getDefault(), "%.2f", distanceKm),
                                                color = XmaTextWhite,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Text("Kilometers", color = XmaTextMuted, fontSize = 11.sp)
                                    }

                                    Box(modifier = Modifier.height(30.dp).width(1.dp).background(XmaOutline))

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = String.format(Locale.getDefault(), "%.0f", calories),
                                                color = XmaTextWhite,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Text("Calories (kcal)", color = XmaTextMuted, fontSize = 11.sp)
                                    }

                                    Box(modifier = Modifier.height(30.dp).width(1.dp).background(XmaOutline))

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Timer, contentDescription = null, tint = XmaSuccess, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "$activeMinutes",
                                                color = XmaTextWhite,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Text("Active Mins", color = XmaTextMuted, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // 3. Real-Time Motion Sensor Telemetry & Cadence
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = XmaSurface),
                            border = BorderStroke(1.dp, XmaOutline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Sensors, contentDescription = null, tint = XmaRedPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("MOTION SENSOR TELEMETRY", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }
                                    Text(
                                        text = "$cadenceSpm SPM",
                                        color = XmaGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Active Motion State
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1B1816))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MotionPhotosOn,
                                        contentDescription = null,
                                        tint = XmaRedPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Current Activity State:", color = XmaTextMuted, fontSize = 10.sp)
                                        Text(
                                            text = motionActivity,
                                            color = XmaTextWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Sensor Engine & Acceleration Intensity
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Engine: $activeSensorMode", color = XmaTextMuted, fontSize = 10.sp)
                                        Text(
                                            text = String.format(Locale.getDefault(), "Acc: %.2f m/s²", currentMagnitude),
                                            color = XmaGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val magnitudeNormalized = ((currentMagnitude - 9.81f).coerceAtLeast(0f) / 10f).coerceIn(0f, 1f)
                                    LinearProgressIndicator(
                                        progress = { magnitudeNormalized },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = XmaRedPrimary,
                                        trackColor = XmaSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // 4. Dojo Footwork / Conditioning Workout Session Mode
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF15141D)),
                            border = BorderStroke(1.dp, if (isSessionActive) XmaRedPrimary else XmaOutline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = XmaRedPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("DOJO FOOTWORK SESSION", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }

                                    if (isSessionActive) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(XmaRedPrimary)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("RECORDING", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Session Stats
                                val hrs = sessionSeconds / 3600
                                val mins = (sessionSeconds % 3600) / 60
                                val secs = sessionSeconds % 60
                                val timeFormatted = if (hrs > 0) {
                                    String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
                                } else {
                                    String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(timeFormatted, color = XmaTextWhite, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                        Text("Elapsed Time", color = XmaTextMuted, fontSize = 10.sp)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$sessionSteps", color = XmaGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                        Text("Session Steps", color = XmaTextMuted, fontSize = 10.sp)
                                    }

                                    val sessionKcal = stepTrackerManager.calculateCalories(sessionSteps)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(String.format(Locale.getDefault(), "%.1f", sessionKcal), color = Color(0xFFFF5722), fontSize = 18.sp, fontWeight = FontWeight.Black)
                                        Text("Session kcal", color = XmaTextMuted, fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (!isSessionActive && sessionSeconds == 0) {
                                        Button(
                                            onClick = { stepTrackerManager.startWorkoutSession() },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("START FOOTWORK / STEP DRILL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    } else {
                                        if (isSessionActive) {
                                            Button(
                                                onClick = { stepTrackerManager.pauseWorkoutSession() },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26211C)),
                                                border = BorderStroke(1.dp, XmaGold),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.Pause, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("PAUSE", color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Button(
                                                onClick = { stepTrackerManager.resumeWorkoutSession() },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("RESUME", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Button(
                                            onClick = { stepTrackerManager.stopWorkoutSession() },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF281313)),
                                            border = BorderStroke(1.dp, Color(0xFFE53935)),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Stop, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("STOP", color = Color(0xFFE53935), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. Daily Goal Customizer
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = XmaSurface),
                            border = BorderStroke(1.dp, XmaOutline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Flag, contentDescription = null, tint = XmaGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("DAILY STEP GOAL", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }

                                Text(
                                    text = "Select your daily target to match your training intensity:",
                                    color = XmaTextMuted,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                val goals = listOf(4000, 6000, 8000, 10000, 12000)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    goals.forEach { g ->
                                        val isSelected = dailyGoal == g
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) XmaRedPrimary else XmaSurfaceVariant)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) XmaRedPrimary else XmaOutline,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { onUpdateDailyGoal(g) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${g / 1000}k",
                                                color = if (isSelected) Color.White else XmaTextWhite,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 6. Recent History (Last 7 Days)
                    if (recentStepsList.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                                border = BorderStroke(1.dp, XmaOutline)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text("RECENT 7-DAY ACTIVITY", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    recentStepsList.take(7).forEach { itemRecord ->
                                        val itemPct = if (itemRecord.goalSteps > 0) {
                                            (itemRecord.stepCount.toFloat() / itemRecord.goalSteps.toFloat()).coerceIn(0f, 1f)
                                        } else 0f

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.width(80.dp)) {
                                                Text(itemRecord.date, color = XmaTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("${itemRecord.activeMinutes} min", color = XmaTextMuted, fontSize = 9.sp)
                                            }

                                            LinearProgressIndicator(
                                                progress = { itemPct },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp)),
                                                color = if (itemPct >= 1f) XmaGold else XmaRedPrimary,
                                                trackColor = XmaSurfaceVariant
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = String.format(Locale.getDefault(), "%,d", itemRecord.stepCount),
                                                color = if (itemPct >= 1f) XmaGold else XmaTextWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.width(60.dp),
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 7. Sensei's Martial Arts Tip
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF14120E)),
                            border = BorderStroke(1.dp, XmaGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = XmaGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("SENSEI'S FOOTWORK TIP", color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "In Karate and Kumite, agility (Ashi-Sabaki) comes from light feet and strong calves. Maintaining 6,000+ daily steps enhances reaction time, stamina, and blitz speed during dojo bouts.",
                                        color = XmaTextWhite.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
