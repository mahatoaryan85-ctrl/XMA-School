package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.data.model.StudentEntity
import com.example.data.remote.gemini.GeminiApiClient
import com.example.data.remote.gemini.GeminiContent
import com.example.data.remote.gemini.GeminiContentRequest
import com.example.data.remote.gemini.GeminiGenerationConfig
import com.example.data.remote.gemini.GeminiPart
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    GEMINI
}

@Composable
fun GeminiAiCoachModal(
    student: StudentEntity,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = MessageSender.GEMINI,
                text = "Oss ${student.fullName}! 🙏 Main hoon aapka XMA AI Sensei (Powered by Google Gemini).\n\nAap mujhse apni **Martial Arts Training, WKF Kata/Kumite, Warmup, Flexibility, Kicking Drills**, aur **Special Diet & Nutrition Plan (Pure Veg / Non-Veg)** ke baare mein kuch bhi pooch sakte hain!"
            )
        )
    }

    val quickQuestions = listOf(
        "🥋 Kata performance kaise improve karein?",
        "🥗 Martial arts student diet plan kya hona chahiye?",
        "🦵 High kicks ke liye leg flexibility drills?",
        "⚡ Kumite reaction speed badhane ke exercises?",
        "🥛 Pre & Post training meals kya lein?",
        "🧘 Kicking balance aur core strength tips?"
    )

    fun sendPrompt(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || isLoading) return

        messages.add(ChatMessage(sender = MessageSender.USER, text = trimmed))
        inputPrompt = ""
        isLoading = true

        scope.launch {
            // Scroll to bottom
            listState.animateScrollToItem(messages.size - 1)

            val reply = queryGeminiAi(
                student = student,
                userPrompt = trimmed,
                history = messages.toList()
            )

            messages.add(ChatMessage(sender = MessageSender.GEMINI, text = reply))
            isLoading = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, XmaGold.copy(alpha = 0.6f), RoundedCornerShape(22.dp)),
            color = Color(0xFF0D0F14)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                // Top Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E1428), Color(0xFF181220), Color(0xFF0F172A))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
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
                                    contentDescription = "Gemini AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ASK GEMINI AI",
                                        color = XmaTextWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(XmaGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, XmaGold, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "TRAINING & DIET",
                                            color = XmaGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "Sensei Raj Sanchu Dojo • ${student.beltRank}",
                                    color = XmaTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = XmaTextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Quick Topic Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF131620))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickQuestions) { question ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(XmaSurfaceVariant)
                                .border(1.dp, XmaOutline, RoundedCornerShape(16.dp))
                                .clickable { sendPrompt(question) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = question,
                                color = XmaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Messages Chat List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        MessageBubble(msg)
                    }

                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF8B5CF6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(XmaSurfaceVariant)
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = XmaGold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Gemini coach is thinking...",
                                            color = XmaTextMuted,
                                            fontSize = 12.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Bar at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(XmaSurface)
                        .border(1.dp, XmaOutline)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputPrompt,
                            onValueChange = { inputPrompt = it },
                            placeholder = {
                                Text(
                                    text = "Ask training tips, kata, kumite, or diet...",
                                    color = XmaTextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF0D0F14),
                                unfocusedContainerColor = Color(0xFF0D0F14),
                                focusedBorderColor = XmaGold,
                                unfocusedBorderColor = XmaOutline,
                                focusedTextColor = XmaTextWhite,
                                unfocusedTextColor = XmaTextWhite
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { sendPrompt(inputPrompt) },
                            enabled = inputPrompt.isNotBlank() && !isLoading,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputPrompt.isNotBlank() && !isLoading) XmaRedPrimary else XmaSurfaceVariant
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (inputPrompt.isNotBlank() && !isLoading) Color.White else XmaTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(if (isUser) 0.90f else 0.95f)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
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
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser)
                            Brush.horizontalGradient(
                                listOf(Color(0xFFB91C1C), Color(0xFFDC2626))
                            )
                        else
                            Brush.linearGradient(
                                listOf(Color(0xFF1B1D28), Color(0xFF151822))
                            )
                    )
                    .border(
                        1.dp,
                        if (isUser) Color.Transparent else Color(0xFF2C3246),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = message.text,
                        color = if (isUser) Color.White else Color(0xFFEDEDED),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(XmaSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = XmaGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private suspend fun queryGeminiAi(
    student: StudentEntity,
    userPrompt: String,
    history: List<ChatMessage>
): String = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
        // Fallback intelligent offline martial arts response if API key is not yet set in secrets
        return@withContext getOfflineMartialArtsCoachResponse(student, userPrompt)
    }

    try {
        val systemPrompt = """
            You are the Official AI Martial Arts Coach & Nutritionist at XMA SCHOOL OF MARTIAL ARTS (Headquarters: Mega Sports Complex, Memco More, Dhanbad under Sensei Raj Sanchu).
            
            Student Profile:
            - Name: ${student.fullName}
            - Belt Rank: ${student.beltRank}
            - Batch: ${student.batchName}
            
            Your expertise:
            1. Martial Arts Training: WKF Shotokan Karate Kata (Heian/Pinan series, Bassai Dai, Kanku Dai, Empi, Jion, etc.), Kumite footwork & distancing (kizami-tsuki, gyaku-tsuki, mawashi-geri, uramawashi), Kickboxing combos, Self-defense tactics.
            2. Diet & Nutrition: Practical Indian diet (veg & non-veg: eggs, paneer, dal, sprouts, oats, seasonal fruits, hydrating electrolytes) designed for athletic endurance, muscle recovery, lean power, and agility.
            3. Tone: Encouraging, respectful martial arts discipline ("Oss!"), clear bullet points, safe execution advice, structured tips.
            4. Keep answers concise, highly practical, and actionable for young students and athletes. Support Hindi/Hinglish if the student asks in Hindi/Hinglish.
        """.trimIndent()

        val contents = history.takeLast(6).map { msg ->
            GeminiContent(
                role = if (msg.sender == MessageSender.USER) "user" else "model",
                parts = listOf(GeminiPart(text = msg.text))
            )
        } + listOf(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = userPrompt))
            )
        )

        val request = GeminiContentRequest(
            contents = contents,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f,
                topP = 0.95f,
                maxOutputTokens = 1200
            )
        )

        val response = GeminiApiClient.api.generateContent(apiKey = apiKey, request = request)
        val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        if (!reply.isNullOrBlank()) {
            reply
        } else {
            getOfflineMartialArtsCoachResponse(student, userPrompt)
        }
    } catch (e: Exception) {
        // Fallback gracefully so students always get great guidance
        getOfflineMartialArtsCoachResponse(student, userPrompt)
    }
}

/**
 * High-quality domain-specific fallback coach response if network or API key is unavailable.
 */
private fun getOfflineMartialArtsCoachResponse(student: StudentEntity, prompt: String): String {
    val p = prompt.lowercase()
    return when {
        p.contains("diet") || p.contains("food") || p.contains("khana") || p.contains("nutrition") || p.contains("meal") -> {
            """
            Oss ${student.fullName}! 🥋 Yahan hai aapka Martial Arts Athlete Diet & Nutrition Guide:
            
            ⚡ Pre-Training (1-2 ghante pehle):
            • 1 Banana + 4-5 Bheege huye Badam (Almonds) ya Khajoor (Dates)
            • 1 glass Nimbu Paani ya Coconut Water (hydration ke liye)
            • Light Oats ya Brown Bread with Peanut Butter
            
            💪 Post-Training (Recovery - 30 min ke andar):
            • Boiled Eggs (2-3) ya Sprouted Moong / Paneer (100g)
            • Sattu drink ya Low-fat milk with pinch of turmeric
            • High-protein Dal + Roti + Fresh Salad & Curd
            
            💧 Hydration Rule:
            • Training ke doran small sips of water lein, ek saath zyada paani na piyein.
            • Fried junk food, extra sugar aur cold drinks se bachein.
            """.trimIndent()
        }
        p.contains("kata") -> {
            """
            Oss! 🥋 WKF Kata Mastery Tips for ${student.beltRank}:
            
            1. **Zanshin & Focus**: Har technique shuru hone se pehle aur khatam hone ke baad 1 second complete stillness maintain karein.
            2. **Kime (Impact Power)**: Speed ke end point par muscle contraction (tensing) aur snap laayein.
            3. **Stance (Dachi) Depth**:
               • Zenkutsu-dachi (Front stance): Front knee bent right above toes, back leg straight, 60-40 weight ratio.
               • Kokutsu-dachi (Back stance): 70% weight on back leg.
            4. **Breathing (Ibuki)**: Technique strike karte waqt sharp exhalation (Kiai) karein.
            
            *Tip: Daily dojo mirror ke saamne slow-motion me practice karein, phir full power speed laayein.*
            """.trimIndent()
        }
        p.contains("kumite") || p.contains("fight") || p.contains("sparring") -> {
            """
            Oss! 🥊 WKF Kumite Speed & Strategy Guide:
            
            1. **Footwork (Bouncing & Distance)**:
               • Apne toes par light rahein, heel floor par zyada press na karein.
               • "Ma-ai" (Safe fighting distance) ko judge karna seekhein.
            2. **Lead Hand Kizami-Tsuki**:
               • Opponent ke aage aate hi direct lead jab hit karein. Yeh sabse fast score karta hai.
            3. **Gyaku-Tsuki Counter**:
               • Opponent ke attack ko slip/parry karke hip rotation ke saath instant reverse punch maarein.
            4. **Recovery & Guard**:
               • Attack ke baad haath giraye bina instantly defensive stance me wapas aayein.
            """.trimIndent()
        }
        p.contains("flexibility") || p.contains("kick") || p.contains("leg") || p.contains("stretch") -> {
            """
            Oss! 🦵 High Kicks & Flexibility Routine:
            
            1. **Dynamic Stretches (Before Training)**:
               • Leg Swings (Front, Back & Side) - 15 reps each leg.
               • Hip rotations and deep squats.
            2. **Static Stretches (After Training)**:
               • Butterfly Stretch (Groin opener) - Hold for 45 seconds.
               • Side Split & Front Split progression - Keep spine straight.
               • Pike stretch (Touching toes with straight knees).
            3. **Chamber Drill for Kicks**:
               • Deewar (Wall) ka sahara lekar knee ko chest tak uthayein aur 10 seconds hold karein (Mawashi-Geri & Yoko-Geri chamber).
            """.trimIndent()
        }
        else -> {
            """
            Oss ${student.fullName}! 🙏
            
            Main aapka XMA AI Sensei hoon. Aap mujhse pooch sakte hain:
            • **Kata Technique** (Stance, speed, kime)
            • **Kumite Drills** (Footwork, timing, combinations)
            • **Daily Diet & Nutrition** (Vegetarian & Non-Veg protein plans)
            • **Stretching & Flexibility** for high kicks
            
            Neeche diye gaye buttons par tap karein ya apna sawal type karein!
            """.trimIndent()
        }
    }
}
