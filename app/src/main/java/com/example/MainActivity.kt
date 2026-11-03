package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppUpdateDialog
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.instructor.InstructorDashboardScreen
import com.example.ui.screens.student.StudentContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.update.UpdateCheckState
import com.example.viewmodel.UserRole
import com.example.viewmodel.XmaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF08090C)
                ) {
                    XmaApp()
                }
            }
        }
    }
}

@Composable
fun XmaApp(viewModel: XmaViewModel = viewModel()) {
    val context = LocalContext.current
    val currentRole by viewModel.currentRole.collectAsState()
    val allBatches by viewModel.allBatches.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val currentInstructor by viewModel.currentInstructor.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allAttendance by viewModel.allAttendance.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val allAnnouncements by viewModel.allAnnouncements.collectAsState()

    val appUpdateConfig by viewModel.appUpdateConfig.collectAsState()
    val isUpdateDialogVisible by viewModel.isUpdateDialogVisible.collectAsState()
    val updateDownloadState by viewModel.downloadState.collectAsState()
    val updateCheckState by viewModel.updateCheckState.collectAsState()

    val isMandatoryUpdate = updateCheckState == UpdateCheckState.MANDATORY_UPDATE_AVAILABLE ||
            appUpdateConfig?.updateType.equals("MANDATORY", ignoreCase = true)

    LaunchedEffect(Unit) {
        // App is already updated to latest version; do not block login
    }

    LaunchedEffect(statusMessage) {
        if (statusMessage != null) {
            Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090C))
    ) {
        when (currentRole) {
            UserRole.NONE -> {
                AuthScreen(
                    batches = allBatches,
                    onLoginStudent = { studentId, pin, onError ->
                        viewModel.loginStudent(studentId, pin, onSuccess = {}, onError = onError)
                    },
                    onLoginInstructor = { email, pass, onError ->
                        viewModel.loginInstructor(email, pass, onSuccess = {}, onError = onError)
                    },
                    onRegisterStudent = { name, dob, gender, parent, pMobile, sMobile, address, batch, pin, onSuccess, onError ->
                        viewModel.registerStudent(
                            fullName = name,
                            dob = dob,
                            gender = gender,
                            parentName = parent,
                            parentMobile = pMobile,
                            studentMobile = sMobile,
                            address = address,
                            batchId = batch,
                            pin = pin,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    }
                )
            }

            UserRole.STUDENT -> {
                StudentContainer(
                    viewModel = viewModel,
                    onLogout = { viewModel.logout() }
                )
            }

            UserRole.INSTRUCTOR -> {
                InstructorDashboardScreen(
                    instructor = currentInstructor,
                    students = allStudents,
                    batches = allBatches,
                    attendanceList = allAttendance,
                    payments = allPayments,
                    videos = allVideos,
                    announcements = allAnnouncements,
                    todayDate = viewModel.todayDateString,
                    onRegisterStudent = { newS ->
                        viewModel.adminAddStudent(newS) {}
                    },
                    onToggleStudentStatus = { sId, curr ->
                        viewModel.adminToggleStudentStatus(sId, curr)
                    },
                    onResetPin = { sId, newP ->
                        viewModel.adminResetStudentPin(sId, newP) {}
                    },
                    onRecordFeePayment = { sId, name, amount, method, txId, month ->
                        viewModel.adminRecordPayment(sId, name, amount, method, txId, month) {}
                    },
                    onAddVideo = { vid ->
                        viewModel.adminAddVideo(vid) {}
                    },
                    onDeleteVideo = { vidId ->
                        viewModel.adminDeleteVideo(vidId)
                    },
                    onSendAnnouncement = { title, msg, batch ->
                        viewModel.adminSendAnnouncement(title, msg, batch) {}
                    },
                    onAddPerformanceNote = { sId, note, tag ->
                        viewModel.adminAddPerformanceNote(sId, note, tag) {}
                    },
                    onGenerateReport = { reportType ->
                        viewModel.generateReportCsv(reportType)
                    },
                    appUpdateConfig = appUpdateConfig,
                    currentAppVersionName = viewModel.appUpdateManager.currentVersionName,
                    onPublishAppUpdate = { version, minVersion, downloadUrl, notes, isMandatory ->
                        viewModel.adminPublishAppVersion(
                            latestVersion = version,
                            latestVersionCode = 2,
                            minimumSupportedVersion = minVersion,
                            minimumSupportedVersionCode = 1,
                            updateType = if (isMandatory) "MANDATORY" else "OPTIONAL",
                            updateUrl = downloadUrl,
                            releaseNotes = notes,
                            updateTitle = "New Version Available (v$version)",
                            updateMessage = "A new update for XMA SCHOOL is ready to install.",
                            onSuccess = {}
                        )
                    },
                    onCheckAppUpdate = {
                        viewModel.checkForAppUpdates(silent = false)
                    },
                    onLogout = { viewModel.logout() }
                )
            }
        }

        // Global Real-Time App Update Dialog overlay (never displayed on login/auth screen so students log in directly)
        if (isUpdateDialogVisible && appUpdateConfig != null && currentRole != UserRole.NONE) {
            AppUpdateDialog(
                config = appUpdateConfig!!,
                currentVersion = viewModel.appUpdateManager.currentVersionName,
                isMandatory = isMandatoryUpdate,
                downloadState = updateDownloadState,
                onUpdateNow = {
                    appUpdateConfig?.let { config ->
                        viewModel.startAppUpdateDownload(config)
                    }
                },
                onDismissLater = {
                    viewModel.dismissUpdateDialog()
                }
            )
        }
    }
}
