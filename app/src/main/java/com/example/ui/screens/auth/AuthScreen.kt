package com.example.ui.screens.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BatchEntity
import com.example.ui.components.AppDownloadQrModal
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
import com.example.viewmodel.UserRole

@Composable
fun AuthScreen(
    batches: List<BatchEntity>,
    onLoginStudent: (studentId: String, pin: String, onError: (String) -> Unit) -> Unit,
    onLoginInstructor: (email: String, pass: String, onError: (String) -> Unit) -> Unit,
    onRegisterStudent: (
        fullName: String,
        dob: String,
        gender: String,
        parentName: String,
        parentMobile: String,
        studentMobile: String,
        address: String,
        batchId: String,
        pin: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) -> Unit
) {
    var studentIdInput by remember { mutableStateOf("") }
    var studentPinInput by remember { mutableStateOf("") }
    var instructorEmailInput by remember { mutableStateOf("") }
    var instructorPassInput by remember { mutableStateOf("") }

    var selectedPortalTab by remember { mutableStateOf(0) } // 0 = Student, 1 = Admin / Sensei
    var passwordVisible by remember { mutableStateOf(false) }
    var instructorPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var adminErrorMessage by remember { mutableStateOf<String?>(null) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showForgotPinDialog by remember { mutableStateOf(false) }
    var showDownloadQrModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08090C))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Martial Arts School Emblem
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(XmaRedPrimary, XmaRedDark, Color.Black)
                    )
                )
                .border(2.dp, XmaGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsMartialArts,
                contentDescription = "XMA School Emblem",
                tint = Color.White,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "XMA SCHOOL",
            color = XmaTextWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Text(
            text = "MARTIAL ARTS & SELF DEFENCE ACADEMY",
            color = XmaRedPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Student App Download QR Code Standee Launcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF2A1538), Color(0xFF131D2E))))
                .border(1.2.dp, XmaGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .clickable { showDownloadQrModal = true }
                .padding(horizontal = 14.dp, vertical = 11.dp)
                .testTag("auth_screen_download_qr_button"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(XmaGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "QR Code",
                        tint = XmaGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "📱 Download Student App (QR Code)",
                        color = XmaTextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Scan QR to install directly on phone",
                        color = XmaGold.copy(alpha = 0.85f),
                        fontSize = 10.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = XmaGold,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Prominent Portal Switcher (STUDENT vs ADMIN / SENSEI)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(XmaSurfaceVariant)
                .border(1.dp, XmaOutline, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Student Portal Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedPortalTab == 0) XmaRedPrimary else Color.Transparent)
                    .clickable {
                        selectedPortalTab = 0
                        errorMessage = null
                    }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (selectedPortalTab == 0) Color.White else XmaTextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STUDENT LOGIN",
                        color = if (selectedPortalTab == 0) Color.White else XmaTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Admin / Sensei Portal Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedPortalTab == 1) XmaGold else Color.Transparent)
                    .clickable {
                        selectedPortalTab = 1
                        adminErrorMessage = null
                    }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = if (selectedPortalTab == 1) Color.Black else XmaTextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ADMIN / SENSEI",
                        color = if (selectedPortalTab == 1) Color.Black else XmaTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedPortalTab == 0) {
            // STUDENT PORTAL LOGIN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "STUDENT PORTAL LOGIN",
                        color = XmaTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter your Student ID or Registered Mobile Number",
                        color = XmaTextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = studentIdInput,
                        onValueChange = {
                            studentIdInput = it
                            errorMessage = null
                        },
                        label = { Text("Student ID or Mobile Number", color = XmaTextMuted) },
                        placeholder = { Text("e.g. Piyush06 or 6206303447", color = XmaTextMuted.copy(alpha = 0.5f), fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = XmaRedPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = XmaSurfaceVariant,
                            unfocusedContainerColor = XmaSurfaceVariant,
                            focusedBorderColor = XmaRedPrimary,
                            unfocusedBorderColor = XmaOutline,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = studentPinInput,
                        onValueChange = {
                            studentPinInput = it
                            errorMessage = null
                        },
                        label = { Text("Password / PIN", color = XmaTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = XmaRedPrimary) },
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = XmaSurfaceVariant,
                            unfocusedContainerColor = XmaSurfaceVariant,
                            focusedBorderColor = XmaRedPrimary,
                            unfocusedBorderColor = XmaOutline,
                            focusedTextColor = XmaTextWhite,
                            unfocusedTextColor = XmaTextWhite
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password / PIN?",
                            color = XmaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { showForgotPinDialog = true }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onLoginStudent(studentIdInput, studentPinInput) { err ->
                                errorMessage = err
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "LOGIN AS STUDENT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New Student?", color = XmaTextMuted, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Register Here",
                            color = XmaRedPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showRegisterDialog = true }
                                .padding(4.dp)
                        )
                    }

                    // Error feedback
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(XmaError.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, XmaError.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = XmaError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMessage!!, color = XmaError, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        } else {
            // ADMIN / SENSEI PORTAL LOGIN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = XmaSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XmaGold.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(XmaGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = XmaGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ADMIN / SENSEI PORTAL",
                                color = XmaTextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Chief Instructors & Branch Management",
                                color = XmaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "1-Tap Quick Select Account:",
                        color = XmaTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick select instructor chips (Selects ID only, no password auto-fill)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (instructorEmailInput == "Raj7007") XmaGold.copy(alpha = 0.2f) else XmaSurfaceVariant)
                                    .border(1.dp, if (instructorEmailInput == "Raj7007") XmaGold else XmaOutline, RoundedCornerShape(8.dp))
                                    .clickable {
                                        instructorEmailInput = "Raj7007"
                                        adminErrorMessage = null
                                    }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🥋 Sensei Raj (Raj7007)", color = if (instructorEmailInput == "Raj7007") XmaGold else XmaTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (instructorEmailInput == "Ashish99") XmaGold.copy(alpha = 0.2f) else XmaSurfaceVariant)
                                    .border(1.dp, if (instructorEmailInput == "Ashish99") XmaGold else XmaOutline, RoundedCornerShape(8.dp))
                                    .clickable {
                                        instructorEmailInput = "Ashish99"
                                        adminErrorMessage = null
                                    }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🥋 Sensei Ashish (Ashish99)", color = if (instructorEmailInput == "Ashish99") XmaGold else XmaTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (instructorEmailInput == "Sambhu01") XmaGold.copy(alpha = 0.2f) else XmaSurfaceVariant)
                                    .border(1.dp, if (instructorEmailInput == "Sambhu01") XmaGold else XmaOutline, RoundedCornerShape(8.dp))
                                    .clickable {
                                        instructorEmailInput = "Sambhu01"
                                        adminErrorMessage = null
                                    }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🥋 Sensei Sambhu (Sambhu01)", color = if (instructorEmailInput == "Sambhu01") XmaGold else XmaTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (instructorEmailInput == "admin@xmaschool.com") XmaGold.copy(alpha = 0.2f) else XmaSurfaceVariant)
                                    .border(1.dp, if (instructorEmailInput == "admin@xmaschool.com") XmaGold else XmaOutline, RoundedCornerShape(8.dp))
                                    .clickable {
                                        instructorEmailInput = "admin@xmaschool.com"
                                        adminErrorMessage = null
                                    }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👑 Master Admin", color = if (instructorEmailInput == "admin@xmaschool.com") XmaGold else XmaTextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = instructorEmailInput,
                        onValueChange = {
                            instructorEmailInput = it
                            adminErrorMessage = null
                        },
                        label = { Text("Instructor ID or Email", color = XmaTextMuted) },
                        placeholder = { Text("e.g. Raj7007, Ashish99, Sambhu01", color = XmaTextMuted.copy(alpha = 0.5f), fontSize = 12.sp) },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = instructorPassInput,
                        onValueChange = {
                            instructorPassInput = it
                            adminErrorMessage = null
                        },
                        label = { Text("Instructor / Admin Password", color = XmaTextMuted) },
                        placeholder = { Text("Enter your secure password", color = XmaTextMuted.copy(alpha = 0.5f), fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = XmaGold) },
                        trailingIcon = {
                            IconButton(onClick = { instructorPasswordVisible = !instructorPasswordVisible }) {
                                Icon(
                                    imageVector = if (instructorPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = XmaTextMuted
                                )
                            }
                        },
                        visualTransformation = if (instructorPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrect = false),
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

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🔒 Manual Entry Required: Please enter your personal instructor password",
                        color = XmaTextMuted,
                        fontSize = 11.sp
                    )

                    if (adminErrorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(XmaError.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, XmaError.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = XmaError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(adminErrorMessage!!, color = XmaError, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onLoginInstructor(instructorEmailInput.trim(), instructorPassInput.trim()) { err ->
                                adminErrorMessage = err
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOGIN AS INSTRUCTOR / SENSEI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }

    // Student Registration Modal
    if (showRegisterDialog) {
        var regName by remember { mutableStateOf("") }
        var regDob by remember { mutableStateOf("2011-08-20") }
        var regGender by remember { mutableStateOf("Male") }
        var regParent by remember { mutableStateOf("") }
        var regParentPhone by remember { mutableStateOf("") }
        var regStudentPhone by remember { mutableStateOf("") }
        var regAddress by remember { mutableStateOf("Dojo Residency, Block 4") }
        var regBatchId by remember { mutableStateOf(batches.firstOrNull()?.id ?: "batch_head") }
        var regPin by remember { mutableStateOf("1234") }
        var regError by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = { showRegisterDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, XmaOutline, RoundedCornerShape(20.dp)),
                color = XmaSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("STUDENT REGISTRATION", color = XmaTextWhite, fontSize = 15.sp, fontWeight = FontWeight.Black)
                            Text("Automatic Student ID Allocation", color = XmaGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { showRegisterDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = XmaTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Full Name", color = XmaTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regDob,
                            onValueChange = { regDob = it },
                            label = { Text("DOB (YYYY-MM-DD)", color = XmaTextMuted) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = regGender,
                            onValueChange = { regGender = it },
                            label = { Text("Gender", color = XmaTextMuted) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = regParent,
                        onValueChange = { regParent = it },
                        label = { Text("Parent / Guardian Name", color = XmaTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regParentPhone,
                            onValueChange = { regParentPhone = it },
                            label = { Text("Parent Mobile", color = XmaTextMuted) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = regStudentPhone,
                            onValueChange = { regStudentPhone = it },
                            label = { Text("Student Mobile", color = XmaTextMuted) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = regAddress,
                        onValueChange = { regAddress = it },
                        label = { Text("Residential Address", color = XmaTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "SELECT TRAINING BRANCH / BATCH",
                        color = XmaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    batches.forEach { batch ->
                        val isSelected = regBatchId == batch.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF2D1832) else XmaSurfaceVariant)
                                .border(1.dp, if (isSelected) XmaGold else XmaOutline, RoundedCornerShape(8.dp))
                                .clickable { regBatchId = batch.id }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${batch.name} (${batch.branchName})",
                                    color = if (isSelected) XmaGold else XmaTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Instructor: ${batch.instructorName} • ${batch.timing}",
                                    color = XmaTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { regBatchId = batch.id },
                                colors = RadioButtonDefaults.colors(selectedColor = XmaGold, unselectedColor = XmaTextMuted)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = regPin,
                        onValueChange = { regPin = it },
                        label = { Text("Set 4-digit PIN", color = XmaTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (regError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(regError!!, color = XmaError, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (regName.isBlank()) {
                                regError = "Please enter student name"
                                return@Button
                            }
                            onRegisterStudent(
                                regName,
                                regDob,
                                regGender,
                                regParent,
                                regParentPhone,
                                regStudentPhone,
                                regAddress,
                                regBatchId,
                                regPin,
                                { allocatedId ->
                                    showRegisterDialog = false
                                },
                                { err ->
                                    regError = err
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("REGISTER & GET STUDENT ID", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }

    // Forgot PIN Dialog
    if (showForgotPinDialog) {
        Dialog(onDismissRequest = { showForgotPinDialog = false }) {
            Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), color = XmaSurface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("FORGOT PASSWORD / PIN", color = XmaTextWhite, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "For security at XMA SCHOOL, PIN resets are authenticated through your academy instructor or Master Vikram Sen.",
                        color = XmaTextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = XmaSurfaceVariant)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Help & Support:", color = XmaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Please contact your Dojo Branch or Head Instructor to verify your identity and update your PIN.", color = XmaTextWhite, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showForgotPinDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("GOT IT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showDownloadQrModal) {
        AppDownloadQrModal(onDismiss = { showDownloadQrModal = false })
    }
}

