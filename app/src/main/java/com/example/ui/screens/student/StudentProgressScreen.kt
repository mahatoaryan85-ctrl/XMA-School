package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyStepEntity
import com.example.data.model.PerformanceNoteEntity
import com.example.data.model.StudentEntity
import com.example.data.model.VideoProgressEntity
import com.example.ui.components.BeltRankBadge
import com.example.ui.components.CategoryProgressBar
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSuccess
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import java.util.Locale

@Composable
fun StudentProgressScreen(
    student: StudentEntity,
    categoryProgress: Map<String, Int>,
    overallProgress: Int,
    progressList: List<VideoProgressEntity>,
    performanceNotes: List<PerformanceNoteEntity>,
    todaySteps: Int = 0,
    goalSteps: Int = 6000,
    recentSteps: List<DailyStepEntity> = emptyList(),
    onOpenTracker: () -> Unit = {}
) {
    val totalWatched = progressList.size
    val totalCompleted = progressList.count { it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0B0E))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "TRAINING PROGRESS",
                    color = XmaTextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Technique Mastery & Belt Curriculum",
                    color = XmaGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Overall Progress Hero Gauge Card
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
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OVERALL MARTIAL ARTS MASTERY",
                            color = XmaTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$overallProgress%",
                            color = XmaGold,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        BeltRankBadge(rank = student.beltRank)
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { overallProgress / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = XmaGold,
                            trackColor = XmaSurfaceVariant,
                            strokeWidth = 8.dp
                        )
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = XmaGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Metric Counters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Videos Watched",
                    value = "$totalWatched",
                    subtitle = "Techniques Started",
                    icon = Icons.Default.VideoLibrary,
                    accentColor = XmaRedPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Completed",
                    value = "$totalCompleted",
                    subtitle = "Mastered Drills",
                    icon = Icons.Default.CheckCircle,
                    accentColor = XmaSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category-wise Progress Card (KATA, KUMITE, KICKBOXING, SELF DEFENCE, FITNESS)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DISCIPLINE BREAKDOWN",
                        color = XmaTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val categories = listOf(
                        Triple("KATA (Form & Stances)", categoryProgress["KATA"] ?: 70, XmaRedPrimary),
                        Triple("KUMITE (Sparring & Counter)", categoryProgress["KUMITE"] ?: 45, XmaGold),
                        Triple("KICKBOXING (Combos & Power)", categoryProgress["KICKBOXING"] ?: 80, Color(0xFFF97316)),
                        Triple("SELF DEFENCE (Street Escapes)", categoryProgress["SELF DEFENCE"] ?: 60, Color(0xFF38BDF8)),
                        Triple("FITNESS (Core & Conditioning)", categoryProgress["FITNESS"] ?: 90, XmaSuccess)
                    )

                    categories.forEach { (cat, pct, color) ->
                        CategoryProgressBar(
                            category = cat,
                            percentage = pct,
                            color = color
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }

        // Belt Promotion Path
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "BELT RANK PROGRESSION",
                        color = XmaTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val belts = listOf("White Belt", "Yellow Belt", "Orange Belt", "Green Belt", "Blue Belt", "Brown Belt", "Black Belt")
                    val currentIdx = belts.indexOfFirst { student.beltRank.contains(it.split(" ").first(), ignoreCase = true) }.coerceAtLeast(1)

                    belts.forEachIndexed { idx, bName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (idx <= currentIdx) XmaSuccess else XmaSurfaceVariant,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (idx <= currentIdx) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("${idx + 1}", color = XmaTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = bName,
                                color = if (idx == currentIdx) XmaGold else if (idx < currentIdx) XmaTextWhite else XmaTextMuted,
                                fontSize = 13.sp,
                                fontWeight = if (idx == currentIdx) FontWeight.Black else FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            if (idx == currentIdx) {
                                StatusBadge(status = "CURRENT RANK")
                            }
                        }
                    }
                }
            }
        }

        // Daily Step & Footwork Conditioning Card
        item {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(XmaRedPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "FOOTWORK & STEP CONDITIONING",
                                    color = XmaTextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Ashi-Sabaki Stamina & Daily Drills",
                                    color = XmaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onOpenTracker,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF241615)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, XmaRedPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("TRACKER", color = XmaRedPrimary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val stepPct = if (goalSteps > 0) (todaySteps.toFloat() / goalSteps.toFloat()).coerceIn(0f, 1f) else 0f
                    val stepPctInt = (stepPct * 100).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("TODAY'S STEPS", color = XmaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = String.format(Locale.getDefault(), "%,d / %,d", todaySteps, goalSteps),
                                color = XmaTextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "$stepPctInt% OF GOAL",
                            color = if (stepPctInt >= 100) XmaGold else XmaRedPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { stepPct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (stepPctInt >= 100) XmaGold else XmaRedPrimary,
                        trackColor = XmaSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(XmaSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val distanceKm = (todaySteps * 0.762) / 1000.0
                        val calories = todaySteps * 0.042
                        Text("🏃 ${String.format(Locale.getDefault(), "%.2f km", distanceKm)}", color = XmaTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("🔥 ${String.format(Locale.getDefault(), "%.0f kcal", calories)}", color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("🥋 Footwork Ready", color = XmaSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sensei's Performance Feedback Notes
        item {
            SectionHeader(title = "INSTRUCTOR REMARKS & FEEDBACK")
        }

        if (performanceNotes.isEmpty()) {
            item {
                Text(
                    text = "No instructor remarks recorded yet.",
                    color = XmaTextMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            items(performanceNotes) { note ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(XmaGold.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Comment, contentDescription = null, tint = XmaGold, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(note.instructorName, color = XmaTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            StatusBadge(status = note.tag)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${note.note}\"",
                            color = XmaTextWhite,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = note.date,
                            color = XmaTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
