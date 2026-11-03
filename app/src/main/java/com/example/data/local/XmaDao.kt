package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AppNotificationEntity
import com.example.data.model.AppUpdateConfigEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.BatchEntity
import com.example.data.model.DailyStepEntity
import com.example.data.model.FeePaymentEntity
import com.example.data.model.InstructorEntity
import com.example.data.model.PerformanceNoteEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TrainingVideoEntity
import com.example.data.model.VideoProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface XmaDao {
    // --- Students ---
    @Query("SELECT * FROM students ORDER BY studentId ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE LOWER(studentId) = LOWER(:studentId) OR studentId = :studentId LIMIT 1")
    fun getStudentById(studentId: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE LOWER(studentId) = LOWER(:studentId) OR studentId = :studentId LIMIT 1")
    suspend fun getStudentByIdSync(studentId: String): StudentEntity?

    @Query("""
        SELECT * FROM students 
        WHERE LOWER(studentId) = LOWER(:input) 
           OR studentId = :input 
           OR studentMobile = :input 
           OR parentMobile = :input 
           OR REPLACE(studentMobile, ' ', '') = :input 
           OR REPLACE(parentMobile, ' ', '') = :input
           OR REPLACE(studentMobile, '+91', '') = :input
           OR REPLACE(parentMobile, '+91', '') = :input
           OR LOWER(fullName) = LOWER(:input)
        LIMIT 1
    """)
    suspend fun findStudentForAuth(input: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET accountStatus = :status WHERE studentId = :studentId")
    suspend fun updateStudentStatus(studentId: String, status: String)

    @Query("UPDATE students SET pin = :pin WHERE studentId = :studentId")
    suspend fun updateStudentPin(studentId: String, pin: String)

    @Query("UPDATE students SET profilePhotoUri = :photoUri WHERE studentId = :studentId")
    suspend fun updateStudentPhoto(studentId: String, photoUri: String)

    @Query("UPDATE students SET feeStatus = :feeStatus WHERE studentId = :studentId")
    suspend fun updateStudentFeeStatus(studentId: String, feeStatus: String)

    @Query("DELETE FROM students WHERE studentId = :studentId")
    suspend fun deleteStudent(studentId: String)

    // --- Instructors ---
    @Query("SELECT * FROM instructors WHERE LOWER(email) = LOWER(:email) OR email = :email LIMIT 1")
    suspend fun getInstructorByEmail(email: String): InstructorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructor(instructor: InstructorEntity)

    // --- Batches ---
    @Query("SELECT * FROM batches ORDER BY name ASC")
    fun getAllBatches(): Flow<List<BatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity)

    @Query("DELETE FROM batches WHERE id = :id")
    suspend fun deleteBatch(id: String)

    @Query("SELECT * FROM batches WHERE id = :id LIMIT 1")
    suspend fun getBatchById(id: String): BatchEntity?

    // --- Attendance ---
    @Query("SELECT * FROM attendance ORDER BY timestamp DESC, date DESC, time DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY timestamp DESC, date DESC, time DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date = :date LIMIT 1")
    suspend fun getTodayAttendanceForStudent(studentId: String, date: String): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY timestamp DESC, time DESC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId")
    suspend fun getAttendanceCountForStudent(studentId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    // --- Training Videos ---
    @Query("SELECT * FROM training_videos ORDER BY category ASC, id ASC")
    fun getAllVideos(): Flow<List<TrainingVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: TrainingVideoEntity)

    @Update
    suspend fun updateVideo(video: TrainingVideoEntity)

    @Query("DELETE FROM training_videos WHERE id = :id")
    suspend fun deleteVideo(id: String)

    // --- Video Progress ---
    @Query("SELECT * FROM video_progress")
    fun getAllVideoProgress(): Flow<List<VideoProgressEntity>>

    @Query("SELECT * FROM video_progress WHERE studentId = :studentId")
    fun getProgressForStudent(studentId: String): Flow<List<VideoProgressEntity>>

    @Query("SELECT * FROM video_progress WHERE studentId = :studentId AND videoId = :videoId LIMIT 1")
    suspend fun getProgressForVideo(studentId: String, videoId: String): VideoProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: VideoProgressEntity)

    // --- Fee Payments ---
    @Query("SELECT * FROM fee_payments ORDER BY id DESC")
    fun getAllPayments(): Flow<List<FeePaymentEntity>>

    @Query("SELECT * FROM fee_payments WHERE studentId = :studentId ORDER BY id DESC")
    fun getPaymentsForStudent(studentId: String): Flow<List<FeePaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: FeePaymentEntity): Long

    @Query("UPDATE fee_payments SET status = :status WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, status: String)

    @Query("SELECT * FROM fee_payments WHERE id = :id LIMIT 1")
    suspend fun getPaymentById(id: Long): FeePaymentEntity?

    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements")
    suspend fun getAnnouncementsSync(): List<AnnouncementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE studentId IS NULL OR studentId = :studentId ORDER BY timestamp DESC")
    fun getNotificationsForStudent(studentId: String): Flow<List<AppNotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE studentId IS NULL OR studentId = :studentId")
    suspend fun markAllNotificationsAsRead(studentId: String)

    // --- Performance Notes ---
    @Query("SELECT * FROM performance_notes WHERE studentId = :studentId ORDER BY id DESC")
    fun getNotesForStudent(studentId: String): Flow<List<PerformanceNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformanceNote(note: PerformanceNoteEntity)

    // --- Remote App Version Config ---
    @Query("SELECT * FROM app_update_config WHERE id = 'primary_config' LIMIT 1")
    fun getAppUpdateConfig(): Flow<AppUpdateConfigEntity?>

    @Query("SELECT * FROM app_update_config WHERE id = 'primary_config' LIMIT 1")
    suspend fun getAppUpdateConfigSync(): AppUpdateConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAppUpdateConfig(config: AppUpdateConfigEntity)

    // --- Step & Motion Activity Tracking ---
    @Query("SELECT * FROM daily_steps WHERE studentId = :studentId AND date = :date LIMIT 1")
    fun getDailySteps(studentId: String, date: String): Flow<DailyStepEntity?>

    @Query("SELECT * FROM daily_steps WHERE studentId = :studentId AND date = :date LIMIT 1")
    suspend fun getDailyStepsSync(studentId: String, date: String): DailyStepEntity?

    @Query("SELECT * FROM daily_steps WHERE studentId = :studentId ORDER BY date DESC LIMIT :limit")
    fun getRecentStepRecords(studentId: String, limit: Int = 7): Flow<List<DailyStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailySteps(dailyStep: DailyStepEntity)
}
