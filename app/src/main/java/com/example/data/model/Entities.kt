package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val studentId: String,
    val fullName: String,
    val profilePhotoUri: String = "",
    val dob: String = "2012-05-14",
    val gender: String = "Male",
    val parentName: String = "",
    val parentMobile: String = "",
    val studentMobile: String = "",
    val address: String = "123 Martial Arts Ave, Dojo District",
    val batchId: String = "batch_head",
    val batchName: String = "Head Branch",
    val joiningDate: String = "2025-01-10",
    val instructorName: String = "Sensei Raj Sanchu",
    val monthlyFee: Double = 2500.0,
    val feeDueDate: String = "5th of every month",
    val feeStatus: String = "Pending", // Paid, Pending, Partially Paid, Overdue
    val accountStatus: String = "Active", // Active, Inactive
    val pin: String = "1234",
    val beltRank: String = "Yellow Belt"
)

@Entity(tableName = "instructors")
data class InstructorEntity(
    @PrimaryKey val email: String, // admin@xmaschool.com
    val fullName: String,
    val password: String, // admin123
    val phone: String = "+91 9876543210",
    val role: String = "Head Instructor"
)

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey val id: String,
    val name: String,
    val timing: String,
    val days: String,
    val instructorName: String,
    val branchName: String = name
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm:ss
    val timestamp: Long = System.currentTimeMillis(),
    val selfieUri: String = "",
    val status: String = "Present", // Present, Absent, Late
    val verificationScore: Int = 98,
    val isVerified: Boolean = true,
    val remarks: String = "CameraX Selfie Biometric Verified"
)

@Entity(tableName = "training_videos")
data class TrainingVideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // KATA, KUMITE, KICKBOXING, SELF DEFENCE, FITNESS
    val difficulty: String, // Beginner, Intermediate, Advanced
    val duration: String,
    val durationSeconds: Int = 480,
    val description: String,
    val instructorName: String,
    val videoUrl: String = "video_placeholder_stream",
    val thumbnailUrl: String = "",
    val isPublished: Boolean = true
)

@Entity(tableName = "video_progress")
data class VideoProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val videoId: String,
    val watchPercentage: Int = 0,
    val isCompleted: Boolean = false,
    val lastPositionSeconds: Int = 0,
    val completedDate: String? = null,
    val updatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "fee_payments")
data class FeePaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val month: String, // e.g. "September 2026"
    val amount: Double,
    val paymentDate: String,
    val paymentMethod: String, // Cash, UPI, Bank Transfer, Other
    val transactionId: String,
    val status: String = "Paid",
    val receiptNumber: String
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val targetBatch: String = "All",
    val date: String,
    val authorName: String = "Head Instructor"
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String? = null, // null means all students
    val title: String,
    val message: String,
    val type: String, // FEE_REMINDER, FEE_OVERDUE, ATTENDANCE, VIDEO, ANNOUNCEMENT
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "performance_notes")
data class PerformanceNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val date: String,
    val instructorName: String,
    val note: String,
    val tag: String = "Discipline"
)

@Entity(tableName = "app_update_config")
data class AppUpdateConfigEntity(
    @PrimaryKey val id: String = "primary_config",
    val latestVersion: String = "1.0.0",
    val latestVersionCode: Int = 1,
    val minimumSupportedVersion: String = "1.0.0",
    val minimumSupportedVersionCode: Int = 1,
    val updateType: String = "OPTIONAL", // "OPTIONAL" or "MANDATORY"
    val updateUrl: String = "https://github.com/martial-arts/xma-school/releases/latest/download/xma_school.apk",
    val releaseNotes: String = "• Real-time updates & enhanced security\n• Performance optimizations and bug fixes",
    val updateTitle: String = "New Update Available",
    val updateMessage: String = "A new improved version of XMA SCHOOL is ready for install with new curriculum lessons and attendance features.",
    val publishedDate: String = "2026-09-10",
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_steps",
    primaryKeys = ["studentId", "date"]
)
data class DailyStepEntity(
    val studentId: String,
    val date: String, // YYYY-MM-DD
    val stepCount: Int = 0,
    val goalSteps: Int = 6000,
    val distanceMeters: Double = 0.0,
    val caloriesBurned: Double = 0.0,
    val activeMinutes: Int = 0,
    val motionType: String = "Martial Arts Movement",
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)


