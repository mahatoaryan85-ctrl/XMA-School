package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object YouTubeHelper {

    fun extractVideoId(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val trimmed = url.trim()

        if (trimmed.contains("youtu.be/")) {
            val after = trimmed.substringAfter("youtu.be/").substringBefore("?").substringBefore("&").substringBefore("/")
            if (after.isNotBlank()) return after
        }

        if (trimmed.contains("/shorts/")) {
            val after = trimmed.substringAfter("/shorts/").substringBefore("?").substringBefore("&").substringBefore("/")
            if (after.isNotBlank()) return after
        }

        if (trimmed.contains("/embed/")) {
            val after = trimmed.substringAfter("/embed/").substringBefore("?").substringBefore("&").substringBefore("/")
            if (after.isNotBlank()) return after
        }

        if (trimmed.contains("v=")) {
            val after = trimmed.substringAfter("v=").substringBefore("&").substringBefore("#")
            if (after.isNotBlank()) return after
        }

        val regex = "^(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=|shorts\\/))([\\w-]{11})(?:\\S+)?$".toRegex()
        val match = regex.find(trimmed)
        if (match != null && match.groupValues.size > 1) {
            return match.groupValues[1]
        }

        return null
    }

    fun getThumbnailUrl(url: String?): String? {
        val videoId = extractVideoId(url)
        return if (videoId != null) {
            "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        } else null
    }

    fun getEmbedHtml(videoId: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body, html { width: 100%; height: 100%; background-color: #000000; overflow: hidden; }
                    .iframe-container { position: relative; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
                    iframe { width: 100%; height: 100%; border: none; }
                </style>
            </head>
            <body>
                <div class="iframe-container">
                    <iframe 
                        src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&rel=0&modestbranding=1" 
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                        allowfullscreen>
                    </iframe>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun launchVideo(context: Context, url: String) {
        val trimmed = url.trim()
        if (trimmed.isBlank()) {
            Toast.makeText(context, "Video link is not available", Toast.LENGTH_SHORT).show()
            return
        }
        val videoId = extractVideoId(trimmed)

        // 1. Try launching in YouTube App via vnd.youtube
        if (videoId != null) {
            try {
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(appIntent)
                return
            } catch (e: Exception) {
                // Fallback to web link
            }
        }

        // 2. Direct browser intent
        try {
            val targetUrl = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                trimmed
            } else if (videoId != null) {
                "https://www.youtube.com/watch?v=$videoId"
            } else {
                "https://$trimmed"
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open video link: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
