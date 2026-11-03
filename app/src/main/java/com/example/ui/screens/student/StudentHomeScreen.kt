package com.example.ui.screens.student

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.FitnessCenter
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrainingVideoEntity
import com.example.ui.components.AttendanceIncentiveRewardCard
import com.example.ui.components.BeltRankBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.StepCountMotionCard
import com.example.ui.components.UpiPaymentHelper
import com.example.util.YouTubeHelper
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

data class TournamentShowcaseItem(
    val title: String,
    val category: String,
    val location: String,
    val description: String,
    val badge: String,
    val gradient: List<Color>
)

@Composable
fun StudentHomeScreen(
    student: StudentEntity,
    todayAttendance: AttendanceEntity?,
    attendancePercentage: Int,
    overallProgress: Int,
    videos: List<TrainingVideoEntity>,
    announcements: List<AnnouncementEntity>,
    hasUpdateAvailable: Boolean = false,
    updateVersionName: String = "",
    onOpenMarkAttendance: () -> Unit,
    onOpenPayFee: () -> Unit,
    onWatchVideo: (TrainingVideoEntity) -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onOpenGeminiCoach: () -> Unit = {},
    onOpenUpdate: (() -> Unit)? = null,
    onSubmitUpiProof: ((utr: String, method: String, onResult: (Boolean, String) -> Unit) -> Unit)? = null,
    stepCount: Int = 0,
    stepGoal: Int = 6000,
    cadenceSpm: Int = 0,
    motionActivity: String = "Stationary",
    distanceMeters: Double = 0.0,
    caloriesBurned: Double = 0.0,
    activeMinutes: Int = 0,
    isStepTracking: Boolean = true,
    onOpenStepTracker: () -> Unit = {}
) {
    val context = LocalContext.current
    val upiInfo = remember(student.instructorName, student.batchName, student.batchId) {
        UpiPaymentHelper.getInstructorUpiInfo(student.instructorName, student.batchName, student.batchId)
    }
    var showUtrDialog by remember { mutableStateOf(false) }
    var enteredUtr by remember { mutableStateOf("") }
    var utrError by remember { mutableStateOf<String?>(null) }
    var isSubmittingUtr by remember { mutableStateOf(false) }
    var utrFeedbackMsg by remember { mutableStateOf<String?>(null) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0B0E))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // All updates are integrated; no intrusive update alert is displayed to students

        // 1. Child-friendly Student Greeting Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(XmaRedPrimary, XmaRedDark))
                            )
                            .border(2.dp, XmaGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (student.profilePhotoUri.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(student.profilePhotoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.SportsMartialArts,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "OSS, ${student.fullName}!",
                                color = XmaTextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ID: ${student.studentId}",
                                color = XmaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•",
                                color = XmaTextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = student.batchName,
                                color = XmaTextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        BeltRankBadge(rank = student.beltRank)
                    }
                }
            }
        }

        // 1.5 NEW APP UPDATE ALERT BANNER (If update available)
        if (hasUpdateAvailable) {
            item {
                Card(
                    onClick = { onOpenUpdate?.invoke() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1500)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, XmaGold)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(XmaGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "NEW UPDATE v$updateVersionName AVAILABLE!",
                                    color = XmaGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Tap to update app & unlock Ask Gemini AI",
                                    color = XmaTextWhite,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Button(
                            onClick = { onOpenUpdate?.invoke() },
                            colors = ButtonDefaults.buttonColors(containerColor = XmaGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("UPDATE", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // 2. REAL-TIME STEP COUNT & MOTION TRACKER
        item {
            StepCountMotionCard(
                currentSteps = stepCount,
                dailyGoal = stepGoal,
                cadenceSpm = cadenceSpm,
                motionActivity = motionActivity,
                distanceMeters = distanceMeters,
                caloriesBurned = caloriesBurned,
                activeMinutes = activeMinutes,
                isTracking = isStepTracking,
                onOpenTracker = onOpenStepTracker
            )
        }

        // 2.1 SPECIAL REWARD INCENTIVE: 100% ATTENDANCE = WIRELESS NECKBAND FREE
        item {
            AttendanceIncentiveRewardCard(
                attendancePercentage = attendancePercentage,
                onNavigateToAttendance = onNavigateToAttendance
            )
        }

        // 2.5 ASK GEMINI ABOUT YOUR TRAINING & DIET
        item {
            Card(
                onClick = onOpenGeminiCoach,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14121F)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFFFF3366), XmaGold, Color(0xFF8B5CF6))))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFFF3366), Color(0xFFFF9900))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ASK GEMINI AI",
                                        color = XmaGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFF3366).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, Color(0xFFFF3366), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("NEW", color = Color(0xFFFF3366), fontSize = 8.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                Text(
                                    text = "Your Training & Diet Coach",
                                    color = XmaTextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Button(
                            onClick = onOpenGeminiCoach,
                            colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("ASK NOW", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Instant AI answers for Kata techniques, Kumite drills, flexibility stretching, pre & post workout diet plans customized for ${student.fullName}.",
                        color = XmaTextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF201832))
                                .border(0.5.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Martial Arts Drills", color = Color(0xFFE9D5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF281C10))
                                .border(0.5.dp, XmaGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = XmaGold, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Athlete Diet & Nutrition", color = Color(0xFFFEF08A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. HERO "MARK ATTENDANCE" ACTION CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (todayAttendance != null) XmaSurfaceVariant else Color(0xFF1E1117)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (todayAttendance != null) XmaSuccess.copy(alpha = 0.6f) else XmaRedPrimary
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (todayAttendance != null) XmaSuccess.copy(alpha = 0.2f) else XmaRedPrimary.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (todayAttendance != null) Icons.Default.CheckCircle else Icons.Default.Face,
                                    contentDescription = null,
                                    tint = if (todayAttendance != null) XmaSuccess else XmaRedPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TODAY'S ATTENDANCE",
                                    color = XmaTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (todayAttendance != null) "Present (Verified)" else "Not Marked Yet",
                                    color = if (todayAttendance != null) XmaSuccess else XmaTextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        StatusBadge(status = if (todayAttendance != null) "PRESENT" else "NOT MARKED")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (todayAttendance == null) {
                        Button(
                            onClick = onOpenMarkAttendance,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MARK ATTENDANCE WITH SELFIE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(XmaSurface, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Recorded Today", color = XmaTextMuted, fontSize = 11.sp)
                                Text("${todayAttendance.date} at ${todayAttendance.time}", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onNavigateToAttendance,
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("View Log", color = XmaTextWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Metrics Row (Attendance % + Total Days + Training Progress)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Attendance",
                    value = "$attendancePercentage%",
                    subtitle = "This Month",
                    icon = Icons.Default.Schedule,
                    accentColor = XmaSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAttendance
                )
                MetricCard(
                    title = "Training",
                    value = "$overallProgress%",
                    subtitle = "Course Progress",
                    icon = Icons.Default.TrendingUp,
                    accentColor = XmaGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTraining
                )
            }
        }

        // 4. MONTH-END STUDENT PENDING FEE SECTION (Student Dashboard)
        item {
            if (student.feeStatus != "Paid") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (student.feeStatus == "Overdue") Color(0xFF2B0E12) else Color(0xFF26180A)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (student.feeStatus == "Overdue") XmaError else XmaWarning
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (student.feeStatus == "Overdue") XmaError.copy(alpha = 0.25f) else XmaWarning.copy(alpha = 0.25f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = if (student.feeStatus == "Overdue") XmaError else XmaWarning,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MONTH-END PENDING TRAINING FEE",
                                    color = if (student.feeStatus == "Overdue") XmaError else XmaGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Dues for September 2026 • Regular Training Batch",
                                    color = XmaTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            StatusBadge(status = student.feeStatus.uppercase())
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fee Details Summary Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF130E14), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Student Name & ID:", color = XmaTextMuted, fontSize = 11.sp)
                                Text("${student.fullName} (${student.studentId})", color = XmaTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Enrolled Branch:", color = XmaTextMuted, fontSize = 11.sp)
                                Text(student.batchName, color = XmaTextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Assigned Sensei:", color = XmaTextMuted, fontSize = 11.sp)
                                Text("${upiInfo.instructorName} (${upiInfo.branchName})", color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Due Date:", color = XmaTextMuted, fontSize = 11.sp)
                                Text(student.feeDueDate, color = XmaWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF2A1C28)))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Month-End Dues:", color = XmaTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("₹${student.monthlyFee.toInt()}", color = XmaGold, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Strict Pendency Warning Notice
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1408)),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7A4A00))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📌 STRICT PENDENCY POLICY",
                                    color = XmaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Jab tak student UPI (PhonePe / GPay / Paytm) se fee pay karke 12-digit UTR enter nahi karega aur Sensei verify nahi karenge, tab tak dashboard par Payment PENDING hi dikhegi.",
                                    color = Color(0xFFFFD599),
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dedicated Sensei UPI Gateway Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1430)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF5F259F))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode2,
                                            contentDescription = null,
                                            tint = Color(0xFFA855F7),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ASSIGNED SENSEI UPI GATEWAY",
                                            color = Color(0xFFA855F7),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF351952))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = upiInfo.branchName,
                                            color = XmaGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = upiInfo.instructorName,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "UPI ID: ${upiInfo.upiId}",
                                            color = Color(0xFFFFD599),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    // 1-Tap Copy UPI ID
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("UPI ID", upiInfo.upiId)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied UPI ID: ${upiInfo.upiId}", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("COPY", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "📌 Note: Kripya monthly training fee sirf ${upiInfo.instructorName} ke is authorized UPI ID (${upiInfo.upiId}) par hi pay karein.",
                                    color = XmaTextMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Direct UPI Launch Section
                        Text(
                            text = "PAY DIRECTLY VIA YOUR UPI PLATFORM:",
                            color = XmaTextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // PhonePe Button
                            Button(
                                onClick = {
                                    UpiPaymentHelper.launchUpiPlatform(
                                        context = context,
                                        amount = student.monthlyFee,
                                        studentId = student.studentId,
                                        studentName = student.fullName,
                                        targetPackage = UpiPaymentHelper.PHONEPE_PACKAGE,
                                        appName = "PhonePe",
                                        upiId = upiInfo.upiId,
                                        payeeName = upiInfo.payeeName
                                    )
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("PhonePe", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Google Pay Button
                            Button(
                                onClick = {
                                    UpiPaymentHelper.launchUpiPlatform(
                                        context = context,
                                        amount = student.monthlyFee,
                                        studentId = student.studentId,
                                        studentName = student.fullName,
                                        targetPackage = UpiPaymentHelper.GPAY_PACKAGE,
                                        appName = "Google Pay",
                                        upiId = upiInfo.upiId,
                                        payeeName = upiInfo.payeeName
                                    )
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("GPay", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Paytm Button
                            Button(
                                onClick = {
                                    UpiPaymentHelper.launchUpiPlatform(
                                        context = context,
                                        amount = student.monthlyFee,
                                        studentId = student.studentId,
                                        studentName = student.fullName,
                                        targetPackage = UpiPaymentHelper.PAYTM_PACKAGE,
                                        appName = "Paytm",
                                        upiId = upiInfo.upiId,
                                        payeeName = upiInfo.payeeName
                                    )
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B9F1)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("Paytm", color = Color(0xFF032B66), fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Submit UTR or Open Full Payment Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    enteredUtr = ""
                                    utrError = null
                                    utrFeedbackMsg = null
                                    showUtrDialog = true
                                },
                                modifier = Modifier.weight(1.2f).height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSuccess),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ENTER 12-DIGIT UTR", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = onOpenPayFee,
                                modifier = Modifier.weight(1f).height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = XmaGold, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("OFFICIAL QR", color = XmaTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // If fee is paid, show a positive green verification card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B2416)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaSuccess.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(XmaSuccess.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = XmaSuccess, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MONTH-END FEE CLEARED",
                                    color = XmaSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "September 2026 fee paid (₹${student.monthlyFee.toInt()})",
                                    color = XmaTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Button(
                            onClick = onOpenPayFee,
                            colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, XmaSuccess.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("RECEIPT", color = XmaSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. Announcements from Instructor
        if (announcements.isNotEmpty()) {
            item {
                SectionHeader(title = "ANNOUNCEMENTS")
            }
            items(announcements.take(2)) { ann ->
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
                            Text(
                                text = ann.title,
                                color = XmaTextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(status = ann.targetBatch)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ann.message,
                            color = XmaTextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "By ${ann.authorName} • ${ann.date}",
                            color = XmaGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 6. Latest Training Videos
        item {
            SectionHeader(
                title = "LATEST TRAINING VIDEOS",
                actionText = "VIEW ALL",
                onActionClick = onNavigateToTraining
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(videos.take(8)) { vid ->
                    val isYouTube = vid.videoUrl.contains("youtube.com") || vid.videoUrl.contains("youtu.be")
                    Card(
                        modifier = Modifier
                            .width(235.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onWatchVideo(vid)
                            },
                        colors = CardDefaults.cardColors(containerColor = XmaSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                    ) {
                        Column {
                            // Martial arts video thumbnail
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            if (vid.category.contains("KATA"))
                                                listOf(Color(0xFF1E1035), Color(0xFF0F172A))
                                            else
                                                listOf(Color(0xFF2B0B0B), Color(0xFF0F172A))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val thumbUrl = if (isYouTube) YouTubeHelper.getThumbnailUrl(vid.videoUrl) else null
                                if (thumbUrl != null) {
                                    AsyncImage(
                                        model = thumbUrl,
                                        contentDescription = vid.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Black.copy(alpha = 0.35f),
                                                        Color.Black.copy(alpha = 0.65f)
                                                    )
                                                )
                                            )
                                    )
                                }

                                if (isYouTube) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFF0000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SmartDisplay,
                                            contentDescription = "Watch on YouTube",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = null,
                                        tint = XmaRedPrimary,
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                ) {
                                    StatusBadge(status = vid.category)
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(vid.duration, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = vid.title,
                                    color = XmaTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "By ${vid.instructorName}",
                                    color = XmaTextMuted,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (isYouTube) {
                                            YouTubeHelper.launchVideo(context, vid.videoUrl)
                                        } else {
                                            onWatchVideo(vid)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isYouTube) Color(0xFFFF0000) else XmaRedPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isYouTube) Icons.Default.SmartDisplay else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isYouTube) "WATCH ON YOUTUBE" else "TRAIN NOW",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Official Academy Headquarters & Main Dojo Address Card
        item {
            val clipboardManager = LocalClipboardManager.current
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131722)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = XmaGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OFFICIAL ACADEMY HEADQUARTERS",
                                color = XmaGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(XmaGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, XmaGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("DHANBAD, JHARKHAND", color = XmaGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Mega Sports Complex, Memco more, Chanchani Colony, Dhanbad, Jharkhand 826001",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = XmaTextMuted, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Batch Timings: Mon, Wed, Fri • 5:00 PM – 6:30 PM",
                            color = XmaTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString("Mega Sports Complex, Memco more, Chanchani Colony, Dhanbad, Jharkhand 826001"))
                                Toast.makeText(context, "Academy address copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = XmaTextWhite, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("COPY ADDRESS", color = XmaTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                try {
                                    val geoUri = Uri.parse("geo:0,0?q=Mega+Sports+Complex,+Memco+more,+Chanchani+Colony,+Dhanbad,+Jharkhand+826001")
                                    val intent = Intent(Intent.ACTION_VIEW, geoUri)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Unable to launch map application", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("OPEN IN MAPS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 8. Championship Achievements & Academy Gallery
        item {
            SectionHeader(
                title = "ACADEMY SHOWCASE & TOURNAMENT GALLERY",
                actionText = "DHANBAD",
                onActionClick = {}
            )
        }

        item {
            val galleryItems = listOf(
                TournamentShowcaseItem(
                    title = "Chotanagpur Cup Karate Championship 2025",
                    category = "KUMITE CHAMPIONSHIP",
                    location = "Mega Sports Complex, Dhanbad",
                    description = "State-level Kumite competition featuring elite athletes in red and blue safety gear competing under certified national referees.",
                    badge = "TOURNAMENT HIGHLIGHT",
                    gradient = listOf(Color(0xFF2A0808), Color(0xFF13091B))
                ),
                TournamentShowcaseItem(
                    title = "Jharkhand State Karate Championship",
                    category = "TEAM MEDAL CEREMONY",
                    location = "Dhanbad Sports Pavilion",
                    description = "Team XMA students and Senseis celebrating multiple Gold, Silver and Bronze medals with certificates of excellence.",
                    badge = "MEDAL VICTORY",
                    gradient = listOf(Color(0xFF1B1B08), Color(0xFF0F1A1B))
                ),
                TournamentShowcaseItem(
                    title = "Sensei & Student Bronze Podium Honors",
                    category = "PODIUM FELICITATION",
                    location = "State Federation Arena",
                    description = "Sensei felicitating outstanding female athlete after clinching the Bronze Medal at the State Karate Championship.",
                    badge = "PODIUM FINISH",
                    gradient = listOf(Color(0xFF0E1A29), Color(0xFF160E2A))
                ),
                TournamentShowcaseItem(
                    title = "Mega Sports Complex Dojo Belt Grading",
                    category = "KYU GRADUATION",
                    location = "Main Training Dojo, Dhanbad",
                    description = "Annual Kyu promotion ceremony in traditional dogi with certificates and Wall of Fame honoring student dedication.",
                    badge = "WALL OF FAME",
                    gradient = listOf(Color(0xFF1A0A2A), Color(0xFF091720))
                )
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(galleryItems) { showcase ->
                    Card(
                        modifier = Modifier
                            .width(260.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = XmaSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(Brush.verticalGradient(showcase.gradient)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = XmaGold,
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                            .border(1.dp, XmaGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(showcase.badge, color = XmaGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = showcase.category,
                                    color = XmaRedPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = showcase.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = showcase.description,
                                    color = XmaTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    maxLines = 3
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = XmaGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = showcase.location,
                                        color = XmaGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Direct UTR Submission Dialog
    if (showUtrDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSubmittingUtr) showUtrDialog = false },
            containerColor = Color(0xFF181324),
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "SUBMIT 12-DIGIT UPI UTR",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column {
                    val dialogUpiInfo = UpiPaymentHelper.getInstructorUpiInfo(student.instructorName, student.batchName, student.batchId)
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF231536)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Payee: ${dialogUpiInfo.instructorName}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("UPI ID: ${dialogUpiInfo.upiId}", color = Color(0xFFFFD599), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text("₹${student.monthlyFee.toInt()}", color = XmaGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Text(
                        text = "PhonePe, Google Pay ya Paytm me ₹${student.monthlyFee.toInt()} transfer karne ke baad jo 12-digit UTR/Ref number milta hai, use yahan enter karein:",
                        color = XmaTextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = enteredUtr,
                        onValueChange = {
                            enteredUtr = it
                            utrError = null
                        },
                        placeholder = { Text("e.g. 426819283741", color = XmaTextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Ascii,
                            capitalization = KeyboardCapitalization.Characters
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite,
                            focusedBorderColor = Color(0xFF9D65E8),
                            unfocusedBorderColor = Color(0xFF3B2A59),
                            focusedContainerColor = Color(0xFF0F0B18),
                            unfocusedContainerColor = Color(0xFF0F0B18)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (utrError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(utrError!!, color = XmaRedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    if (utrFeedbackMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(utrFeedbackMsg!!, color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Note: Sensei verification complete hone tak status PENDING rahega.",
                        color = Color(0xFFFFB74D),
                        fontSize = 10.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = enteredUtr.trim()
                        if (!UpiPaymentHelper.isValidUtr(clean)) {
                            utrError = "Kripya valid 12-digit UPI UTR number enter karein."
                        } else {
                            isSubmittingUtr = true
                            onSubmitUpiProof?.invoke(clean, "UPI App") { success, msg ->
                                isSubmittingUtr = false
                                if (success) {
                                    showUtrDialog = false
                                } else {
                                    utrError = msg
                                }
                            } ?: run {
                                isSubmittingUtr = false
                                showUtrDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = XmaSuccess),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isSubmittingUtr
                ) {
                    Text("SUBMIT UTR", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUtrDialog = false },
                    enabled = !isSubmittingUtr
                ) {
                    Text("CANCEL", color = XmaTextMuted, fontSize = 12.sp)
                }
            }
        )
    }
}
