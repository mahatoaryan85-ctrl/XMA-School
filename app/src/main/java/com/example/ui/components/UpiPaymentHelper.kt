package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Helper utility for launching official UPI payment apps (PhonePe, Google Pay, Paytm, etc.)
 * directly with pre-filled instructor/branch specific merchant account parameters.
 */
object UpiPaymentHelper {
    // Specific Authorized UPI IDs per Instructor
    const val SAMBHU_UPI_ID = "8539049019@ptsbi"
    const val SAMBHU_PAYEE_NAME = "Sensei Sambhu"

    const val ASHISH_UPI_ID = "9631284765@ybl"
    const val ASHISH_PAYEE_NAME = "Sensei Ashish"

    const val RAJ_SANCHU_UPI_ID = "bibekmahato@ybl"
    const val RAJ_SANCHU_PAYEE_NAME = "Sensei Raj Sanchu"

    const val ACADEMY_UPI_ID = "bibekmahato@ybl"
    const val ACADEMY_PAYEE_NAME = "BIBEK KUMAR MAHATO"

    const val PHONEPE_PACKAGE = "com.phonepe.app"
    const val GPAY_PACKAGE = "com.google.android.apps.nbu.paisa.user"
    const val PAYTM_PACKAGE = "net.one97.paytm"
    const val BHIM_PACKAGE = "in.org.npci.upiapp"

    data class InstructorUpiInfo(
        val upiId: String,
        val payeeName: String,
        val instructorName: String,
        val branchName: String
    )

    /**
     * Resolves the exact authorized UPI ID and payee name for a student based on their instructor and branch.
     * - Sensei Sambhu (Sindri Branch) -> 8539049019@ptsbi
     * - Sensei Ashish (Apartment Branch) -> 9631284765@ybl
     * - Sensei Raj Sanchu (Head Branch / Main) -> bibekmahato@ybl
     */
    fun getInstructorUpiInfo(
        instructorName: String?,
        batchName: String? = null,
        batchId: String? = null
    ): InstructorUpiInfo {
        val ins = (instructorName ?: "").lowercase()
        val batch = (batchName ?: "").lowercase()
        val bid = (batchId ?: "").lowercase()

        return when {
            ins.contains("sambhu") || batch.contains("sindri") || bid.contains("sindri") -> {
                InstructorUpiInfo(
                    upiId = SAMBHU_UPI_ID,
                    payeeName = SAMBHU_PAYEE_NAME,
                    instructorName = "Sensei Sambhu",
                    branchName = "Sindri Branch"
                )
            }
            ins.contains("ashish") || batch.contains("apartment") || bid.contains("apartment") -> {
                InstructorUpiInfo(
                    upiId = ASHISH_UPI_ID,
                    payeeName = ASHISH_PAYEE_NAME,
                    instructorName = "Sensei Ashish",
                    branchName = "Apartment Branch"
                )
            }
            else -> {
                InstructorUpiInfo(
                    upiId = RAJ_SANCHU_UPI_ID,
                    payeeName = RAJ_SANCHU_PAYEE_NAME,
                    instructorName = "Sensei Raj Sanchu",
                    branchName = "Head Branch"
                )
            }
        }
    }

    fun buildUpiUri(
        upiId: String = ACADEMY_UPI_ID,
        payeeName: String = ACADEMY_PAYEE_NAME,
        amount: Double,
        note: String
    ): Uri {
        val encodedName = Uri.encode(payeeName)
        val encodedNote = Uri.encode(note)
        val uriStr = if (amount > 0) {
            "upi://pay?pa=$upiId&pn=$encodedName&am=${amount.toInt()}&cu=INR&tn=$encodedNote"
        } else {
            "upi://pay?pa=$upiId&pn=$encodedName&cu=INR&tn=$encodedNote"
        }
        return Uri.parse(uriStr)
    }

    /**
     * Launches the targeted UPI application directly, or falls back to system UPI chooser.
     */
    fun launchUpiPlatform(
        context: Context,
        amount: Double,
        studentId: String,
        studentName: String,
        targetPackage: String? = null,
        appName: String = "UPI App",
        upiId: String = ACADEMY_UPI_ID,
        payeeName: String = ACADEMY_PAYEE_NAME
    ) {
        val note = "XMA Fee - $studentId ($studentName)"
        val uri = buildUpiUri(upiId = upiId, payeeName = payeeName, amount = amount, note = note)

        try {
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                if (!targetPackage.isNullOrBlank()) {
                    setPackage(targetPackage)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Target app might not be installed, fallback to system UPI chooser
            try {
                val genericIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(genericIntent, "Pay Fee via $appName (PhonePe, GPay, Paytm)").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            } catch (ex: Exception) {
                Toast.makeText(
                    context,
                    "No UPI application found. Please scan the QR code or pay to UPI ID: $upiId",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Validates an entered UPI UTR / Transaction reference.
     * Bank UTRs are 12 digits, or alpha-numeric transaction IDs.
     */
    fun isValidUtr(raw: String): Boolean {
        val clean = raw.trim().replace(" ", "").replace("-", "")
        return clean.length >= 6
    }
}
