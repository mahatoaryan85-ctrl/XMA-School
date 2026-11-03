package com.example.ui.screens.instructor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.XmaBackground
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import com.example.viewmodel.XmaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorContainer(
    viewModel: XmaViewModel,
    onLogout: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val instructor by viewModel.currentInstructor.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allBatches by viewModel.allBatches.collectAsState()
    val allAttendance by viewModel.allAttendance.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val allAnnouncements by viewModel.allAnnouncements.collectAsState()
    val statusMsg by viewModel.statusMessage.collectAsState()

    LaunchedEffect(statusMsg) {
        if (statusMsg != null) {
            snackbarHostState.showSnackbar(statusMsg!!)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        containerColor = XmaBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(XmaRedPrimary)
                                .border(1.dp, XmaGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
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
                                text = "INSTRUCTOR & ADMIN PORTAL",
                                color = XmaGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = XmaTextMuted)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = XmaSurface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            InstructorDashboardScreen(
                instructor = instructor,
                students = allStudents,
                batches = allBatches,
                attendanceList = allAttendance,
                payments = allPayments,
                videos = allVideos,
                announcements = allAnnouncements,
                todayDate = viewModel.todayDateString,
                onRegisterStudent = { newStudent ->
                    viewModel.adminAddStudent(newStudent) {}
                },
                onUpdateStudent = { updatedStudent ->
                    viewModel.adminUpdateStudent(updatedStudent) {}
                },
                onMarkAttendance = { studentId, status ->
                    viewModel.adminMarkStudentAttendance(studentId, status) {}
                },
                onToggleStudentStatus = { sId, currStatus ->
                    viewModel.adminToggleStudentStatus(sId, currStatus)
                },
                onResetPin = { sId, newPin ->
                    viewModel.adminResetStudentPin(sId, newPin) {}
                },
                onRecordFeePayment = { sId, name, amount, method, txId, month ->
                    viewModel.adminRecordPayment(sId, name, amount, method, txId, month) {}
                },
                onApprovePayment = { paymentId ->
                    viewModel.adminApproveFeePayment(paymentId) {}
                },
                onRejectPayment = { paymentId, reason ->
                    viewModel.adminRejectFeePayment(paymentId, reason) {}
                },
                onAddVideo = { newVid ->
                    viewModel.adminAddVideo(newVid) {}
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
                onAddBatch = { newBatch ->
                    viewModel.adminAddBatch(newBatch) {}
                },
                onLogout = onLogout
            )
        }
    }
}
