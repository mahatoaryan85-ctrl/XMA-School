package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrainingVideoEntity
import com.example.ui.components.AppDownloadQrModal
import com.example.ui.components.FeePaymentModal
import com.example.ui.components.GeminiAiCoachModal
import com.example.ui.components.NotificationDialog
import com.example.ui.components.SelfieAttendanceModal
import com.example.ui.components.VideoPlayerModal
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import com.example.viewmodel.UserRole
import com.example.viewmodel.XmaViewModel

sealed class StudentTab(val index: Int, val title: String, val icon: ImageVector) {
    object Home : StudentTab(0, "Home", Icons.Default.Home)
    object Attendance : StudentTab(1, "Attendance", Icons.Default.CalendarMonth)
    object Training : StudentTab(2, "Training", Icons.Default.SportsMartialArts)
    object Progress : StudentTab(3, "Progress", Icons.Default.TrendingUp)
    object Profile : StudentTab(4, "Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentContainer(
    viewModel: XmaViewModel,
    onLogout: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val student by viewModel.currentStudent.collectAsState()
    val todayAttendance by viewModel.todayStudentAttendance.collectAsState()
    val attendanceList by viewModel.studentAttendance.collectAsState()
    val monthlyAttendancePct by viewModel.studentMonthlyAttendancePercentage.collectAsState()
    val attendanceStreak by viewModel.studentAttendanceStreak.collectAsState()
    val categoryProgress by viewModel.studentCategoryProgress.collectAsState()
    val overallProgress by viewModel.studentOverallProgress.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val studentProgress by viewModel.studentProgress.collectAsState()
    val announcements by viewModel.allAnnouncements.collectAsState()
    val payments by viewModel.studentPayments.collectAsState()
    val notes by viewModel.studentNotes.collectAsState()
    val notifications by viewModel.studentNotifications.collectAsState()
    val statusMsg by viewModel.statusMessage.collectAsState()

    val appUpdateConfig by viewModel.appUpdateConfig.collectAsState()
    val updateCheckState by viewModel.updateCheckState.collectAsState()
    val hasUpdate = updateCheckState == com.example.update.UpdateCheckState.OPTIONAL_UPDATE_AVAILABLE ||
            updateCheckState == com.example.update.UpdateCheckState.MANDATORY_UPDATE_AVAILABLE

    var showSelfieModal by remember { mutableStateOf(false) }
    var showFeeModal by remember { mutableStateOf(false) }
    var activeVideoForPlayer by remember { mutableStateOf<TrainingVideoEntity?>(null) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showGeminiModal by remember { mutableStateOf(false) }
    var showStepTrackerModal by remember { mutableStateOf(false) }
    var showDownloadQrModal by remember { mutableStateOf(false) }

    val todaySteps by viewModel.stepTrackerManager.todaySteps.collectAsState()
    val dailyGoal by viewModel.stepTrackerManager.dailyGoal.collectAsState()
    val cadenceSpm by viewModel.stepTrackerManager.cadenceSpm.collectAsState()
    val motionActivity by viewModel.stepTrackerManager.motionActivity.collectAsState()
    val isStepTracking by viewModel.stepTrackerManager.isTracking.collectAsState()
    val studentRecentSteps by viewModel.studentRecentSteps.collectAsState()

    LaunchedEffect(statusMsg) {
        if (statusMsg != null) {
            snackbarHostState.showSnackbar(statusMsg!!)
            viewModel.clearStatusMessage()
        }
    }

    if (student == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0B0E)),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading student session...", color = XmaTextMuted)
        }
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(XmaRedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsMartialArts, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "XMA SCHOOL",
                                color = XmaTextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "MARTIAL ARTS & SELF DEFENCE",
                                color = XmaRedPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showGeminiModal = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ask Gemini AI",
                            tint = Color(0xFFC084FC)
                        )
                    }

                    // Step & Motion Tracker Quick Launcher
                    IconButton(
                        onClick = { showStepTrackerModal = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF261916))
                    ) {
                        BadgedBox(
                            badge = {
                                if (todaySteps > 0) {
                                    Badge(containerColor = XmaRedPrimary) {
                                        Text("${todaySteps / 1000}k", color = Color.White, fontSize = 8.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "Step Tracker",
                                tint = if (isStepTracking) XmaGold else XmaTextWhite
                            )
                        }
                    }

                    // App Download QR Standee Button
                    IconButton(
                        onClick = { showDownloadQrModal = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1730))
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Download App QR",
                            tint = XmaGold
                        )
                    }

                    val unreadCount = notifications.count { !it.isRead }
                    IconButton(onClick = { showNotificationsDialog = true }) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = XmaRedPrimary) {
                                        Text("$unreadCount", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (unreadCount > 0) XmaGold else XmaTextWhite
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = XmaSurface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = XmaSurface,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    StudentTab.Home,
                    StudentTab.Attendance,
                    StudentTab.Training,
                    StudentTab.Progress,
                    StudentTab.Profile
                )
                tabs.forEach { tab ->
                    val isSelected = selectedTabIndex == tab.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = tab.index },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.title)
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = XmaRedPrimary,
                            selectedTextColor = XmaRedPrimary,
                            unselectedIconColor = XmaTextMuted,
                            unselectedTextColor = XmaTextMuted,
                            indicatorColor = XmaRedPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFF0A0B0E)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> StudentHomeScreen(
                    student = student!!,
                    todayAttendance = todayAttendance,
                    attendancePercentage = monthlyAttendancePct,
                    overallProgress = overallProgress,
                    videos = allVideos,
                    announcements = announcements,
                    hasUpdateAvailable = hasUpdate,
                    updateVersionName = appUpdateConfig?.latestVersion ?: "",
                    onOpenMarkAttendance = { showSelfieModal = true },
                    onOpenPayFee = { showFeeModal = true },
                    onWatchVideo = { vid -> activeVideoForPlayer = vid },
                    onNavigateToTraining = { selectedTabIndex = 2 },
                    onNavigateToAttendance = { selectedTabIndex = 1 },
                    onOpenGeminiCoach = { showGeminiModal = true },
                    onOpenUpdate = { viewModel.showUpdateDialog() },
                    onSubmitUpiProof = { utr, method, onResult ->
                        viewModel.studentSubmitUpiPaymentProof(utr = utr, platform = method, onResult = onResult)
                    },
                    stepCount = todaySteps,
                    stepGoal = dailyGoal,
                    cadenceSpm = cadenceSpm,
                    motionActivity = motionActivity,
                    distanceMeters = viewModel.stepTrackerManager.calculateDistanceMeters(todaySteps),
                    caloriesBurned = viewModel.stepTrackerManager.calculateCalories(todaySteps),
                    activeMinutes = viewModel.stepTrackerManager.calculateActiveMinutes(todaySteps),
                    isStepTracking = isStepTracking,
                    onOpenStepTracker = { showStepTrackerModal = true }
                )
                1 -> StudentAttendanceScreen(
                    student = student!!,
                    attendanceList = attendanceList,
                    todayAttendance = todayAttendance,
                    attendancePercentage = monthlyAttendancePct,
                    streak = attendanceStreak,
                    onOpenMarkAttendance = { showSelfieModal = true }
                )
                2 -> StudentTrainingScreen(
                    videos = allVideos,
                    progressList = studentProgress,
                    onWatchVideo = { vid -> activeVideoForPlayer = vid },
                    onOpenGeminiCoach = { showGeminiModal = true }
                )
                3 -> StudentProgressScreen(
                    student = student!!,
                    categoryProgress = categoryProgress,
                    overallProgress = overallProgress,
                    progressList = studentProgress,
                    performanceNotes = notes,
                    todaySteps = todaySteps,
                    goalSteps = dailyGoal,
                    recentSteps = studentRecentSteps,
                    onOpenTracker = { showStepTrackerModal = true }
                )
                4 -> StudentProfileScreen(
                    student = student!!,
                    paymentHistory = payments,
                    currentAppVersion = viewModel.appUpdateManager.currentVersionName,
                    latestAppVersion = appUpdateConfig?.latestVersion ?: viewModel.appUpdateManager.currentVersionName,
                    hasUpdateAvailable = hasUpdate,
                    onCheckForUpdates = { viewModel.checkForAppUpdates(silent = false) },
                    onOpenUpdateDialog = { viewModel.showUpdateDialog() },
                    onOpenPayFee = { showFeeModal = true },
                    onResetPin = { newPin ->
                        viewModel.adminResetStudentPin(student!!.studentId, newPin) {}
                    },
                    onUpdatePhoto = { newPhoto ->
                        viewModel.updateStudentProfilePhoto(student!!.studentId, newPhoto)
                    },
                    onLogout = onLogout
                )
            }
        }
    }

    // Selfie Attendance Modal
    if (showSelfieModal) {
        SelfieAttendanceModal(
            student = student!!,
            todayAttendance = todayAttendance,
            onDismiss = { showSelfieModal = false },
            onSubmitSelfie = { uri, onSucc, onErr ->
                viewModel.markAttendanceSelfie(uri, onSucc, onErr)
            }
        )
    }

    // Video Player Modal
    if (activeVideoForPlayer != null) {
        val vid = activeVideoForPlayer!!
        val currentProg = studentProgress.firstOrNull { it.videoId == vid.id }
        VideoPlayerModal(
            video = vid,
            currentProgress = currentProg,
            onDismiss = { activeVideoForPlayer = null },
            onProgressUpdate = { pct, comp, pos ->
                viewModel.recordVideoProgress(vid.id, pct, comp, pos)
            }
        )
    }

    // Fee Payment Modal
    if (showFeeModal) {
        FeePaymentModal(
            student = student!!,
            onDismiss = { showFeeModal = false },
            onPaymentConfirmed = { method, txnId ->
                if (method.contains("UPI", ignoreCase = true) || method.contains("PhonePe", ignoreCase = true) || method.contains("GPay", ignoreCase = true) || method.contains("Paytm", ignoreCase = true)) {
                    viewModel.studentSubmitUpiPaymentProof(utr = txnId ?: "", platform = method) { _, _ ->
                        showFeeModal = false
                    }
                } else {
                    viewModel.studentPayFeeOnline(paymentMethod = method, transactionId = txnId ?: "") {
                        showFeeModal = false
                    }
                }
            }
        )
    }

    // In-app Notifications Dialog
    if (showNotificationsDialog) {
        NotificationDialog(
            notifications = notifications,
            onDismiss = { showNotificationsDialog = false },
            onMarkAllRead = { viewModel.markNotificationsRead() }
        )
    }

    // Ask Gemini AI Coach for Training & Diet
    if (showGeminiModal && student != null) {
        GeminiAiCoachModal(
            student = student!!,
            onDismiss = { showGeminiModal = false }
        )
    }

    // Dojo Step & Motion Tracker Modal
    if (showStepTrackerModal) {
        StepTrackerModal(
            stepTrackerManager = viewModel.stepTrackerManager,
            recentStepsList = studentRecentSteps,
            onClose = { showStepTrackerModal = false },
            onUpdateDailyGoal = { newGoal ->
                viewModel.updateDailyGoal(newGoal)
            }
        )
    }

    // App Download & Installation QR Standee Modal
    if (showDownloadQrModal) {
        AppDownloadQrModal(onDismiss = { showDownloadQrModal = false })
    }
}
