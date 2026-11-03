package com.example.ui.screens.instructor

import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SystemUpdate
import com.example.ui.components.AppDownloadQrModal
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppUpdateConfigEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.BatchEntity
import com.example.data.model.FeePaymentEntity
import com.example.data.model.InstructorEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrainingVideoEntity
import com.example.ui.components.AttendancePhotoBitmapProvider
import com.example.ui.components.AttendanceSelfieCard
import com.example.ui.components.AttendanceSelfieDetailDialog
import com.example.ui.components.AttendanceSelfieThumbnail
import com.example.ui.components.BeltRankBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.PhonePeLogoBadge
import com.example.ui.components.PhonePeQrCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.UpiPaymentHelper
import com.example.ui.components.StatusBadge
import com.example.ui.theme.XmaBackground
import com.example.ui.theme.XmaError
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedDark
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSuccess
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import com.example.ui.theme.XmaWarning

@Composable
fun InstructorDashboardScreen(
    instructor: InstructorEntity?,
    students: List<StudentEntity>,
    batches: List<BatchEntity>,
    attendanceList: List<AttendanceEntity>,
    payments: List<FeePaymentEntity>,
    videos: List<TrainingVideoEntity>,
    announcements: List<AnnouncementEntity>,
    todayDate: String,
    onRegisterStudent: (StudentEntity) -> Unit,
    onUpdateStudent: ((StudentEntity) -> Unit)? = null,
    onMarkAttendance: ((String, String) -> Unit)? = null,
    onToggleStudentStatus: (String, String) -> Unit,
    onResetPin: (String, String) -> Unit,
    onRecordFeePayment: (studentId: String, name: String, amount: Double, method: String, txId: String, month: String) -> Unit,
    onApprovePayment: ((paymentId: Long) -> Unit)? = null,
    onRejectPayment: ((paymentId: Long, reason: String) -> Unit)? = null,
    onAddVideo: (TrainingVideoEntity) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onSendAnnouncement: (title: String, message: String, batch: String) -> Unit,
    onAddPerformanceNote: (studentId: String, note: String, tag: String) -> Unit,
    onGenerateReport: (String) -> String,
    onAddBatch: ((BatchEntity) -> Unit)? = null,
    appUpdateConfig: AppUpdateConfigEntity? = null,
    currentAppVersionName: String = "1.0.0",
    onPublishAppUpdate: ((version: String, minVersion: String, downloadUrl: String, notes: String, isMandatory: Boolean) -> Unit)? = null,
    onCheckAppUpdate: (() -> Unit)? = null,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("STUDENTS", "BRANCHES", "ATTENDANCE", "FEES", "TRAINING", "ANNOUNCEMENTS", "REPORTS", "APP UPDATES")

    // Modals state
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddBatchDialog by remember { mutableStateOf(false) }
    var showRecordFeeDialog by remember { mutableStateOf<StudentEntity?>(null) }
    var showAcademyQrDialog by remember { mutableStateOf(false) }
    var showAppDownloadQrDialog by remember { mutableStateOf(false) }
    var showAddVideoDialog by remember { mutableStateOf(false) }
    var showAnnouncementDialog by remember { mutableStateOf(false) }
    var showPerformanceNoteDialog by remember { mutableStateOf<StudentEntity?>(null) }
    var showStudentDetailDialog by remember { mutableStateOf<StudentEntity?>(null) }
    var showReportViewerDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Metrics calculation
    val totalStudents = students.size
    val activeStudents = students.count { it.accountStatus.equals("Active", ignoreCase = true) }
    val todayAttendance = attendanceList.filter { it.date == todayDate }
    val todayPresent = todayAttendance.count { it.status == "Present" }
    val pendingFeesCount = students.count { it.feeStatus != "Paid" }
    val totalCollected = payments.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XmaBackground)
    ) {
        // Instructor Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(XmaSurface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(XmaRedDark)
                        .border(1.5.dp, XmaGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SportsMartialArts, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = instructor?.fullName ?: "Sensei / Academy Head",
                        color = XmaTextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "INSTRUCTOR & ADMIN PORTAL",
                        color = XmaGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAppDownloadQrDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold.copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("instructor_show_download_qr_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "App QR",
                        tint = XmaGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("APP QR", color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("LOGOUT", color = XmaError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Top Summary KPI Metrics Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total Students",
                value = "$activeStudents / $totalStudents",
                subtitle = "Active Enrolled",
                accentColor = XmaGold,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Today Verified",
                value = "$todayPresent",
                subtitle = "$todayDate",
                accentColor = XmaSuccess,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Fees Pending",
                value = "$pendingFeesCount",
                subtitle = "Collected ₹${totalCollected.toInt()}",
                accentColor = if (pendingFeesCount > 0) XmaError else XmaSuccess,
                modifier = Modifier.weight(1.1f)
            )
        }

        // Month-End Pending Fee Admin Alert
        val pendingUpiApprovalsCount = payments.count { it.status == "Pending Verification" }
        val pendingMonthEndAmount = students.filter { it.feeStatus != "Paid" }.sumOf { it.monthlyFee }
        if (pendingFeesCount > 0 || pendingUpiApprovalsCount > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clickable { selectedTab = 3 },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF221318)),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (pendingUpiApprovalsCount > 0) XmaGold else XmaError.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (pendingUpiApprovalsCount > 0) XmaGold.copy(alpha = 0.2f) else XmaError.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Payment,
                                contentDescription = null,
                                tint = if (pendingUpiApprovalsCount > 0) XmaGold else XmaError,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "MONTH-END PENDING FEES: $pendingFeesCount Students (₹${pendingMonthEndAmount.toInt()})",
                                color = XmaTextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (pendingUpiApprovalsCount > 0) {
                                Text(
                                    "⚡ $pendingUpiApprovalsCount UPI payment proofs awaiting your approval",
                                    color = XmaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { selectedTab = 3 },
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("CHECK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            tabs = tabTitles,
            onTabSelected = { selectedTab = it }
        )

        // Tab Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> StudentsTab(
                    students = students,
                    batches = batches,
                    onOpenAdd = { showAddStudentDialog = true },
                    onSelectStudent = { showStudentDetailDialog = it },
                    onRecordFee = { showRecordFeeDialog = it },
                    onAddNote = { showPerformanceNoteDialog = it },
                    onToggleStatus = onToggleStudentStatus
                )
                1 -> BranchesTab(
                    batches = batches,
                    students = students,
                    onOpenAddBatch = { showAddBatchDialog = true }
                )
                2 -> AttendanceTab(
                    attendance = attendanceList,
                    students = students,
                    todayDate = todayDate,
                    onMarkAttendance = onMarkAttendance
                )
                3 -> FeesTab(
                    students = students,
                    payments = payments,
                    instructor = instructor,
                    onRecordFee = { showRecordFeeDialog = it },
                    onShowQr = { showAcademyQrDialog = true },
                    onApprovePayment = onApprovePayment,
                    onRejectPayment = onRejectPayment
                )
                4 -> TrainingTab(
                    videos = videos,
                    onOpenAddVideo = { showAddVideoDialog = true },
                    onDeleteVideo = onDeleteVideo
                )
                5 -> AnnouncementsTab(
                    announcements = announcements,
                    batches = batches,
                    onOpenCreate = { showAnnouncementDialog = true }
                )
                6 -> ReportsTab(
                    onExportReport = { reportType ->
                        val csv = onGenerateReport(reportType)
                        showReportViewerDialog = Pair(reportType, csv)
                    }
                )
                7 -> AppUpdateAdminTab(
                    config = appUpdateConfig,
                    installedVersionName = currentAppVersionName,
                    onPublish = { version, minVer, url, notes, isMandatory ->
                        onPublishAppUpdate?.invoke(version, minVer, url, notes, isMandatory)
                    },
                    onCheckNow = { onCheckAppUpdate?.invoke() }
                )
            }
        }
    }

    // --- DIALOGS ---
    // 1. Add Student Registration Dialog
    if (showAddStudentDialog) {
        AddStudentDialog(
            batches = batches,
            currentCount = students.size,
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { newStudent ->
                onRegisterStudent(newStudent)
                showAddStudentDialog = false
                Toast.makeText(context, "Student ${newStudent.fullName} Registered!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Add Batch Dialog
    if (showAddBatchDialog) {
        AddBatchDialog(
            onDismiss = { showAddBatchDialog = false },
            onConfirm = { newBatch ->
                onAddBatch?.invoke(newBatch)
                showAddBatchDialog = false
                Toast.makeText(context, "Batch '${newBatch.name}' Added!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 2. Record Fee Payment Dialog
    if (showRecordFeeDialog != null) {
        val targetStudent = showRecordFeeDialog!!
        RecordFeeDialog(
            student = targetStudent,
            onDismiss = { showRecordFeeDialog = null },
            onConfirm = { amount, method, txId, month ->
                onRecordFeePayment(targetStudent.studentId, targetStudent.fullName, amount, method, txId, month)
                showRecordFeeDialog = null
                Toast.makeText(context, "Payment logged for ${targetStudent.fullName}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 3. Add Video Dialog
    if (showAddVideoDialog) {
        AddVideoDialog(
            instructorName = instructor?.fullName ?: "Master Vikram Sen",
            onDismiss = { showAddVideoDialog = false },
            onConfirm = { vid ->
                onAddVideo(vid)
                showAddVideoDialog = false
                Toast.makeText(context, "Video '${vid.title}' Published!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 4. Send Announcement Dialog
    if (showAnnouncementDialog) {
        SendAnnouncementDialog(
            batches = batches,
            onDismiss = { showAnnouncementDialog = false },
            onConfirm = { title, msg, batch ->
                onSendAnnouncement(title, msg, batch)
                showAnnouncementDialog = false
                Toast.makeText(context, "Announcement Broadcasted!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 5. Add Performance Note Dialog
    if (showPerformanceNoteDialog != null) {
        val s = showPerformanceNoteDialog!!
        AddPerformanceNoteDialog(
            student = s,
            onDismiss = { showPerformanceNoteDialog = null },
            onConfirm = { note, tag ->
                onAddPerformanceNote(s.studentId, note, tag)
                showPerformanceNoteDialog = null
                Toast.makeText(context, "Assessment added for ${s.fullName}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 6. Student Full Profile & Real-Time Sync Edit Dialog
    if (showStudentDetailDialog != null) {
        StudentDetailModal(
            student = showStudentDetailDialog!!,
            batches = batches,
            attendance = attendanceList,
            onDismiss = { showStudentDetailDialog = null },
            onToggleStatus = { s, curr -> onToggleStudentStatus(s, curr) },
            onResetPin = { id, pin ->
                onResetPin(id, pin)
                Toast.makeText(context, "PIN updated & synced to student app!", Toast.LENGTH_SHORT).show()
            },
            onSaveStudent = { updatedStudent ->
                onUpdateStudent?.invoke(updatedStudent)
                showStudentDetailDialog = null
                Toast.makeText(context, "Profile updated & synced to ${updatedStudent.fullName}'s app!", Toast.LENGTH_LONG).show()
            }
        )
    }

    // 7. Report Viewer & CSV Export Dialog
    if (showReportViewerDialog != null) {
        val (title, csvContent) = showReportViewerDialog!!
        ReportPreviewDialog(
            title = title,
            csvData = csvContent,
            onDismiss = { showReportViewerDialog = null }
        )
    }

    // 8. Academy Official PhonePe QR Code Dialog
    if (showAcademyQrDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showAcademyQrDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(22.dp)),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = { showAcademyQrDialog = false },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF1E1730))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    val activeInstructorUpi = remember(instructor) {
                        UpiPaymentHelper.getInstructorUpiInfo(instructor?.fullName, null, null)
                    }
                    PhonePeQrCard(
                        amount = 0.0,
                        payeeName = activeInstructorUpi.payeeName,
                        upiId = activeInstructorUpi.upiId,
                        note = "XMA Fee - ${activeInstructorUpi.instructorName}",
                        showConfirmFields = false
                    )
                }
            }
        }
    }

    if (showAppDownloadQrDialog) {
        AppDownloadQrModal(onDismiss = { showAppDownloadQrDialog = false })
    }
}

// -------------------------------------------------------------
// TAB 1: STUDENTS DIRECTORY & MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun StudentsTab(
    students: List<StudentEntity>,
    batches: List<BatchEntity>,
    onOpenAdd: () -> Unit,
    onSelectStudent: (StudentEntity) -> Unit,
    onRecordFee: (StudentEntity) -> Unit,
    onAddNote: (StudentEntity) -> Unit,
    onToggleStatus: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBatchFilter by remember { mutableStateOf("ALL") }

    val filtered = students.filter { s ->
        val matchesSearch = s.fullName.contains(searchQuery, ignoreCase = true) ||
                s.studentId.contains(searchQuery, ignoreCase = true) ||
                s.beltRank.contains(searchQuery, ignoreCase = true)
        val matchesBatch = selectedBatchFilter == "ALL" || s.batchId == selectedBatchFilter
        matchesSearch && matchesBatch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STUDENT ENROLLMENTS (${filtered.size})",
                    color = XmaTextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Button(
                    onClick = onOpenAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("REGISTER STUDENT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, ID, or belt rank...", color = XmaTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = XmaRedPrimary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = XmaSurface,
                    unfocusedContainerColor = XmaSurface,
                    focusedBorderColor = XmaRedPrimary,
                    unfocusedBorderColor = XmaOutline,
                    focusedTextColor = XmaTextWhite,
                    unfocusedTextColor = XmaTextWhite
                ),
                singleLine = true
            )
        }

        // Batch Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isSel = selectedBatchFilter == "ALL"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) XmaRedPrimary else XmaSurfaceVariant)
                            .clickable { selectedBatchFilter = "ALL" }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("ALL BATCHES", color = if (isSel) Color.White else XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                items(batches) { b ->
                    val isSel = selectedBatchFilter == b.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) XmaRedPrimary else XmaSurfaceVariant)
                            .clickable { selectedBatchFilter = b.id }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(b.name, color = if (isSel) Color.White else XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Student Cards
        items(filtered) { student ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectStudent(student) },
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(XmaRedPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SportsMartialArts, contentDescription = null, tint = XmaRedPrimary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(student.fullName, color = XmaTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                Text("${student.studentId} • ${student.batchName}", color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        BeltRankBadge(rank = student.beltRank)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatusBadge(status = student.accountStatus)
                            StatusBadge(status = student.feeStatus)
                        }

                        // Quick actions
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { onSelectStudent(student) },
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, XmaRedPrimary)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = XmaRedPrimary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("EDIT", color = XmaRedPrimary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                            if (student.feeStatus != "Paid") {
                                Button(
                                    onClick = { onRecordFee(student) },
                                    colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold)
                                ) {
                                    Text("COLLECT", color = XmaGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Button(
                                onClick = { onAddNote(student) },
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("+ NOTE", color = XmaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB: ACADEMY BRANCHES & BATCHES
// -------------------------------------------------------------
@Composable
private fun BranchesTab(
    batches: List<BatchEntity>,
    students: List<StudentEntity>,
    onOpenAddBatch: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACADEMY BRANCHES & BATCHES",
                        color = XmaTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${batches.size} Active Training Centers",
                        color = XmaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = onOpenAddBatch,
                    colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD BATCH", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(batches) { batch ->
            val branchStudents = students.filter { s ->
                s.batchId == batch.id || s.batchName.equals(batch.name, ignoreCase = true)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2D1832))
                                    .border(1.dp, XmaGold, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsMartialArts,
                                    contentDescription = null,
                                    tint = XmaGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = batch.name,
                                    color = XmaTextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (batch.branchName.isNotBlank() && batch.branchName != batch.name)
                                        "${batch.branchName} Center"
                                    else
                                        "Official Dojo Branch",
                                    color = XmaGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(XmaRedPrimary.copy(alpha = 0.2f))
                                .border(1.dp, XmaRedPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${branchStudents.size} Enrolled",
                                color = XmaTextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Instructor and Timing Info Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(XmaSurfaceVariant)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = XmaGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Head Instructor: ",
                                color = XmaTextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = batch.instructorName,
                                color = XmaTextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = XmaGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Training Timings: ",
                                color = XmaTextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = batch.timing,
                                color = XmaTextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = XmaSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Schedule Days: ",
                                color = XmaTextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = batch.days,
                                color = XmaTextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Enrolled students preview
                    if (branchStudents.isNotEmpty()) {
                        Text(
                            text = "REGISTERED STUDENTS IN THIS BRANCH:",
                            color = XmaTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(branchStudents) { st ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E1628))
                                        .border(1.dp, XmaOutline, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(XmaRedPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(st.fullName.take(1), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(st.fullName, color = XmaTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(st.beltRank, color = XmaGold, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No students currently enrolled in this branch",
                            color = XmaTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: ATTENDANCE LOGS & VERIFICATION
// -------------------------------------------------------------
@Composable
private fun AttendanceTab(
    attendance: List<AttendanceEntity>,
    students: List<StudentEntity>,
    todayDate: String,
    onMarkAttendance: ((String, String) -> Unit)? = null
) {
    var filterDate by remember { mutableStateOf(todayDate) }
    var isPhotoGalleryView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRecordForPhotoInspection by remember { mutableStateOf<AttendanceEntity?>(null) }

    val dateFilteredLogs = attendance.filter { it.date == filterDate }
    val filteredLogs = if (searchQuery.isBlank()) {
        dateFilteredLogs
    } else {
        dateFilteredLogs.filter {
            it.studentName.contains(searchQuery, ignoreCase = true) ||
            it.studentId.contains(searchQuery, ignoreCase = true)
        }
    }
    val todayPresentStudentIds = attendance.filter { it.date == todayDate && it.status == "Present" }.map { it.studentId }.toSet()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STUDENT ATTENDANCE & PHOTO TRACKER",
                            color = XmaTextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Track live biometric selfies & CameraX proofs",
                            color = XmaTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "Date: $filterDate",
                        color = XmaGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // View Mode Selector (List & Selfies vs Photo Gallery)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isPhotoGalleryView = false },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isPhotoGalleryView) XmaRedPrimary else XmaSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = if (!isPhotoGalleryView) null else androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LIST & SELFIES", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isPhotoGalleryView = true },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPhotoGalleryView) XmaRedPrimary else XmaSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = if (isPhotoGalleryView) null else androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PHOTO GALLERY", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search student photo by name or ID...", color = XmaTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = XmaGold, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = XmaTextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurface,
                        unfocusedContainerColor = XmaSurface,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite,
                        focusedBorderColor = XmaRedPrimary,
                        unfocusedBorderColor = XmaOutline
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Date-Wise Photo Filter Card & Horizontal Date Selector
            item {
                val availableDates = remember(attendance, todayDate) {
                    val allDates = (listOf(todayDate) + attendance.map { it.date }).distinct()
                    allDates.sortedDescending()
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "DATE-WISE ATTENDANCE PHOTO CHECK",
                                    color = XmaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    "Selected Date: $filterDate (${dateFilteredLogs.size} records)",
                                    color = XmaTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (filterDate != todayDate) {
                                Button(
                                    onClick = { filterDate = todayDate },
                                    colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("TODAY", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Horizontal Date Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(availableDates) { d ->
                                val count = attendance.count { it.date == d }
                                val isSelected = d == filterDate
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) XmaRedPrimary else XmaSurfaceVariant)
                                        .border(1.dp, if (isSelected) XmaGold else XmaOutline, RoundedCornerShape(8.dp))
                                        .clickable { filterDate = d }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (d == todayDate) "Today ($d)" else d,
                                            color = if (isSelected) Color.White else XmaTextWhite,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                        )
                                        Text(
                                            text = "$count Photo${if (count == 1) "" else "s"}",
                                            color = if (isSelected) XmaGold else XmaTextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Records for $filterDate", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${dateFilteredLogs.count { it.status == "Present" }} Present",
                                    color = XmaSuccess,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("•", color = XmaTextMuted)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "${filteredLogs.size} Photos Tracked",
                                    color = XmaGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Live Roll Call Section for Sensei
            if (onMarkAttendance != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = XmaSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, XmaRedPrimary.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = XmaRedPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("QUICK ROLL CALL & VERIFICATION", color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
                                }
                                Text(todayDate, color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Mark attendance directly. Students receive instant in-app update & notification.", color = XmaTextMuted, fontSize = 11.sp)

                            students.forEach { st ->
                                val isPresent = todayPresentStudentIds.contains(st.studentId)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(XmaSurfaceVariant.copy(alpha = 0.6f))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(if (isPresent) XmaSuccess.copy(alpha = 0.2f) else XmaOutline.copy(alpha = 0.4f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = st.fullName.take(2).uppercase(),
                                                color = if (isPresent) XmaSuccess else XmaTextMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(st.fullName, color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("${st.beltRank} Belt • ${st.batchName}", color = XmaTextMuted, fontSize = 10.sp)
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { onMarkAttendance(st.studentId, "Present") },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isPresent) XmaSuccess else XmaSurfaceVariant
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            border = if (isPresent) null else androidx.compose.foundation.BorderStroke(1.dp, XmaSuccess)
                                        ) {
                                            Text(if (isPresent) "✓ PRESENT" else "PRESENT", color = if (isPresent) Color.White else XmaSuccess, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        }
                                        Button(
                                            onClick = { onMarkAttendance(st.studentId, "Absent") },
                                            colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, XmaRedPrimary.copy(alpha = 0.5f))
                                        ) {
                                            Text("ABSENT", color = XmaRedPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Attendance Records Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isPhotoGalleryView) "ATTENDANCE PHOTO GALLERY (${filteredLogs.size})" else "VERIFIED ATTENDANCE LOGS (${filteredLogs.size})",
                        color = XmaGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Tap photo to inspect",
                        color = XmaTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            if (filteredLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = XmaSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = XmaTextMuted, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No attendance photos found for $filterDate.", color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("When students submit attendance selfies, their photos appear here.", color = XmaTextMuted, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            } else if (isPhotoGalleryView) {
                // PHOTO GALLERY VIEW: Grid of student selfie photo cards
                val chunks = filteredLogs.chunked(2)
                chunks.forEach { rowPair ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowPair.forEach { att ->
                                val st = students.firstOrNull { it.studentId == att.studentId }
                                Box(modifier = Modifier.weight(1f)) {
                                    AttendanceSelfieCard(
                                        record = att,
                                        student = st,
                                        onInspectPhoto = { selectedRecordForPhotoInspection = it }
                                    )
                                }
                            }
                            if (rowPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // LIST VIEW: Items with selfie photo thumbnail & quick track photo button
                items(filteredLogs) { att ->
                    val st = students.firstOrNull { it.studentId == att.studentId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRecordForPhotoInspection = att },
                        colors = CardDefaults.cardColors(containerColor = XmaSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Real Attendance Selfie Thumbnail
                                AttendanceSelfieThumbnail(
                                    record = att,
                                    student = st,
                                    size = 54.dp,
                                    onClick = { selectedRecordForPhotoInspection = att }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(att.studentName, color = XmaTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("ID: ${att.studentId} • Time: ${att.time}", color = XmaTextMuted, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = XmaSuccess, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Face Match: ${att.verificationScore}%", color = XmaSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                StatusBadge(status = att.status)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { selectedRecordForPhotoInspection = att },
                                    colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaRedPrimary.copy(alpha = 0.6f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = XmaRedPrimary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TRACK PHOTO", color = XmaTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Full Biometric Photo Detail Dialog
        if (selectedRecordForPhotoInspection != null) {
            val inspectingRecord = selectedRecordForPhotoInspection!!
            val inspectingStudent = students.firstOrNull { it.studentId == inspectingRecord.studentId }
            AttendanceSelfieDetailDialog(
                record = inspectingRecord,
                student = inspectingStudent,
                onDismiss = { selectedRecordForPhotoInspection = null },
                onUpdateStatus = onMarkAttendance
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 3: FEES & PAYMENTS
// -------------------------------------------------------------
@Composable
private fun FeesTab(
    students: List<StudentEntity>,
    payments: List<FeePaymentEntity>,
    instructor: InstructorEntity? = null,
    onRecordFee: (StudentEntity) -> Unit,
    onShowQr: () -> Unit,
    onApprovePayment: ((paymentId: Long) -> Unit)? = null,
    onRejectPayment: ((paymentId: Long, reason: String) -> Unit)? = null
) {
    val pendingStudents = students.filter { it.feeStatus != "Paid" }
    val pendingUpiVerifications = payments.filter { it.status.equals("Pending Verification", ignoreCase = true) }
    val verifiedPayments = payments.filter { it.status.equals("Paid", ignoreCase = true) }
    val totalPendingAmount = pendingStudents.sumOf { it.monthlyFee }

    val instructorUpiInfo = remember(instructor) {
        UpiPaymentHelper.getInstructorUpiInfo(instructor?.fullName, null, null)
    }

    var rejectTargetPayment by remember { mutableStateOf<FeePaymentEntity?>(null) }
    var rejectReason by remember { mutableStateOf("Amount not credited in bank account / Invalid UTR") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Academy PhonePe QR Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF120E1E)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF5F259F))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PhonePeLogoBadge(size = 36)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "PhonePe Official QR",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF5F259F))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(instructorUpiInfo.branchName, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                "Payee: ${instructorUpiInfo.payeeName} (${instructorUpiInfo.upiId})",
                                color = Color(0xFFA855F7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Button(
                        onClick = onShowQr,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("SHOW QR", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Month-End Pending Fee Admin Dashboard Overview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1528)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "MONTH-END PENDING FEES DASHBOARD",
                        color = XmaGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = XmaSurface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Students Pending", color = XmaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("${pendingStudents.size}", color = if (pendingStudents.isNotEmpty()) XmaError else XmaSuccess, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                Text("Unpaid Dues", color = XmaTextMuted, fontSize = 9.sp)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = XmaSurface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Total Due Amount", color = XmaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("₹${totalPendingAmount.toInt()}", color = XmaGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                Text("Monthly Dues", color = XmaTextMuted, fontSize = 9.sp)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1.1f),
                            colors = CardDefaults.cardColors(containerColor = XmaSurface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Awaiting Approval", color = XmaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("${pendingUpiVerifications.size}", color = if (pendingUpiVerifications.isNotEmpty()) Color(0xFFBB86FC) else XmaSuccess, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                Text("UPI Proofs", color = XmaTextMuted, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section: UPI Payment Proofs Awaiting Verification
        if (pendingUpiVerifications.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF231435)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFA855F7))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "STUDENT UPI PROOFS AWAITING APPROVAL (${pendingUpiVerifications.size})",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Important: Student ne UPI (PhonePe / GPay / Paytm) ke through payment kiya hai aur 12-digit UTR submit kiya hai. Jab tak aap bank statement check karke APPROVE nahi karenge, tab tak student dashboard par fee PENDING hi rahegi.",
                            color = Color(0xFFD8B4FE),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            items(pendingUpiVerifications) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF9333EA))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.studentName, color = XmaTextWhite, fontSize = 15.sp, fontWeight = FontWeight.Black)
                                Text("Student ID: ${p.studentId} • For Month: ${p.month}", color = XmaTextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF5F259F))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(p.paymentMethod, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("UTR: ${p.transactionId}", color = XmaGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Submitted on: ${p.paymentDate}", color = XmaTextMuted, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${p.amount.toInt()}", color = XmaGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF4A154B))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("VERIFY UTR", color = Color(0xFFE9D5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Approve / Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onApprovePayment?.invoke(p.id) },
                                modifier = Modifier.weight(1.3f),
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSuccess),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("APPROVE & MARK PAID", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = { rejectTargetPayment = p },
                                modifier = Modifier.weight(0.7f),
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, XmaError),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = XmaError)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("REJECT", color = XmaError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Month-End Pending Student Fees List
        item {
            SectionHeader(title = "MONTH-END UNPAID & PENDING FEES (${pendingStudents.size})")
        }

        if (pendingStudents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("🎉 All student monthly fees are up-to-date and paid!", color = XmaSuccess, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(pendingStudents) { s ->
                val isUpiPendingApproval = s.feeStatus.equals("Pending Verification", ignoreCase = true)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUpiPendingApproval) Color(0xFFA855F7) else XmaError.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.fullName, color = XmaTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text("${s.studentId} • Batch: ${s.batchName} • Mob: ${s.parentMobile}", color = XmaTextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Fee: ₹${s.monthlyFee.toInt()}", color = XmaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("• Due: ${s.feeDueDate}", color = XmaTextMuted, fontSize = 11.sp)
                            }
                            if (isUpiPendingApproval) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF3B1854))
                                        .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("⏳ Student submitted UPI proof (Verify above)", color = Color(0xFFE9D5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Button(
                            onClick = { onRecordFee(s) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isUpiPendingApproval) Color(0xFF5F259F) else XmaRedPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isUpiPendingApproval) "VIEW / CASH" else "COLLECT CASH", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Recent Receipts
        item {
            SectionHeader(title = "RECENT VERIFIED PAYMENT RECEIPTS (${verifiedPayments.size})")
        }

        items(verifiedPayments) { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(p.studentName, color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Receipt: ${p.receiptNumber} • ${p.month}", color = XmaTextMuted, fontSize = 11.sp)
                        Text("Mode: ${p.paymentMethod} • Txn: ${p.transactionId}", color = XmaTextMuted, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹${p.amount.toInt()}", color = XmaSuccess, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text(p.paymentDate, color = XmaTextMuted, fontSize = 10.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(XmaSuccess.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("PAID", color = XmaSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog
    if (rejectTargetPayment != null) {
        val target = rejectTargetPayment!!
        Dialog(onDismissRequest = { rejectTargetPayment = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaError)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Reject Payment Proof", color = XmaError, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Student: ${target.studentName}\nAmount: ₹${target.amount.toInt()}\nUTR: ${target.transactionId}",
                        color = XmaTextWhite,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Reason for Rejection", color = XmaTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite,
                            focusedBorderColor = XmaError,
                            unfocusedBorderColor = XmaOutline
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { rejectTargetPayment = null },
                            colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant)
                        ) {
                            Text("CANCEL", color = XmaTextWhite)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onRejectPayment?.invoke(target.id, rejectReason)
                                rejectTargetPayment = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = XmaError)
                        ) {
                            Text("CONFIRM REJECT", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: TRAINING VIDEOS MANAGEMENT
// -------------------------------------------------------------
@Composable
private fun TrainingTab(
    videos: List<TrainingVideoEntity>,
    onOpenAddVideo: () -> Unit,
    onDeleteVideo: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACADEMY VIDEO LESSONS (${videos.size})",
                    color = XmaTextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Button(
                    onClick = onOpenAddVideo,
                    colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ADD LESSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(videos) { vid ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(XmaSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = XmaRedPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(vid.title, color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${vid.category} • ${vid.difficulty} • ${vid.duration}", color = XmaGold, fontSize = 11.sp)
                            Text(vid.description, color = XmaTextMuted, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                    IconButton(onClick = { onDeleteVideo(vid.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = XmaError, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: ANNOUNCEMENTS & BROADCASTS
// -------------------------------------------------------------
@Composable
private fun AnnouncementsTab(
    announcements: List<AnnouncementEntity>,
    batches: List<BatchEntity>,
    onOpenCreate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACADEMY BROADCASTS (${announcements.size})",
                    color = XmaTextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Button(
                    onClick = onOpenCreate,
                    colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("BROADCAST", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(announcements) { ann ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ann.title, color = XmaTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        StatusBadge(status = ann.targetBatch)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(ann.message, color = XmaTextMuted, fontSize = 12.sp, lineHeight = 17.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Author: ${ann.authorName}", color = XmaGold, fontSize = 11.sp)
                        Text(ann.date, color = XmaTextMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 6: REPORTS & EXPORT CENTER
// -------------------------------------------------------------
@Composable
private fun ReportsTab(
    onExportReport: (String) -> Unit
) {
    val reportTypes = listOf(
        Pair("Daily Attendance", "CSV of today's check-ins with selfie verification confidence score"),
        Pair("Monthly Attendance", "Complete attendance log across all student sessions"),
        Pair("Fee Pending Report", "List of all active students with pending or overdue fees"),
        Pair("Fee Collection Report", "Payment transaction register with receipts and payment modes"),
        Pair("Training Video Report", "Curriculum inventory of kata, kumite and kickboxing lessons"),
        Pair("Student Performance Report", "Student belt ranks, batch rosters and status registers")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "EXPORT DATA & AUDIT REPORTS (CSV)",
                color = XmaTextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }

        items(reportTypes) { (title, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = XmaTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(desc, color = XmaTextMuted, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onExportReport(title) },
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VIEW CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// APP UPDATE MANAGEMENT TAB (FOR INSTRUCTORS / ADMIN)
// -------------------------------------------------------------
@Composable
private fun AppUpdateAdminTab(
    config: AppUpdateConfigEntity?,
    installedVersionName: String,
    onPublish: (version: String, minVersion: String, downloadUrl: String, notes: String, isMandatory: Boolean) -> Unit,
    onCheckNow: () -> Unit
) {
    var versionInput by remember(config) { mutableStateOf(config?.latestVersion ?: "1.1.0") }
    var minVersionInput by remember(config) { mutableStateOf(config?.minimumSupportedVersion ?: "1.0.0") }
    var downloadUrlInput by remember(config) { mutableStateOf(config?.updateUrl ?: "https://github.com/martial-arts/xma-school/releases/latest/download/xma_school.apk") }
    var releaseNotesInput by remember(config) { mutableStateOf(config?.releaseNotes ?: "• Performance enhancements and bug fixes\n• Real-time updates engine\n• Improved attendance selfie verification") }
    var isMandatoryInput by remember(config) { mutableStateOf(config?.updateType.equals("MANDATORY", ignoreCase = true)) }
    var publishSuccessMsg by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0B0E))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "APP UPDATE SYSTEM CONTROL",
                    color = XmaTextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Manage and release real-time app updates to all enrolled students",
                    color = XmaTextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Current status card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = XmaGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CURRENT UPDATE SERVER STATUS", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        StatusBadge(status = if (config?.updateType.equals("MANDATORY", ignoreCase = true)) "MANDATORY ENFORCED" else "ACTIVE")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("This Device App Version", color = XmaTextMuted, fontSize = 11.sp)
                            Text("v$installedVersionName", color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Published Latest Version", color = XmaTextMuted, fontSize = 11.sp)
                            Text("v${config?.latestVersion ?: "1.0.0"}", color = XmaGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Minimum Supported Version", color = XmaTextMuted, fontSize = 11.sp)
                            Text("v${config?.minimumSupportedVersion ?: "1.0.0"}", color = XmaTextWhite, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Published Date", color = XmaTextMuted, fontSize = 11.sp)
                            Text(config?.publishedDate ?: "Initial Release", color = XmaTextMuted, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onCheckNow,
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate App Startup Update Check", color = XmaTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Publish New Release Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PUBLISH NEW VERSION BROADCAST",
                        color = XmaTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Once published, all students with older app versions will immediately receive an Update Available prompt on start.",
                        color = XmaTextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = versionInput,
                            onValueChange = { versionInput = it },
                            label = { Text("New Version Name (e.g. 1.2.0)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = XmaGold,
                                unfocusedBorderColor = XmaOutline,
                                focusedTextColor = XmaTextWhite,
                                unfocusedTextColor = XmaTextWhite
                            )
                        )

                        OutlinedTextField(
                            value = minVersionInput,
                            onValueChange = { minVersionInput = it },
                            label = { Text("Min Version (e.g. 1.0.0)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = XmaGold,
                                unfocusedBorderColor = XmaOutline,
                                focusedTextColor = XmaTextWhite,
                                unfocusedTextColor = XmaTextWhite
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = downloadUrlInput,
                        onValueChange = { downloadUrlInput = it },
                        label = { Text("Secure APK Download URL (HTTPS)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = XmaGold,
                            unfocusedBorderColor = XmaOutline,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = releaseNotesInput,
                        onValueChange = { releaseNotesInput = it },
                        label = { Text("What's New / Release Notes") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = XmaGold,
                            unfocusedBorderColor = XmaOutline,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Force Mandatory Update", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("If enabled, students cannot skip this update and must install it to continue.", color = XmaTextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = isMandatoryInput,
                            onCheckedChange = { isMandatoryInput = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = XmaRedPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (publishSuccessMsg != null) {
                        Text(
                            text = publishSuccessMsg!!,
                            color = XmaSuccess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (versionInput.isNotBlank() && downloadUrlInput.isNotBlank()) {
                                onPublish(
                                    versionInput.trim(),
                                    minVersionInput.trim(),
                                    downloadUrlInput.trim(),
                                    releaseNotesInput.trim(),
                                    isMandatoryInput
                                )
                                publishSuccessMsg = "Version v${versionInput.trim()} published! Students will now see the update dialog."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PUBLISH VERSION TO ALL STUDENTS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "🥋 XMA SCHOOL App Update v${versionInput.trim()}")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🥋 *XMA SCHOOL NEW UPDATE (v${versionInput.trim()}) AVAILABLE!*\n\n" +
                                    "Dear Students & Parents,\n" +
                                    "A new updated version of the XMA SCHOOL application is ready for installation.\n\n" +
                                    "📥 *Download & Install APK Link:*\n${downloadUrlInput.trim()}\n\n" +
                                    "✨ *What's New in This Update:*\n${releaseNotesInput.trim()}\n\n" +
                                    "⚠️ *Important:* Please install this APK over your existing app. All your attendance and profile records will remain completely safe!"
                                )
                            }
                            try {
                                context.startActivity(Intent.createChooser(shareIntent, "Share XMA Update with Students"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open share sheet", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHARE UPDATE LINK (WHATSAPP / SHARE)", color = XmaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // How Students Receive Update Guide Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141822)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = XmaGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HOW STUDENTS RECEIVE THIS UPDATE",
                            color = XmaTextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "1. Students who already have the app installed need the new APK (v1.1.0, Version Code 2).\n" +
                               "2. Export the updated APK from AI Studio Settings / Download APK.\n" +
                               "3. Send the APK or your download link to students on WhatsApp or Google Drive.\n" +
                               "4. When students tap 'Install' on the new APK, Android upgrades the existing app seamlessly without deleting any attendance, belt rank, or student data.\n" +
                               "5. In the new update, Admin ID auto-fill is removed, and role-switching is permanently disabled.",
                        color = XmaTextMuted,
                        fontSize = 11.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPONENT: SCROLLABLE TAB ROW
// -------------------------------------------------------------
@Composable
private fun ScrollableTabRow(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(XmaSurface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(tabs.indices.toList()) { index ->
            val isSelected = selectedTabIndex == index
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) XmaRedPrimary else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tabs[index],
                    color = if (isSelected) Color.White else XmaTextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: ADD NEW STUDENT
// -------------------------------------------------------------
@Composable
private fun AddStudentDialog(
    batches: List<BatchEntity>,
    currentCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (StudentEntity) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("2012-08-15") }
    var gender by remember { mutableStateOf("Male") }
    var parentName by remember { mutableStateOf("") }
    var parentMobile by remember { mutableStateOf("") }
    var studentMobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("Dojo Training Complex") }
    var selectedBatchId by remember { mutableStateOf(batches.firstOrNull()?.id ?: "batch_a") }
    var beltRank by remember { mutableStateOf("White Belt") }
    var customStudentId by remember { mutableStateOf("student%03d".format(currentCount + 1)) }
    var customPassword by remember { mutableStateOf("Xma@123") }
    var passwordVisible by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTER NEW STUDENT",
                        color = XmaTextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Box(
                        modifier = Modifier
                            .background(XmaGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .border(1.dp, XmaGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Instructor Portal", color = XmaGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Instructor-created Credentials Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E162B)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STUDENT LOGIN CREDENTIALS (SET BY INSTRUCTOR)",
                                color = XmaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Set the Login ID and Password for this student. The student will use these exact details to log in to their app.",
                            color = XmaTextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        OutlinedTextField(
                            value = customStudentId,
                            onValueChange = { customStudentId = it },
                            label = { Text("Student Login ID", color = XmaGold) },
                            placeholder = { Text("e.g. STU008, aryan01, rohit2026", color = XmaTextMuted.copy(alpha = 0.5f)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = XmaGold) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = XmaSurfaceVariant,
                                unfocusedContainerColor = XmaSurfaceVariant,
                                focusedBorderColor = XmaGold,
                                unfocusedBorderColor = XmaOutline,
                                focusedTextColor = XmaTextWhite,
                                unfocusedTextColor = XmaTextWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customPassword,
                            onValueChange = { customPassword = it },
                            label = { Text("Student Login Password / PIN", color = XmaGold) },
                            placeholder = { Text("e.g. Xma@123, Pass@2026, 1234", color = XmaTextMuted.copy(alpha = 0.5f)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = XmaGold) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = XmaTextMuted
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = XmaSurfaceVariant,
                                unfocusedContainerColor = XmaSurfaceVariant,
                                focusedBorderColor = XmaGold,
                                unfocusedBorderColor = XmaOutline,
                                focusedTextColor = XmaTextWhite,
                                unfocusedTextColor = XmaTextWhite
                            ),
                            singleLine = true
                        )
                    }
                }

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Student Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("DOB (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = XmaSurfaceVariant,
                            unfocusedContainerColor = XmaSurfaceVariant,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = XmaSurfaceVariant,
                            unfocusedContainerColor = XmaSurfaceVariant,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        ),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = parentMobile,
                        onValueChange = { parentMobile = it },
                        label = { Text("Parent Mobile") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = XmaSurfaceVariant,
                            unfocusedContainerColor = XmaSurfaceVariant,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = studentMobile,
                        onValueChange = { studentMobile = it },
                        label = { Text("Student Mobile") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = XmaSurfaceVariant,
                            unfocusedContainerColor = XmaSurfaceVariant,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        ),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Residential Address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = beltRank,
                    onValueChange = { beltRank = it },
                    label = { Text("Starting Belt (e.g. White Belt, Yellow Belt)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                // Batch / Branch Selection
                Text("Select Academy Branch:", color = XmaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    batches.forEach { b ->
                        val isSel = selectedBatchId == b.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF2D1832) else XmaSurfaceVariant)
                                .border(1.dp, if (isSel) XmaGold else XmaOutline, RoundedCornerShape(8.dp))
                                .clickable { selectedBatchId = b.id }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${b.name} (${b.branchName})",
                                    color = if (isSel) XmaGold else XmaTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Instructor: ${b.instructorName} • ${b.timing}",
                                    color = XmaTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            RadioButton(
                                selected = isSel,
                                onClick = { selectedBatchId = b.id },
                                colors = RadioButtonDefaults.colors(selectedColor = XmaGold, unselectedColor = XmaTextMuted)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = XmaTextWhite)
                    }
                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && customStudentId.isNotBlank()) {
                                val b = batches.firstOrNull { it.id == selectedBatchId } ?: batches.firstOrNull()
                                val assignedId = customStudentId.trim()
                                val assignedPin = if (customPassword.isNotBlank()) customPassword.trim() else "Xma@123"
                                val newStudent = StudentEntity(
                                    studentId = assignedId,
                                    fullName = fullName.trim(),
                                    dob = dob.trim(),
                                    gender = gender.trim(),
                                    parentName = parentName.trim(),
                                    parentMobile = parentMobile.trim(),
                                    studentMobile = studentMobile.trim(),
                                    address = address.trim(),
                                    batchId = b?.id ?: selectedBatchId,
                                    batchName = b?.name ?: "Mega Sports Complex Branch",
                                    joiningDate = "2026-09-01",
                                    instructorName = b?.instructorName ?: "Sensei Raj Sanchu",
                                    monthlyFee = 2500.0,
                                    feeDueDate = "5th of every month",
                                    feeStatus = "Pending",
                                    accountStatus = "Active",
                                    pin = assignedPin,
                                    beltRank = beltRank.trim()
                                )
                                onConfirm(newStudent)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Register", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: ADD BATCH / BRANCH
// -------------------------------------------------------------
@Composable
private fun AddBatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (BatchEntity) -> Unit
) {
    var branchNameInput by remember { mutableStateOf("") }
    var batchNameInput by remember { mutableStateOf("") }
    var timingInput by remember { mutableStateOf("6:00 PM – 7:30 PM") }
    var daysInput by remember { mutableStateOf("Mon, Wed, Fri") }
    var instructorInput by remember { mutableStateOf("Sensei Raj Sanchu") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "CREATE NEW BATCH / BRANCH",
                    color = XmaTextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Configure branch schedule & assigned sensei",
                    color = XmaGold,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = branchNameInput,
                    onValueChange = { branchNameInput = it },
                    label = { Text("Branch Name (e.g. Sindri Branch)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = batchNameInput,
                    onValueChange = { batchNameInput = it },
                    label = { Text("Batch / Center Name (e.g. Sindri Branch)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = instructorInput,
                    onValueChange = { instructorInput = it },
                    label = { Text("Instructor Name (Sensei)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = timingInput,
                    onValueChange = { timingInput = it },
                    label = { Text("Batch Timing (e.g. 5:00 PM – 6:30 PM)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = daysInput,
                    onValueChange = { daysInput = it },
                    label = { Text("Training Days (e.g. Mon, Wed, Fri)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = XmaTextWhite)
                    }
                    Button(
                        onClick = {
                            if (batchNameInput.isNotBlank()) {
                                val id = "batch_" + System.currentTimeMillis().toString().takeLast(6)
                                onConfirm(
                                    BatchEntity(
                                        id = id,
                                        name = batchNameInput.trim(),
                                        timing = timingInput.trim(),
                                        days = daysInput.trim(),
                                        instructorName = instructorInput.trim(),
                                        branchName = if (branchNameInput.isNotBlank()) branchNameInput.trim() else batchNameInput.trim()
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Batch", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: RECORD FEE PAYMENT
// -------------------------------------------------------------
@Composable
private fun RecordFeeDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, method: String, txId: String, month: String) -> Unit
) {
    var amountText by remember { mutableStateOf(student.monthlyFee.toInt().toString()) }
    var selectedMethod by remember { mutableStateOf("Cash") }
    var txId by remember { mutableStateOf("") }
    var monthText by remember { mutableStateOf("September 2026") }

    val paymentMethods = listOf("Cash", "UPI", "Bank Transfer", "POS Card")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("RECORD FEE PAYMENT", color = XmaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("Student: ${student.fullName} (${student.studentId})", color = XmaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = monthText,
                    onValueChange = { monthText = it },
                    label = { Text("Billing Month") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                Text("Payment Method:", color = XmaTextMuted, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(paymentMethods) { m ->
                        val isSel = selectedMethod == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSel) XmaRedPrimary else XmaSurfaceVariant)
                                .clickable { selectedMethod = m }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(m, color = if (isSel) Color.White else XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (selectedMethod == "UPI") {
                    var showEmbeddedQr by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF161026))
                            .border(1.dp, Color(0xFF5F259F), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PhonePeLogoBadge(size = 26)
                                Spacer(modifier = Modifier.width(8.dp))
                                val targetStudentUpi = UpiPaymentHelper.getInstructorUpiInfo(student.instructorName, student.batchName, student.batchId)
                                Column {
                                    Text("PhonePe QR • ${targetStudentUpi.instructorName}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(targetStudentUpi.upiId, color = Color(0xFFA855F7), fontSize = 10.sp)
                                }
                            }
                            Button(
                                onClick = { showEmbeddedQr = !showEmbeddedQr },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (showEmbeddedQr) "HIDE QR" else "SHOW QR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (showEmbeddedQr) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val targetStudentUpi = UpiPaymentHelper.getInstructorUpiInfo(student.instructorName, student.batchName, student.batchId)
                            val parsedAmt = amountText.toDoubleOrNull() ?: student.monthlyFee
                            PhonePeQrCard(
                                amount = parsedAmt,
                                payeeName = targetStudentUpi.payeeName,
                                upiId = targetStudentUpi.upiId,
                                note = "Fee - ${student.studentId}",
                                showConfirmFields = false
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = txId,
                    onValueChange = { txId = it },
                    label = { Text("Reference / UPI Transaction ID (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = XmaTextWhite)
                    }
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: student.monthlyFee
                            onConfirm(amt, selectedMethod, txId, monthText)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Receipt", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: ADD VIDEO
// -------------------------------------------------------------
@Composable
private fun AddVideoDialog(
    instructorName: String,
    onDismiss: () -> Unit,
    onConfirm: (TrainingVideoEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("KATA") }
    var difficulty by remember { mutableStateOf("Beginner") }
    var duration by remember { mutableStateOf("10 mins") }
    var description by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("video_stream_placeholder") }

    val categories = listOf("KATA", "KUMITE", "KICKBOXING", "SELF DEFENCE", "FITNESS")
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("PUBLISH TRAINING VIDEO", color = XmaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Video Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                Text("Category:", color = XmaTextMuted, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val isSel = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) XmaRedPrimary else XmaSurfaceVariant)
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(cat, color = if (isSel) Color.White else XmaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("Difficulty:", color = XmaTextMuted, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(difficulties) { d ->
                        val isSel = difficulty == d
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) XmaGold.copy(alpha = 0.3f) else XmaSurfaceVariant)
                                .clickable { difficulty = d }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(d, color = if (isSel) XmaGold else XmaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (e.g. 12 mins)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Syllabus notes") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    minLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = XmaTextWhite)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val vid = TrainingVideoEntity(
                                    id = "vid_${System.currentTimeMillis()}",
                                    title = title.trim(),
                                    category = category,
                                    difficulty = difficulty,
                                    duration = duration.trim(),
                                    description = description.trim(),
                                    instructorName = instructorName,
                                    videoUrl = videoUrl
                                )
                                onConfirm(vid)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Publish", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: SEND ANNOUNCEMENT
// -------------------------------------------------------------
@Composable
private fun SendAnnouncementDialog(
    batches: List<BatchEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, msg: String, batch: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    var selectedBatch by remember { mutableStateOf("All") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("BROADCAST ANNOUNCEMENT", color = XmaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Announcement Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    singleLine = true
                )

                Text("Target Batch:", color = XmaTextMuted, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        val isSel = selectedBatch == "All"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) XmaRedPrimary else XmaSurfaceVariant)
                                .clickable { selectedBatch = "All" }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("All Batches", color = if (isSel) Color.White else XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(batches) { b ->
                        val isSel = selectedBatch == b.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) XmaRedPrimary else XmaSurfaceVariant)
                                .clickable { selectedBatch = b.name }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(b.name, color = if (isSel) Color.White else XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = msg,
                    onValueChange = { msg = it },
                    label = { Text("Announcement Message") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = XmaTextWhite)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && msg.isNotBlank()) {
                                onConfirm(title.trim(), msg.trim(), selectedBatch)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Broadcast", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: ADD PERFORMANCE NOTE
// -------------------------------------------------------------
@Composable
private fun AddPerformanceNoteDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onConfirm: (note: String, tag: String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Technique") }
    val tags = listOf("Technique", "Discipline", "Belt Exam", "Stamina", "Sparring")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ADD COACH FEEDBACK", color = XmaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("Student: ${student.fullName} (${student.studentId})", color = XmaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Text("Tag:", color = XmaTextMuted, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(tags) { t ->
                        val isSel = selectedTag == t
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) XmaRedPrimary else XmaSurfaceVariant)
                                .clickable { selectedTag = t }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(t, color = if (isSel) Color.White else XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Performance observations & training focus") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmaSurfaceVariant,
                        unfocusedContainerColor = XmaSurfaceVariant,
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite
                    ),
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = XmaTextWhite)
                    }
                    Button(
                        onClick = {
                            if (note.isNotBlank()) {
                                onConfirm(note.trim(), selectedTag)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Note", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: STUDENT FULL DETAIL & REAL-TIME SYNC EDIT MODAL
// -------------------------------------------------------------
@Composable
private fun StudentDetailModal(
    student: StudentEntity,
    batches: List<BatchEntity>,
    attendance: List<AttendanceEntity> = emptyList(),
    onDismiss: () -> Unit,
    onToggleStatus: (String, String) -> Unit,
    onResetPin: (String, String) -> Unit,
    onSaveStudent: (StudentEntity) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }

    // Editable state fields
    var fullName by remember { mutableStateOf(student.fullName) }
    var selectedBelt by remember { mutableStateOf(student.beltRank) }
    var selectedBatch by remember {
        mutableStateOf(batches.firstOrNull { it.id == student.batchId } ?: batches.firstOrNull() ?: BatchEntity(id = student.batchId, name = student.batchName, timing = "6:00 AM", days = "Mon-Fri", instructorName = student.instructorName, branchName = "Main Branch"))
    }
    var feeStatus by remember { mutableStateOf(student.feeStatus) }
    var monthlyFeeStr by remember { mutableStateOf(student.monthlyFee.toInt().toString()) }
    var feeDueDate by remember { mutableStateOf(student.feeDueDate) }
    var studentMobile by remember { mutableStateOf(student.studentMobile) }
    var parentMobile by remember { mutableStateOf(student.parentMobile) }
    var address by remember { mutableStateOf(student.address) }
    var newPinInput by remember { mutableStateOf("") }

    val beltOptions = listOf("White", "Yellow", "Orange", "Green", "Blue", "Purple", "Brown", "Black")
    val feeStatusOptions = listOf("Paid", "Pending", "Overdue")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEditMode) "EDIT STUDENT PROFILE" else "STUDENT PROFILE",
                            color = XmaTextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "ID: ${student.studentId} • Live Sync Enabled",
                            color = XmaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { isEditMode = !isEditMode },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditMode) XmaRedPrimary else XmaSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEditMode) "CANCEL" else "EDIT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(XmaOutline))

                if (!isEditMode) {
                    // View Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(student.fullName, color = XmaTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        BeltRankBadge(rank = student.beltRank)
                    }

                    InfoRow(label = "Date of Birth", value = student.dob)
                    InfoRow(label = "Gender", value = student.gender)
                    InfoRow(label = "Branch & Batch", value = "${student.batchName} (${student.instructorName})")
                    InfoRow(label = "Parent / Guardian", value = student.parentName)
                    InfoRow(label = "Parent Mobile", value = student.parentMobile)
                    InfoRow(label = "Student Mobile", value = student.studentMobile)
                    InfoRow(label = "Address", value = student.address)
                    InfoRow(label = "Joining Date", value = student.joiningDate)
                    InfoRow(label = "Monthly Fee", value = "₹${student.monthlyFee.toInt()}")
                    InfoRow(label = "Fee Due Date", value = student.feeDueDate)
                    InfoRow(label = "Fee Status", value = student.feeStatus)
                    InfoRow(label = "Login PIN", value = student.pin)
                } else {
                    // Edit Mode
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Full Name", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = XmaSurfaceVariant,
                                unfocusedContainerColor = XmaSurfaceVariant,
                                focusedTextColor = XmaTextWhite,
                                unfocusedTextColor = XmaTextWhite
                            ),
                            singleLine = true
                        )

                        Text("Belt Rank (Promotion)", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(beltOptions) { belt ->
                                val isSelected = selectedBelt.equals(belt, ignoreCase = true)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedBelt = belt }
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) XmaRedPrimary else XmaOutline,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    color = if (isSelected) XmaRedPrimary.copy(alpha = 0.2f) else XmaSurfaceVariant
                                ) {
                                    Text(
                                        text = "$belt Belt",
                                        color = if (isSelected) XmaRedPrimary else XmaTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Text("Assigned Branch & Batch", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            batches.forEach { b ->
                                val isSel = selectedBatch.id == b.id
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedBatch = b }
                                        .border(
                                            width = if (isSel) 2.dp else 1.dp,
                                            color = if (isSel) XmaGold else XmaOutline,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    color = if (isSel) XmaGold.copy(alpha = 0.15f) else XmaSurfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("${b.branchName} • ${b.name}", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Sensei ${b.instructorName} • ${b.timing}", color = XmaTextMuted, fontSize = 10.sp)
                                        }
                                        if (isSel) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Text("Fee Status", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            feeStatusOptions.forEach { st ->
                                val isSel = feeStatus.equals(st, ignoreCase = true)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { feeStatus = st }
                                        .border(
                                            width = if (isSel) 2.dp else 1.dp,
                                            color = if (isSel) when (st) {
                                                "Paid" -> XmaSuccess
                                                "Overdue" -> XmaRedPrimary
                                                else -> XmaGold
                                            } else XmaOutline,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    color = if (isSel) when (st) {
                                        "Paid" -> XmaSuccess.copy(alpha = 0.2f)
                                        "Overdue" -> XmaRedPrimary.copy(alpha = 0.2f)
                                        else -> XmaGold.copy(alpha = 0.2f)
                                    } else XmaSurfaceVariant
                                ) {
                                    Text(
                                        text = st,
                                        color = if (isSel) when (st) {
                                            "Paid" -> XmaSuccess
                                            "Overdue" -> XmaRedPrimary
                                            else -> XmaGold
                                        } else XmaTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Monthly Fee (₹)", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = monthlyFeeStr,
                                    onValueChange = { if (it.all { ch -> ch.isDigit() }) monthlyFeeStr = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = XmaSurfaceVariant,
                                        unfocusedContainerColor = XmaSurfaceVariant,
                                        focusedTextColor = XmaTextWhite,
                                        unfocusedTextColor = XmaTextWhite
                                    ),
                                    singleLine = true
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Due Date", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = feeDueDate,
                                    onValueChange = { feeDueDate = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = XmaSurfaceVariant,
                                        unfocusedContainerColor = XmaSurfaceVariant,
                                        focusedTextColor = XmaTextWhite,
                                        unfocusedTextColor = XmaTextWhite
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Student Mobile", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = studentMobile,
                                    onValueChange = { studentMobile = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = XmaSurfaceVariant,
                                        unfocusedContainerColor = XmaSurfaceVariant,
                                        focusedTextColor = XmaTextWhite,
                                        unfocusedTextColor = XmaTextWhite
                                    ),
                                    singleLine = true
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Parent Mobile", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = parentMobile,
                                    onValueChange = { parentMobile = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = XmaSurfaceVariant,
                                        unfocusedContainerColor = XmaSurfaceVariant,
                                        focusedTextColor = XmaTextWhite,
                                        unfocusedTextColor = XmaTextWhite
                                    ),
                                    singleLine = true
                                )
                            }
                        }

                        Text("Address", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = XmaSurfaceVariant,
                                unfocusedContainerColor = XmaSurfaceVariant,
                                focusedTextColor = XmaTextWhite,
                                unfocusedTextColor = XmaTextWhite
                            ),
                            singleLine = true
                        )

                        // Action button to save and broadcast update to student
                        Button(
                            onClick = {
                                val feeVal = monthlyFeeStr.toDoubleOrNull() ?: student.monthlyFee
                                val updated = student.copy(
                                    fullName = fullName.trim(),
                                    beltRank = selectedBelt,
                                    batchId = selectedBatch.id,
                                    batchName = "${selectedBatch.branchName} - ${selectedBatch.name}",
                                    instructorName = selectedBatch.instructorName,
                                    feeStatus = feeStatus,
                                    monthlyFee = feeVal,
                                    feeDueDate = feeDueDate.trim(),
                                    studentMobile = studentMobile.trim(),
                                    parentMobile = parentMobile.trim(),
                                    address = address.trim()
                                )
                                onSaveStudent(updated)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE & SYNC TO STUDENT APP", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                // Attendance Biometric Photos History for this student
                val studentSelfies = attendance.filter { it.studentId == student.studentId }.sortedByDescending { "${it.date} ${it.time}" }
                var inspectingSelfie by remember { mutableStateOf<AttendanceEntity?>(null) }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ATTENDANCE PHOTOS (${studentSelfies.size})",
                            color = XmaGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Tap photo to track & audit",
                            color = XmaTextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    if (studentSelfies.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(XmaSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No attendance selfie records found yet.",
                                color = XmaTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(studentSelfies.take(10)) { selfie ->
                                Card(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .clickable { inspectingSelfie = selfie },
                                    colors = CardDefaults.cardColors(containerColor = XmaSurfaceVariant),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AttendanceSelfieThumbnail(
                                            record = selfie,
                                            student = student,
                                            size = 48.dp,
                                            onClick = { inspectingSelfie = selfie }
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(selfie.date, color = XmaTextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text("${selfie.verificationScore}% Match", color = XmaSuccess, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                if (inspectingSelfie != null) {
                    AttendanceSelfieDetailDialog(
                        record = inspectingSelfie!!,
                        student = student,
                        onDismiss = { inspectingSelfie = null }
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(XmaOutline))

                // Toggle Account Active / Inactive
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Account Status", color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Active students can mark attendance & access dojo", color = XmaTextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = student.accountStatus.equals("Active", ignoreCase = true),
                        onCheckedChange = { onToggleStatus(student.studentId, student.accountStatus) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = XmaSuccess
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Student Login ID and Password Management by Instructor
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F172B)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "STUDENT LOGIN CREDENTIALS",
                                    color = XmaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "Login ID: ${student.studentId}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Student uses Login ID '${student.studentId}' or mobile '${student.studentMobile}' to log in.",
                            color = XmaTextMuted,
                            fontSize = 10.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newPinInput,
                                onValueChange = { if (it.length <= 20) newPinInput = it },
                                placeholder = { Text("Set new student password / PIN", color = XmaTextMuted, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = XmaSurfaceVariant,
                                    unfocusedContainerColor = XmaSurfaceVariant,
                                    focusedBorderColor = XmaGold,
                                    unfocusedBorderColor = XmaOutline,
                                    focusedTextColor = XmaTextWhite,
                                    unfocusedTextColor = XmaTextWhite
                                ),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (newPinInput.isNotBlank()) {
                                        onResetPin(student.studentId, newPinInput.trim())
                                        newPinInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("UPDATE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = XmaTextWhite)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: REPORT CSV PREVIEW
// -------------------------------------------------------------
@Composable
private fun ReportPreviewDialog(
    title: String,
    csvData: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(18.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = XmaTextWhite, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = XmaTextMuted)
                    }
                }

                Text("CSV Export Generated Ready for Excel / Sheets:", color = XmaGold, fontSize = 11.sp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFF07080A), RoundedCornerShape(8.dp))
                        .border(1.dp, XmaOutline, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = csvData,
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "$title exported to downloads folder!", Toast.LENGTH_LONG).show()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaSuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("DOWNLOAD CSV FILE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = XmaTextMuted, fontSize = 12.sp)
        Text(value, color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
