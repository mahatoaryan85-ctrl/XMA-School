package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StudentEntity
import com.example.ui.theme.XmaGold
import com.example.ui.theme.XmaOutline
import com.example.ui.theme.XmaRedPrimary
import com.example.ui.theme.XmaSuccess
import com.example.ui.theme.XmaSurface
import com.example.ui.theme.XmaSurfaceVariant
import com.example.ui.theme.XmaTextMuted
import com.example.ui.theme.XmaTextWhite

@Composable
fun FeePaymentModal(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (method: String, transactionId: String?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("PhonePe UPI QR") }
    var isSuccess by remember { mutableStateOf(false) }
    var confirmedTxId by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val upiInfo = remember(student.instructorName, student.batchName, student.batchId) {
        UpiPaymentHelper.getInstructorUpiInfo(student.instructorName, student.batchName, student.batchId)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, XmaOutline, RoundedCornerShape(22.dp)),
            color = XmaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PhonePeLogoBadge(size = 30)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MONTHLY TRAINING FEE",
                                color = XmaTextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "XMA SECURE PAYMENT GATEWAY",
                                color = PhonePePurpleLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = XmaTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isSuccess) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .background(XmaSuccess.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, XmaSuccess, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = XmaSuccess,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Payment Completed!",
                            color = XmaTextWhite,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${student.monthlyFee.toInt()} successfully paid via $selectedMethod",
                            color = XmaSuccess,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (confirmedTxId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ref/UTR: $confirmedTxId",
                                color = PhonePePurpleLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A digital receipt has been saved in your profile history.",
                            color = XmaTextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("DONE", fontWeight = FontWeight.Black)
                        }
                    }
                    return@Column
                }

                // Summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XmaSurfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XmaOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Student", color = XmaTextMuted, fontSize = 12.sp)
                            Text(
                                "${student.fullName} (${student.studentId})",
                                color = XmaTextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Enrolled Batch", color = XmaTextMuted, fontSize = 12.sp)
                            Text(
                                student.batchName,
                                color = XmaTextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Assigned Sensei", color = XmaTextMuted, fontSize = 12.sp)
                            Text(
                                "${upiInfo.instructorName} (${upiInfo.branchName})",
                                color = XmaGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Official UPI ID", color = XmaTextMuted, fontSize = 12.sp)
                            Text(
                                upiInfo.upiId,
                                color = PhonePePurpleLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Billing Cycle", color = XmaTextMuted, fontSize = 12.sp)
                            Text(
                                "Monthly Training Dues",
                                color = XmaTextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(XmaOutline)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total Payable",
                                color = XmaTextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "₹${student.monthlyFee.toInt()}",
                                color = XmaGold,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SELECT PAYMENT MODE",
                    color = XmaTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val methods = listOf(
                    Triple("PhonePe UPI QR", "Official PhonePe QR • GPay, Paytm, BHIM", Icons.Default.QrCode),
                    Triple("Credit / Debit Card", "Visa, Mastercard, RuPay cards", Icons.Default.CreditCard),
                    Triple("Net Banking", "SBI, HDFC, ICICI, Axis & more", Icons.Default.AccountBalance),
                    Triple("Cash at Academy", "Pay directly to instructor at reception", Icons.Default.Payments)
                )

                methods.forEach { (name, desc, icon) ->
                    val isSel = selectedMethod == name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSel) {
                                    if (name.startsWith("PhonePe")) PhonePePurple.copy(alpha = 0.25f)
                                    else XmaRedPrimary.copy(alpha = 0.15f)
                                } else XmaSurfaceVariant
                            )
                            .border(
                                1.dp,
                                if (isSel) {
                                    if (name.startsWith("PhonePe")) PhonePePurpleLight
                                    else XmaRedPrimary
                                } else XmaOutline,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedMethod = name }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSel) {
                                if (name.startsWith("PhonePe")) PhonePePurpleLight else XmaRedPrimary
                            } else XmaTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    name,
                                    color = XmaTextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (name.startsWith("PhonePe")) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PhonePePurple)
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "OFFICIAL QR",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                            Text(desc, color = XmaTextMuted, fontSize = 10.sp)
                        }
                        if (isSel) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (name.startsWith("PhonePe")) PhonePePurpleLight else XmaRedPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // If PhonePe UPI QR is selected, display the official PhonePe QR Card
                if (selectedMethod == "PhonePe UPI QR") {
                    PhonePeQrCard(
                        amount = student.monthlyFee,
                        payeeName = upiInfo.payeeName,
                        upiId = upiInfo.upiId,
                        note = "XMA Fee - ${student.studentId}",
                        showConfirmFields = true,
                        onPaymentConfirmed = { txnId ->
                            confirmedTxId = txnId
                            onPaymentConfirmed(selectedMethod, txnId)
                            isSuccess = true
                        }
                    )
                } else {
                    // Secondary payment button for Card / Net Banking / Cash
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = XmaSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "256-Bit SSL Encrypted Martial Arts Payment Gateway",
                            color = XmaTextMuted,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val txn = "TXN-MAN-${System.currentTimeMillis().toString().takeLast(8)}"
                            confirmedTxId = txn
                            onPaymentConfirmed(selectedMethod, txn)
                            isSuccess = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = XmaRedPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (selectedMethod.contains("Cash")) "CONFIRM CASH PAYMENT AT DESK"
                            else "PROCEED TO PAY ₹${student.monthlyFee.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
