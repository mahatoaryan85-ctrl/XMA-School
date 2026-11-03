package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSuccess
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

// PhonePe Official Palette
val PhonePePurple = Color(0xFF5F259F)
val PhonePePurpleLight = Color(0xFFA855F7)
val PhonePeDarkBg = Color(0xFF09090D)
val PhonePeCardBg = Color(0xFF13131A)

@Composable
fun PhonePeLogoBadge(size: Int = 36) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(PhonePePurple),
        contentAlignment = Alignment.Center
    ) {
        // Stylized Devanagari 'पे' for PhonePe
        Text(
            text = "पे",
            color = Color.White,
            fontSize = (size * 0.58f).sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Generates a high-quality, crisp QR code Bitmap using ZXing.
 */
fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

/**
 * Complete PhonePe Payment QR Card replica matching the official merchant standee:
 * - PhonePe Branding
 * - ACCEPTED HERE
 * - Scan & Pay Using PhonePe App
 * - Center QR with PhonePe icon
 * - BIBEK KUMAR MAHATO
 * - Copy UPI ID & Launch UPI app intent
 * - Confirm payment with UTR / Transaction ID
 */
@Composable
fun PhonePeQrCard(
    amount: Double,
    payeeName: String = "BIBEK KUMAR MAHATO",
    upiId: String = "bibekmahato@ybl",
    note: String = "XMA School Fee",
    modifier: Modifier = Modifier,
    showConfirmFields: Boolean = true,
    onPaymentConfirmed: ((transactionId: String) -> Unit)? = null
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    var transactionRef by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val upiPayload = remember(amount, upiId, payeeName, note) {
        val encodedName = Uri.encode(payeeName)
        val encodedNote = Uri.encode(note)
        if (amount > 0) {
            "upi://pay?pa=$upiId&pn=$encodedName&am=${amount.toInt()}&cu=INR&tn=$encodedNote"
        } else {
            "upi://pay?pa=$upiId&pn=$encodedName&cu=INR&tn=$encodedNote"
        }
    }

    val qrBitmap = remember(upiPayload) {
        generateQrBitmap(upiPayload, 600)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = PhonePeDarkBg),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2A1C47))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: PhonePe Logo & Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                PhonePeLogoBadge(size = 38)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "PhonePe",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle: ACCEPTED HERE
            Text(
                text = "ACCEPTED HERE",
                color = PhonePePurpleLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtext: Scan & Pay Using PhonePe App
            Text(
                text = "Scan & Pay Using PhonePe App",
                color = Color(0xFFD1D5DB),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            // QR Code Box with White Background & Center PhonePe Logo
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "PhonePe QR Code for $payeeName",
                        modifier = Modifier.size(210.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Center PhonePe Circle overlay
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PhonePeLogoBadge(size = 36)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payee Name: BIBEK KUMAR MAHATO
            Text(
                text = payeeName,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // UPI ID Chip with Copy Action
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1730))
                    .border(1.dp, Color(0xFF4C2A85), RoundedCornerShape(20.dp))
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("UPI ID", upiId)
                        clipboard.setPrimaryClip(clip)
                        copied = true
                        Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UPI: $upiId",
                    color = PhonePePurpleLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy UPI ID",
                    tint = if (copied) XmaSuccess else PhonePePurpleLight,
                    modifier = Modifier.size(13.dp)
                )
            }

            if (amount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Amount Payable: ",
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "₹${amount.toInt()}",
                        color = XmaGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct UPI App Launcher Buttons (PhonePe, Google Pay, Paytm, etc.)
            Text(
                text = "TAP TO OPEN YOUR UPI APP",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PhonePe Direct
                Button(
                    onClick = {
                        UpiPaymentHelper.launchUpiPlatform(
                            context = context,
                            amount = amount,
                            studentId = note,
                            studentName = payeeName,
                            targetPackage = UpiPaymentHelper.PHONEPE_PACKAGE,
                            appName = "PhonePe",
                            upiId = upiId,
                            payeeName = payeeName
                        )
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("PhonePe", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Google Pay Direct
                Button(
                    onClick = {
                        UpiPaymentHelper.launchUpiPlatform(
                            context = context,
                            amount = amount,
                            studentId = note,
                            studentName = payeeName,
                            targetPackage = UpiPaymentHelper.GPAY_PACKAGE,
                            appName = "Google Pay",
                            upiId = upiId,
                            payeeName = payeeName
                        )
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("GPay", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Paytm Direct
                Button(
                    onClick = {
                        UpiPaymentHelper.launchUpiPlatform(
                            context = context,
                            amount = amount,
                            studentId = note,
                            studentName = payeeName,
                            targetPackage = UpiPaymentHelper.PAYTM_PACKAGE,
                            appName = "Paytm",
                            upiId = upiId,
                            payeeName = payeeName
                        )
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B9F1)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Paytm", color = Color(0xFF042E6F), fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Any UPI Fallback
            Button(
                onClick = {
                    UpiPaymentHelper.launchUpiPlatform(
                        context = context,
                        amount = amount,
                        studentId = note,
                        studentName = payeeName,
                        targetPackage = null,
                        appName = "UPI App",
                        upiId = upiId,
                        payeeName = payeeName
                    )
                },
                modifier = Modifier.fillMaxWidth().height(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF261D3B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("MORE UPI APPS (BHIM / CRED / AMAZON)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            if (showConfirmFields && onPaymentConfirmed != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF261D3B))
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Strict Payment Pendency Warning
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1600)),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "⚠️ MANDATORY PAYMENT PENDENCY NOTICE",
                            color = Color(0xFFFFB74D),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Jab tak aap PhonePe, Google Pay ya Paytm me payment complete karke 12-digit UTR Ref number submit nahi karenge, tab tak fee status PENDING hi rahega. Sensei bank account me check karke verify karenge.",
                            color = Color(0xFFFFE0B2),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ENTER 12-DIGIT UPI UTR / REF NUMBER",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Payment screen se 12-digit UTR copy karke yahan dalein taaki Sensei verify kar sakein",
                    color = XmaTextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = transactionRef,
                    onValueChange = {
                        transactionRef = it
                        errorMsg = null
                    },
                    placeholder = {
                        Text(
                            "e.g. 426819283741 or UPI UTR",
                            color = XmaTextMuted,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = XmaTextWhite,
                        unfocusedTextColor = XmaTextWhite,
                        focusedBorderColor = PhonePePurpleLight,
                        unfocusedBorderColor = Color(0xFF3B2A59),
                        focusedContainerColor = Color(0xFF181324),
                        unfocusedContainerColor = Color(0xFF181324)
                    )
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMsg!!,
                        color = XmaRedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val ref = transactionRef.trim()
                        if (!UpiPaymentHelper.isValidUtr(ref)) {
                            errorMsg = "Kripya valid 12-digit UPI UTR number dalein. Bina UTR payment status PENDING hi rahega."
                        } else {
                            onPaymentConfirmed(ref)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = XmaSuccess),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SUBMIT UTR FOR VERIFICATION",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Copyright
            Text(
                text = "© 2026, All rights reserved, PhonePe Ltd (Formerly known as 'PhonePe Private Ltd')",
                color = Color(0xFF6B7280),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
