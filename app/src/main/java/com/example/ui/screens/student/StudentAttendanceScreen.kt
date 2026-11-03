package com.example.ui.screens.student

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AttendanceEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.AttendanceSelfieDetailDialog
import com.example.ui.components.AttendanceSelfieThumbnail
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.XmaError
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSuccess
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import com.example.ui.theme.XmaWarning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentAttendanceScreen(
    student: StudentEntity,
    attendanceList: List<AttendanceEntity>,
    todayAttendance: AttendanceEntity?,
    attendancePercentage: Int,
    streak: Int = 0,
    onOpenMarkAttendance: () -> Unit
) {
    var selectedRecord by remember { mutableStateOf<AttendanceEntity?>(null) }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, PRESENT, LATE, ABSENT

    // Sort by timestamp descending
    val sortedList = remember(attendanceList) {
        attendanceList.sortedWith(
            compareByDescending<AttendanceEntity> { it.timestamp }
                .thenByDescending { it.date }
                .thenByDescending { it.time }
        )
    }

    val totalRecords = sortedList.size
    val presentCount = sortedList.count { it.status == "Present" }
    val lateCount = sortedList.count { it.status == "Late" }
    val absentCount = sortedList.count { it.status == "Absent" }

    // Compute active consecutive streak if not passed
    val calculatedStreak = remember(sortedList) {
        if (streak > 0) streak else {
            var s = 0
            for (att in sortedList) {
                if (att.status == "Present") s++ else break
            }
            s
        }
    }

    val filteredList = remember(sortedList, selectedFilter) {
        when (selectedFilter) {
            "PRESENT" -> sortedList.filter { it.status == "Present" }
            "LATE" -> sortedList.filter { it.status == "Late" }
            "ABSENT" -> sortedList.filter { it.status == "Absent" }
            else -> sortedList
        }
    }

    // Map of date (YYYY-MM-DD) to AttendanceEntity for calendar visualization
    val dateMap = remember(sortedList) { sortedList.associateBy { it.date } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0B0E))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Prominent Today Attendance Action Card
        item {
            if (todayAttendance != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, XmaSuccess.copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(XmaSuccess.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = XmaSuccess,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "TODAY MARKED (VERIFIED)",
                                    color = XmaSuccess,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Checked in at ${todayAttendance.time} • ${todayAttendance.verificationScore}% AI Match",
                                    color = XmaTextWhite,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Stored in Room Database: ${todayAttendance.date}",
                                    color = XmaTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        StatusBadge(status = todayAttendance.status)
                    }
                }
            } else {
                Button(
                    onClick = onOpenMarkAttendance,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "MARK TODAY'S SELFIE ATTENDANCE",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "CameraX Live Biometric Verification • Keep your streak alive!",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // 2. Consistency Tracker & Streak Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CONSISTENCY TRACKER",
                                    color = XmaTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                if (calculatedStreak > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(XmaGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                            .border(1.dp, XmaGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "🔥 $calculatedStreak STREAK",
                                            color = XmaGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$attendancePercentage%",
                                color = if (attendancePercentage >= 75) XmaSuccess else XmaError,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (attendancePercentage >= 75) {
                                    "🥋 Belt Exam Target Met (Minimum 75% required)"
                                } else {
                                    "⚠️ Needs minimum 75% for upcoming belt grading"
                                },
                                color = if (attendancePercentage >= 75) XmaSuccess else XmaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Radial progress indicator
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(76.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { attendancePercentage / 100f },
                                modifier = Modifier.fillMaxSize(),
                                color = if (attendancePercentage >= 75) XmaSuccess else XmaRedPrimary,
                                trackColor = XmaSurfaceVariant,
                                strokeWidth = 8.dp
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = if (attendancePercentage >= 75) XmaSuccess else XmaGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "${totalRecords}d",
                                    color = XmaTextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Consistency Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            title = "Present",
                            value = "$presentCount",
                            subtitle = "Verified Selfies",
                            accentColor = XmaSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Late",
                            value = "$lateCount",
                            subtitle = "Warmup Miss",
                            accentColor = XmaWarning,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Absent",
                            value = "$absentCount",
                            subtitle = "Missed",
                            accentColor = XmaError,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Total",
                            value = "$totalRecords",
                            subtitle = "Room Records",
                            accentColor = XmaGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Calendar Grid Consistency Heatmap
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ATTENDANCE HEATMAP (ROOM DB)",
                            color = XmaTextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Aug - Sep 2026",
                            color = XmaTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(XmaSuccess, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Present ($presentCount)", color = XmaTextMuted, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(XmaWarning, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Late ($lateCount)", color = XmaTextMuted, fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(XmaError, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Absent ($absentCount)", color = XmaTextMuted, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calendar Grid (5 rows of 6 days)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (row in 0 until 5) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0 until 6) {
                                    val dayNum = row * 6 + col + 1
                                    val dateKey = if (dayNum <= 10) "2026-09-%02d".format(dayNum) else "2026-08-%02d".format(dayNum)
                                    val rec = dateMap[dateKey]
                                    val (bgColor, textColor) = when (rec?.status) {
                                        "Present" -> Pair(XmaSuccess, Color.White)
                                        "Late" -> Pair(XmaWarning, Color.Black)
                                        "Absent" -> Pair(XmaError, Color.White)
                                        else -> Pair(XmaSurfaceVariant, XmaTextMuted)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(bgColor.copy(alpha = if (rec != null) 0.85f else 0.35f))
                                            .border(
                                                1.dp,
                                                if (rec != null) bgColor else XmaOutline,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                if (rec != null) selectedRecord = rec
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$dayNum",
                                                color = textColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (rec != null) {
                                                Text(
                                                    text = when (rec.status) {
                                                        "Present" -> "P"
                                                        "Late" -> "L"
                                                        else -> "A"
                                                    },
                                                    color = textColor,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black
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
        }

        // 4. Section Header and Filter Chips
        item {
            Column {
                SectionHeader(title = "SELFIE ATTENDANCE LOGS (ROOM DB)")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your attendance consistency with exact timestamps and facial verification audit logs",
                    color = XmaTextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All ($totalRecords)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = XmaRedPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "PRESENT",
                        onClick = { selectedFilter = "PRESENT" },
                        label = { Text("Present ($presentCount)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = XmaSuccess,
                            selectedLabelColor = Color.White
                        )
                    )
                    if (lateCount > 0) {
                        FilterChip(
                            selected = selectedFilter == "LATE",
                            onClick = { selectedFilter = "LATE" },
                            label = { Text("Late ($lateCount)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = XmaWarning,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                    FilterChip(
                        selected = selectedFilter == "ABSENT",
                        onClick = { selectedFilter = "ABSENT" },
                        label = { Text("Absent ($absentCount)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = XmaError,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 5. Empty State when no records match
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = XmaTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No records found for filter '$selectedFilter'",
                            color = XmaTextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take a selfie check-in to store your attendance in the local Room database.",
                            color = XmaTextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 6. Complete List of Selfie Attendance Records from Room
        items(filteredList) { att ->
            AttendanceRecordListItem(
                record = att,
                onClick = { selectedRecord = att }
            )
        }
    }

    // 7. Verification Receipt Modal Dialog
    if (selectedRecord != null) {
        AttendanceReceiptDialog(
            student = student,
            record = selectedRecord!!,
            onDismiss = { selectedRecord = null }
        )
    }
}

@Composable
fun AttendanceRecordListItem(
    record: AttendanceEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = XmaSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (record.status == "Present") XmaOutline else if (record.status == "Late") XmaWarning.copy(alpha = 0.5f) else XmaError.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AttendanceSelfieThumbnail(
                        record = record,
                        student = null,
                        size = 42.dp,
                        onClick = onClick
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = formatFriendlyDate(record.date),
                            color = XmaTextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Time: ${record.time}",
                                color = XmaTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = " • ",
                                color = XmaTextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "ts: ${record.timestamp}",
                                color = XmaGold.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                StatusBadge(status = record.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Biometric verification badge & remarks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XmaSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (record.status != "Absent") Icons.Default.Face else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (record.status != "Absent") XmaSuccess else XmaTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (record.status != "Absent") {
                            "Selfie Verified • ${record.verificationScore}% AI Match"
                        } else {
                            "Absence Recorded"
                        },
                        color = if (record.status != "Absent") XmaSuccess else XmaTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "View Receipt >",
                    color = XmaGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AttendanceReceiptDialog(
    student: StudentEntity,
    record: AttendanceEntity,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, XmaGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(XmaGold.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = XmaGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ATTENDANCE RECEIPT",
                                color = XmaTextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "XMA SCHOOL BIOMETRIC AUDIT",
                                color = XmaGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = XmaTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Stamp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (record.status == "Present") XmaSuccess.copy(alpha = 0.12f) else XmaError.copy(alpha = 0.12f),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (record.status == "Present") XmaSuccess.copy(alpha = 0.4f) else XmaError.copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (record.status == "Present") Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (record.status == "Present") XmaSuccess else XmaGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "STATUS: ${record.status.uppercase()}",
                            color = if (record.status == "Present") XmaSuccess else XmaGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (record.isVerified) "CameraX Biometric Identity Verified" else "Attendance Not Verified",
                            color = XmaTextWhite,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Captured Biometric Selfie Row
                var showFullscreenSelfie by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(XmaSurfaceVariant, RoundedCornerShape(12.dp))
                        .clickable { showFullscreenSelfie = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AttendanceSelfieThumbnail(
                        record = record,
                        student = student,
                        size = 54.dp,
                        onClick = { showFullscreenSelfie = true }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Captured Attendance Selfie", color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (record.selfieUri.isNotEmpty()) "Stored proof • Tap to inspect" else "CameraX Biometric Proof",
                            color = XmaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (showFullscreenSelfie) {
                    AttendanceSelfieDetailDialog(
                        record = record,
                        student = student,
                        onDismiss = { showFullscreenSelfie = false }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detailed session fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(XmaSurfaceVariant, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReceiptRow(label = "Student Name", value = student.fullName)
                    ReceiptRow(label = "Student ID", value = student.studentId)
                    ReceiptRow(label = "Belt Rank", value = student.beltRank)
                    ReceiptRow(label = "Batch", value = student.batchName)
                    ReceiptRow(label = "Date", value = record.date)
                    ReceiptRow(label = "Time", value = record.time)
                    ReceiptRow(
                        label = "Timestamp (Epoch)",
                        value = "${record.timestamp} ms",
                        isMonospace = true
                    )
                    ReceiptRow(
                        label = "Face AI Confidence",
                        value = "${record.verificationScore}% Match",
                        valueColor = if (record.verificationScore >= 90) XmaSuccess else XmaGold
                    )
                    ReceiptRow(
                        label = "Selfie Reference",
                        value = if (record.selfieUri.isNotEmpty()) record.selfieUri else "None",
                        isMonospace = true
                    )
                    ReceiptRow(label = "Remarks", value = record.remarks)
                    ReceiptRow(
                        label = "Local Storage",
                        value = "Room SQLite (attendance table • id #${record.id})"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CLOSE RECEIPT", color = XmaTextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = XmaTextWhite,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = XmaTextMuted, fontSize = 11.sp)
        Text(
            text = value,
            color = valueColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default
        )
    }
}

private fun formatFriendlyDate(dateStr: String): String {
    return try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
        if (parsed != null) {
            SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(parsed)
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}
