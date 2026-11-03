package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Refresh
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.util.YouTubeHelper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TrainingVideoEntity
import com.example.data.model.VideoProgressEntity
import com.example.ui.theme.XmaError
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSuccess
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerModal(
    video: TrainingVideoEntity,
    currentProgress: VideoProgressEntity?,
    onDismiss: () -> Unit,
    onProgressUpdate: (percentage: Int, isCompleted: Boolean, lastPos: Int) -> Unit
) {
    val context = LocalContext.current
    val isYouTube = video.videoUrl.contains("youtube.com") || video.videoUrl.contains("youtu.be")
    val videoId = remember(video.videoUrl) { YouTubeHelper.extractVideoId(video.videoUrl) }
    val thumbnailUrl = remember(video.videoUrl) { YouTubeHelper.getThumbnailUrl(video.videoUrl) }
    var playerViewMode by remember { mutableStateOf(if (isYouTube && videoId != null) "IN_APP" else "SIMULATED") }
    var webViewKey by remember { mutableIntStateOf(0) }

    val totalSeconds = video.durationSeconds
    var currentSeconds by remember {
        mutableIntStateOf(currentProgress?.lastPositionSeconds ?: 0)
    }
    var isPlaying by remember { mutableStateOf(true) }
    var isCompleted by remember {
        mutableStateOf(currentProgress?.isCompleted == true)
    }

    // Playback loop
    LaunchedEffect(isPlaying, currentSeconds) {
        if (isPlaying && currentSeconds < totalSeconds) {
            delay(1000)
            currentSeconds = (currentSeconds + 1).coerceAtMost(totalSeconds)
            val percent = ((currentSeconds.toFloat() / totalSeconds) * 100).toInt()
            val completedNow = percent >= 95
            if (completedNow && !isCompleted) {
                isCompleted = true
            }
            onProgressUpdate(percent, isCompleted, currentSeconds)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(20.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = video.category)
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(status = video.difficulty)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = video.title,
                            color = XmaTextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = XmaTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Video Viewport (Embedded YouTube or Animated Poster)
                if (isYouTube && videoId != null && playerViewMode == "IN_APP") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black)
                            .border(1.dp, XmaOutline, RoundedCornerShape(14.dp))
                    ) {
                        key(webViewKey) {
                            AndroidView(
                                factory = { ctx ->
                                    WebView(ctx).apply {
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                        settings.javaScriptEnabled = true
                                        settings.domStorageEnabled = true
                                        settings.mediaPlaybackRequiresUserGesture = false
                                        settings.loadWithOverviewMode = true
                                        settings.useWideViewPort = true
                                        webChromeClient = WebChromeClient()
                                        webViewClient = WebViewClient()
                                        setBackgroundColor(android.graphics.Color.BLACK)
                                        loadDataWithBaseURL(
                                            "https://www.youtube.com",
                                            YouTubeHelper.getEmbedHtml(videoId),
                                            "text/html",
                                            "UTF-8",
                                            null
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Completed overlay badge
                        if (isCompleted) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(XmaSuccess.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("COMPLETED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                } else {
                    // Poster / Thumbnail / Simulated Player View
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF020617))
                                )
                            )
                            .border(1.dp, XmaOutline, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (thumbnailUrl != null) {
                            AsyncImage(
                                model = thumbnailUrl,
                                contentDescription = video.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Gradient scrim
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Black.copy(alpha = 0.4f),
                                                Color.Black.copy(alpha = 0.75f)
                                            )
                                        )
                                    )
                            )
                        }

                        // Video animation representation / Play Center
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (isYouTube && videoId != null) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF0000))
                                        .clickable { playerViewMode = "IN_APP" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Video",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.SportsMartialArts,
                                    contentDescription = null,
                                    tint = if (isPlaying) XmaRedPrimary else XmaTextMuted,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isPlaying) "Playing: ${video.title}" else "Paused",
                                color = XmaTextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Text(
                                text = "Instructor: ${video.instructorName}",
                                color = XmaTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Completed overlay badge
                        if (isCompleted) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .background(XmaSuccess.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("COMPLETED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar: YouTube Launch + Mode Switching
                if (isYouTube) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                YouTubeHelper.launchVideo(context, video.videoUrl)
                                onProgressUpdate(100, true, totalSeconds)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.SmartDisplay,
                                    contentDescription = "Watch on YouTube",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OPEN YOUTUBE APP",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.3.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        if (videoId != null) {
                            Button(
                                onClick = {
                                    if (playerViewMode == "IN_APP") {
                                        webViewKey++
                                    } else {
                                        playerViewMode = "IN_APP"
                                    }
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = XmaSurfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = XmaGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (playerViewMode == "IN_APP") "RELOAD" else "IN-APP",
                                        color = XmaTextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Time slider and timestamps
                val progressFloat = if (totalSeconds > 0) currentSeconds.toFloat() / totalSeconds.toFloat() else 0f
                Slider(
                    value = progressFloat,
                    onValueChange = { newProg ->
                        val targetSec = (newProg * totalSeconds).toInt()
                        currentSeconds = targetSec
                        val percent = (newProg * 100).toInt()
                        val completedNow = percent >= 95
                        if (completedNow && !isCompleted) isCompleted = true
                        onProgressUpdate(percent, isCompleted, targetSec)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = XmaRedPrimary,
                        activeTrackColor = XmaRedPrimary,
                        inactiveTrackColor = XmaSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val curMin = currentSeconds / 60
                    val curSec = currentSeconds % 60
                    val totMin = totalSeconds / 60
                    val totSec = totalSeconds % 60
                    Text(
                        text = "%02d:%02d".format(curMin, curSec),
                        color = XmaRedPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%d%% Watched".format(((currentSeconds.toFloat() / totalSeconds) * 100).toInt()),
                        color = XmaTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "%02d:%02d".format(totMin, totSec),
                        color = XmaTextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentSeconds = (currentSeconds - 10).coerceAtLeast(0)
                        val p = ((currentSeconds.toFloat() / totalSeconds) * 100).toInt()
                        onProgressUpdate(p, isCompleted, currentSeconds)
                    }) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Rewind 10s", tint = XmaTextWhite)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(XmaRedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { isPlaying = !isPlaying }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(onClick = {
                        currentSeconds = (currentSeconds + 10).coerceAtMost(totalSeconds)
                        val p = ((currentSeconds.toFloat() / totalSeconds) * 100).toInt()
                        val comp = p >= 95
                        if (comp) isCompleted = true
                        onProgressUpdate(p, isCompleted, currentSeconds)
                    }) {
                        Icon(Icons.Default.FastForward, contentDescription = "Forward 10s", tint = XmaTextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "TECHNIQUE BREAKDOWN",
                            color = XmaTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = video.description,
                            color = XmaTextWhite,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mark as completed button
                Button(
                    onClick = {
                        isCompleted = true
                        currentSeconds = totalSeconds
                        onProgressUpdate(100, true, totalSeconds)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) XmaSurfaceVariant else XmaSuccess
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, XmaSuccess) else null
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isCompleted) XmaSuccess else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCompleted) "TECHNIQUE COMPLETED (100%)" else "MARK AS COMPLETED",
                        color = if (isCompleted) XmaSuccess else Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
