package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.XmaDatabase
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
import com.example.data.repository.XmaRepository
import com.example.sensor.StepTrackerManager
import com.example.update.AppUpdateManager
import com.example.update.UpdateCheckState
import com.example.update.UpdateDownloadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class UserRole {
    NONE,
    STUDENT,
    INSTRUCTOR
}

class XmaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: XmaRepository = XmaRepository(
        XmaDatabase.getDatabase(application, viewModelScope).xmaDao()
    )
    val appUpdateManager: AppUpdateManager = AppUpdateManager(application)
    val stepTrackerManager: StepTrackerManager = StepTrackerManager(application, viewModelScope)

    // App Update Configuration from Remote/Local
    val appUpdateConfig: StateFlow<AppUpdateConfigEntity?> = repository.appUpdateConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _updateCheckState = MutableStateFlow(UpdateCheckState.IDLE)
    val updateCheckState: StateFlow<UpdateCheckState> = _updateCheckState.asStateFlow()

    private val _isUpdateDialogVisible = MutableStateFlow(false)
    val isUpdateDialogVisible: StateFlow<Boolean> = _isUpdateDialogVisible.asStateFlow()

    val downloadState: StateFlow<UpdateDownloadState> = appUpdateManager.downloadState
    val updateDownloadState: StateFlow<UpdateDownloadState> get() = downloadState

    init {
        stepTrackerManager.onStepCountUpdated = { steps, _, motionType ->
            val sId = _currentStudentId.value
            if (sId != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.recordSteps(
                        studentId = sId,
                        date = todayDateString,
                        steps = steps,
                        goalSteps = stepTrackerManager.dailyGoal.value,
                        motionType = motionType
                    )
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureDefaultInstructors()
            repository.ensureDefaultStudents()
            repository.ensureDefaultBatches()
            repository.ensureDefaultVideos()
            repository.ensureAppUpdateConfig()
            // Automatic lightweight version check on app start
            checkForAppUpdates(silent = true)
        }
    }

    // Role and session
    private val _currentRole = MutableStateFlow(UserRole.NONE)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentStudentId = MutableStateFlow<String?>(null)
    val currentStudentId: StateFlow<String?> = _currentStudentId.asStateFlow()

    private val _currentInstructor = MutableStateFlow<InstructorEntity?>(null)
    val currentInstructor: StateFlow<InstructorEntity?> = _currentInstructor.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Global Collections
    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBatches: StateFlow<List<BatchEntity>> = repository.allBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideos: StateFlow<List<TrainingVideoEntity>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<FeePaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideoProgress: StateFlow<List<VideoProgressEntity>> = repository.allVideoProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Student Reactive State
    val currentStudent: StateFlow<StudentEntity?> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getStudent(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val studentAttendance: StateFlow<List<AttendanceEntity>> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getAttendanceForStudent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentProgress: StateFlow<List<VideoProgressEntity>> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getProgressForStudent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentPayments: StateFlow<List<FeePaymentEntity>> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getPaymentsForStudent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentNotes: StateFlow<List<PerformanceNoteEntity>> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getNotesForStudent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentNotifications: StateFlow<List<AppNotificationEntity>> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getNotificationsForStudent(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: Today's date
    val todayDateString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Derived: Today's attendance record for current student
    val todayStudentAttendance: StateFlow<AttendanceEntity?> = combine(
        studentAttendance,
        _currentStudentId
    ) { attList, _ ->
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        attList.firstOrNull { it.date == today }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Derived: Monthly attendance % for current student
    val studentMonthlyAttendancePercentage: StateFlow<Int> = studentAttendance.combine(
        _currentStudentId
    ) { list, _ ->
        if (list.isEmpty()) return@combine 0
        val presentCount = list.count { it.status == "Present" }
        ((presentCount.toFloat() / list.size.toFloat()) * 100).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 85)

    // Derived: Current consecutive attendance streak for consistency tracking
    val studentAttendanceStreak: StateFlow<Int> = studentAttendance.map { list ->
        var streak = 0
        val sorted = list.sortedByDescending { it.timestamp }
        for (item in sorted) {
            if (item.status == "Present") {
                streak++
            } else {
                break
            }
        }
        streak
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Derived: Category Progress Percentages for current student
    val studentCategoryProgress: StateFlow<Map<String, Int>> = combine(
        allVideos,
        studentProgress
    ) { videos, progressList ->
        val categories = listOf("KATA", "KUMITE", "KICKBOXING", "SELF DEFENCE", "FITNESS")
        val progressMap = progressList.associateBy { it.videoId }

        categories.associateWith { cat ->
            val catVideos = videos.filter { it.category.equals(cat, ignoreCase = true) }
            if (catVideos.isEmpty()) 0
            else {
                val totalPercent = catVideos.sumOf { v ->
                    progressMap[v.id]?.watchPercentage ?: 0
                }
                (totalPercent / catVideos.size)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Daily Steps & Motion Activity tracking for active student
    val studentDailySteps: StateFlow<DailyStepEntity?> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getDailySteps(id, todayDateString) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val studentRecentSteps: StateFlow<List<DailyStepEntity>> = _currentStudentId
        .flatMapLatest { id ->
            if (id != null) repository.getRecentStepRecords(id, 7) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isStepTrackerModalOpen = MutableStateFlow(false)
    val isStepTrackerModalOpen: StateFlow<Boolean> = _isStepTrackerModalOpen.asStateFlow()

    fun openStepTrackerModal() { _isStepTrackerModalOpen.value = true }
    fun closeStepTrackerModal() { _isStepTrackerModalOpen.value = false }

    fun updateDailyGoal(newGoal: Int) {
        stepTrackerManager.setDailyGoal(newGoal)
        val sId = _currentStudentId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordSteps(
                studentId = sId,
                date = todayDateString,
                steps = stepTrackerManager.todaySteps.value,
                goalSteps = newGoal,
                motionType = stepTrackerManager.motionActivity.value
            )
        }
    }

    val studentOverallProgress: StateFlow<Int> = studentCategoryProgress.combine(
        _currentStudentId
    ) { catMap, _ ->
        if (catMap.isEmpty()) 0
        else catMap.values.sum() / catMap.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 72)

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // --- Authentication Actions ---
    fun loginStudent(studentId: String, pin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.authenticateStudent(studentId, pin)
            result.onSuccess { student ->
                _currentStudentId.value = student.studentId
                _currentRole.value = UserRole.STUDENT
                _statusMessage.value = "Welcome back, ${student.fullName}!"
                
                // Seed initial steps if record exists today
                val existingSteps = repository.getDailySteps(student.studentId, todayDateString)
                stepTrackerManager.startTracking()
                onSuccess()
            }.onFailure { ex ->
                onError(ex.message ?: "Authentication failed")
            }
        }
    }

    fun loginInstructor(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.authenticateInstructor(email, pass)
            result.onSuccess { instructor ->
                _currentInstructor.value = instructor
                _currentRole.value = UserRole.INSTRUCTOR
                _statusMessage.value = "Welcome, ${instructor.fullName}!"
                onSuccess()
            }.onFailure { ex ->
                onError(ex.message ?: "Invalid instructor credentials")
            }
        }
    }

    fun logout() {
        stepTrackerManager.stopTracking()
        stepTrackerManager.stopWorkoutSession()
        _currentStudentId.value = null
        _currentInstructor.value = null
        _currentRole.value = UserRole.NONE
        _statusMessage.value = "Logged out successfully"
    }

    // --- Student Registration ---
    fun registerStudent(
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
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = (allStudents.value.size + 1)
            val newId = "student%03d".format(count)
            val batch = allBatches.value.firstOrNull { it.id == batchId } ?: allBatches.value.firstOrNull { it.name.equals(batchId, ignoreCase = true) }
            val batchName = batch?.name ?: "Head Branch"
            val effectiveBatchId = batch?.id ?: batchId

            val student = StudentEntity(
                studentId = newId,
                fullName = fullName.trim(),
                dob = dob.trim(),
                gender = gender,
                parentName = parentName.trim(),
                parentMobile = parentMobile.trim(),
                studentMobile = studentMobile.trim(),
                address = address.trim(),
                batchId = effectiveBatchId,
                batchName = batchName,
                joiningDate = todayDateString,
                instructorName = batch?.instructorName ?: "Sensei Raj Sanchu",
                monthlyFee = 2500.0,
                feeDueDate = "5th of every month",
                feeStatus = "Pending",
                accountStatus = "Active",
                pin = if (pin.isNotBlank()) pin.trim() else "1234",
                beltRank = "White Belt"
            )
            repository.insertStudent(student)
            onSuccess(newId)
        }
    }

    // --- Selfie Attendance Action ---
    fun markAttendanceSelfie(
        selfieUri: String,
        onSuccess: (AttendanceEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val student = currentStudent.value
        if (student == null) {
            onError("No active student session")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.markAttendanceWithSelfie(
                studentId = student.studentId,
                studentName = student.fullName,
                selfieUri = selfieUri
            )
            result.onSuccess { record ->
                _statusMessage.value = "Attendance Marked Successfully!"
                onSuccess(record)
            }.onFailure { ex ->
                onError(ex.message ?: "Failed to mark attendance")
            }
        }
    }

    // --- Video Progress Actions ---
    fun recordVideoProgress(
        videoId: String,
        percentage: Int,
        isCompleted: Boolean,
        lastPositionSec: Int
    ) {
        val studentId = currentStudentId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateVideoProgress(
                studentId = studentId,
                videoId = videoId,
                watchPercentage = percentage,
                isCompleted = isCompleted,
                lastPositionSec = lastPositionSec
            )
        }
    }

    fun markVideoComplete(videoId: String) {
        recordVideoProgress(videoId, 100, true, 480)
    }

    // --- Student Fee Payment Simulation & Verification ---
    fun studentPayFeeOnline(paymentMethod: String, transactionId: String? = null, onSuccess: () -> Unit) {
        val student = currentStudent.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
            val txId = if (!transactionId.isNullOrBlank()) transactionId else "TXN-PP-${System.currentTimeMillis().toString().takeLast(8)}"
            val receipt = "REC-XMA-${(1000..9999).random()}"
            val payment = FeePaymentEntity(
                studentId = student.studentId,
                studentName = student.fullName,
                month = month,
                amount = student.monthlyFee,
                paymentDate = todayDateString,
                paymentMethod = paymentMethod,
                transactionId = txId,
                status = "Paid",
                receiptNumber = receipt
            )
            repository.insertPayment(payment)
            _statusMessage.value = "Fee Paid Successfully! Receipt: $receipt"
            onSuccess()
        }
    }

    /**
     * Submits student UPI payment proof with strict UTR validation.
     * Note: Payment pendency strictly persists until verified by Admin.
     */
    fun studentSubmitUpiPaymentProof(
        utr: String,
        platform: String = "UPI App",
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        val student = currentStudent.value ?: run {
            onResult(false, "Student session not found. Please re-login.")
            return
        }
        val cleanUtr = utr.trim().replace(" ", "").replace("-", "")
        if (cleanUtr.length < 6) {
            onResult(false, "Kripya valid 12-digit UPI UTR / Transaction Ref number enter karein.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
            val receipt = "REC-XMA-${(1000..9999).random()}"
            val payment = FeePaymentEntity(
                studentId = student.studentId,
                studentName = student.fullName,
                month = month,
                amount = student.monthlyFee,
                paymentDate = todayDateString,
                paymentMethod = platform,
                transactionId = cleanUtr,
                status = "Pending Verification",
                receiptNumber = receipt
            )
            repository.insertPayment(payment)
            _statusMessage.value = "Payment Proof Submitted! UTR: $cleanUtr (Awaiting Admin Verification)"
            launch(Dispatchers.Main) {
                onResult(true, "UPI reference submitted! Payment status will remain PENDING until Sensei confirms credit in the bank account.")
            }
        }
    }

    fun adminApproveFeePayment(paymentId: Long, studentId: String = "", onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val resolvedStudentId = if (studentId.isNotBlank()) studentId else {
                allPayments.value.firstOrNull { it.id == paymentId }?.studentId ?: ""
            }
            repository.approvePayment(paymentId, resolvedStudentId)
            _statusMessage.value = "Fee payment approved & marked Paid!"
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun adminRejectFeePayment(paymentId: Long, studentId: String = "", reason: String = "Bank transaction not found or mismatched", onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val resolvedStudentId = if (studentId.isNotBlank()) studentId else {
                allPayments.value.firstOrNull { it.id == paymentId }?.studentId ?: ""
            }
            repository.rejectPayment(paymentId, resolvedStudentId, reason)
            _statusMessage.value = "Payment rejected. Fee status reverted to Pending."
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    // --- Admin / Instructor Actions ---
    fun adminAddStudent(student: StudentEntity, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStudent(student)
            onComplete()
        }
    }

    fun adminUpdateStudent(student: StudentEntity, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldStudent = repository.getStudentSync(student.studentId)
            repository.updateStudent(student)

            // Notify student immediately of changes
            if (oldStudent != null) {
                if (oldStudent.beltRank != student.beltRank) {
                    repository.insertNotification(
                        AppNotificationEntity(
                            studentId = student.studentId,
                            title = "🥋 Belt Promotion: ${student.beltRank}!",
                            message = "Congratulations! You have been promoted to ${student.beltRank} rank by ${currentInstructor.value?.fullName ?: "Sensei"}.",
                            type = "BELT_PROMOTION"
                        )
                    )
                }
                if (oldStudent.batchName != student.batchName || oldStudent.batchId != student.batchId) {
                    repository.insertNotification(
                        AppNotificationEntity(
                            studentId = student.studentId,
                            title = "🏫 Branch & Batch Updated",
                            message = "You are assigned to ${student.batchName} under ${student.instructorName}.",
                            type = "BRANCH_UPDATE"
                        )
                    )
                }
                if (oldStudent.feeStatus != student.feeStatus) {
                    repository.insertNotification(
                        AppNotificationEntity(
                            studentId = student.studentId,
                            title = "💳 Fee Status Updated",
                            message = "Your academy fee status has been set to '${student.feeStatus}' by Dojo admin.",
                            type = "FEE_REMINDER"
                        )
                    )
                }
                if (oldStudent.monthlyFee != student.monthlyFee) {
                    repository.insertNotification(
                        AppNotificationEntity(
                            studentId = student.studentId,
                            title = "💳 Monthly Fee Structure Updated",
                            message = "Your monthly fee structure is now ₹${student.monthlyFee.toInt()} (Due: ${student.feeDueDate}).",
                            type = "FEE_REMINDER"
                        )
                    )
                }
            } else {
                repository.insertNotification(
                    AppNotificationEntity(
                        studentId = student.studentId,
                        title = "📋 Student Profile Created",
                        message = "Welcome to XMA School! Your registration is complete.",
                        type = "PROFILE_UPDATE"
                    )
                )
            }
            onComplete()
        }
    }

    fun adminToggleStudentStatus(studentId: String, currentStatus: String) {
        val newStatus = if (currentStatus.equals("Active", ignoreCase = true)) "Inactive" else "Active"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudentStatus(studentId, newStatus)
            val title = if (newStatus == "Active") "🥋 Account Activated" else "⚠️ Account Deactivated"
            val msg = if (newStatus == "Active")
                "Your student account is now Active. You can mark attendance and access dojo modules."
            else
                "Your account is currently Inactive. Please contact Sensei for assistance."
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = studentId,
                    title = title,
                    message = msg,
                    type = "ACCOUNT_STATUS"
                )
            )
        }
    }

    fun updateStudentProfilePhoto(studentId: String, photoUri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudentPhoto(studentId, photoUri)
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = studentId,
                    title = "📸 Profile Photo Updated",
                    message = "Your profile picture has been updated successfully!",
                    type = "PROFILE"
                )
            )
        }
    }

    fun adminResetStudentPin(studentId: String, newPin: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudentPin(studentId, newPin)
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = studentId,
                    title = "🔑 Login PIN Updated",
                    message = "Your password/PIN has been reset to '$newPin' by your Instructor.",
                    type = "SECURITY"
                )
            )
            onComplete()
        }
    }

    fun adminRecordPayment(
        studentId: String,
        studentName: String,
        amount: Double,
        method: String,
        transId: String,
        month: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val receipt = "REC-XMA-${(1000..9999).random()}"
            val payment = FeePaymentEntity(
                studentId = studentId,
                studentName = studentName,
                month = month,
                amount = amount,
                paymentDate = todayDateString,
                paymentMethod = method,
                transactionId = if (transId.isNotBlank()) transId else "MANUAL-$receipt",
                status = "Paid",
                receiptNumber = receipt
            )
            repository.insertPayment(payment)

            // Update student's fee status to "Paid"
            val s = repository.getStudentSync(studentId)
            if (s != null) {
                repository.updateStudent(s.copy(feeStatus = "Paid"))
            }

            // Notify student immediately
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = studentId,
                    title = "💳 Fee Payment Received",
                    message = "Payment of ₹${amount.toInt()} for $month verified. Receipt No: $receipt via $method.",
                    type = "FEE_CONFIRMATION"
                )
            )
            onComplete()
        }
    }

    fun adminMarkStudentAttendance(studentId: String, status: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val student = repository.getStudentSync(studentId) ?: return@launch
            val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val attendance = AttendanceEntity(
                studentId = studentId,
                studentName = student.fullName,
                date = todayDateString,
                time = timeNow,
                status = status,
                verificationScore = if (status == "Present") 100 else 0,
                isVerified = status == "Present"
            )
            repository.insertAttendance(attendance)
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = studentId,
                    title = "📅 Attendance Marked by Instructor",
                    message = "Your attendance for $todayDateString has been marked as '$status' by Sensei.",
                    type = "ATTENDANCE"
                )
            )
            onComplete()
        }
    }

    fun adminAddVideo(video: TrainingVideoEntity, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertVideo(video)
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = null,
                    title = "🎥 New Training Video Added",
                    message = "New tutorial '${video.title}' (${video.category}) uploaded by ${video.instructorName}.",
                    type = "VIDEO"
                )
            )
            onComplete()
        }
    }

    fun adminUpdateVideo(video: TrainingVideoEntity, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateVideo(video)
            onComplete()
        }
    }

    fun adminDeleteVideo(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteVideo(videoId)
        }
    }

    fun adminAddBatch(batch: BatchEntity, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertBatch(batch)
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = null,
                    title = "🏫 New Branch & Batch Available",
                    message = "${batch.name} (${batch.branchName}) with ${batch.instructorName}. Schedule: ${batch.timing} (${batch.days})",
                    type = "ANNOUNCEMENT"
                )
            )
            onComplete()
        }
    }

    fun adminSendAnnouncement(title: String, message: String, targetBatch: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val ann = AnnouncementEntity(
                title = title.trim(),
                message = message.trim(),
                targetBatch = targetBatch,
                date = todayDateString,
                authorName = currentInstructor.value?.fullName ?: "Instructor"
            )
            repository.insertAnnouncement(ann)
            onComplete()
        }
    }

    fun adminAddPerformanceNote(studentId: String, note: String, tag: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val noteEntity = PerformanceNoteEntity(
                studentId = studentId,
                date = todayDateString,
                instructorName = currentInstructor.value?.fullName ?: "Master Vikram Sen",
                note = note.trim(),
                tag = tag.trim()
            )
            repository.insertPerformanceNote(noteEntity)
            repository.insertNotification(
                AppNotificationEntity(
                    studentId = studentId,
                    title = "🥋 Sensei Assessment Note",
                    message = "Tag: $tag\n$note\n- ${noteEntity.instructorName}",
                    type = "PERFORMANCE_NOTE"
                )
            )
            onComplete()
        }
    }

    fun markNotificationsRead() {
        val studentId = currentStudentId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAllNotificationsAsRead(studentId)
        }
    }

    // --- Report Generator ---
    fun generateReportCsv(reportType: String): String {
        val sb = StringBuilder()
        when (reportType) {
            "Daily Attendance" -> {
                sb.append("Student ID,Student Name,Date,Time,Status,Verification Score\n")
                val today = todayDateString
                allAttendance.value.filter { it.date == today }.forEach {
                    sb.append("${it.studentId},\"${it.studentName}\",${it.date},${it.time},${it.status},${it.verificationScore}%\n")
                }
            }
            "Monthly Attendance" -> {
                sb.append("Student ID,Student Name,Date,Time,Status\n")
                allAttendance.value.forEach {
                    sb.append("${it.studentId},\"${it.studentName}\",${it.date},${it.time},${it.status}\n")
                }
            }
            "Fee Pending Report" -> {
                sb.append("Student ID,Student Name,Batch,Monthly Fee,Fee Due Date,Status,Parent Mobile\n")
                allStudents.value.filter { it.feeStatus != "Paid" }.forEach {
                    sb.append("${it.studentId},\"${it.fullName}\",\"${it.batchName}\",₹${it.monthlyFee},\"${it.feeDueDate}\",${it.feeStatus},${it.parentMobile}\n")
                }
            }
            "Fee Collection Report" -> {
                sb.append("Receipt No,Student ID,Student Name,Month,Amount,Payment Date,Payment Method,Transaction ID\n")
                allPayments.value.forEach {
                    sb.append("${it.receiptNumber},${it.studentId},\"${it.studentName}\",\"${it.month}\",₹${it.amount},${it.paymentDate},${it.paymentMethod},${it.transactionId}\n")
                }
            }
            "Training Video Report" -> {
                sb.append("Video ID,Title,Category,Difficulty,Duration,Instructor\n")
                allVideos.value.forEach {
                    sb.append("${it.id},\"${it.title}\",${it.category},${it.difficulty},${it.duration},\"${it.instructorName}\"\n")
                }
            }
            else -> { // Student Performance Report
                sb.append("Student ID,Full Name,Batch,Belt Rank,Fee Status,Account Status\n")
                allStudents.value.forEach {
                    sb.append("${it.studentId},\"${it.fullName}\",\"${it.batchName}\",${it.beltRank},${it.feeStatus},${it.accountStatus}\n")
                }
            }
        }
        return sb.toString()
    }

    // --- REAL-TIME APP UPDATE SYSTEM ---

    /**
     * Checks for app updates against the remote/local version configuration.
     * Evaluates semantic version numbers and minimum supported versions.
     */
    fun checkForAppUpdates(silent: Boolean = false, onComplete: ((UpdateCheckState) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateCheckState.value = UpdateCheckState.CHECKING
            val config = repository.getAppUpdateConfigSync()
            if (config == null) {
                _updateCheckState.value = UpdateCheckState.NO_UPDATE_AVAILABLE
                onComplete?.invoke(UpdateCheckState.NO_UPDATE_AVAILABLE)
                return@launch
            }

            val status = appUpdateManager.evaluateUpdateStatus(config)
            _updateCheckState.value = status

            when (status) {
                UpdateCheckState.MANDATORY_UPDATE_AVAILABLE -> {
                    if (!silent) {
                        _isUpdateDialogVisible.value = true
                    }
                }
                UpdateCheckState.OPTIONAL_UPDATE_AVAILABLE -> {
                    if (!silent) {
                        _isUpdateDialogVisible.value = true
                    }
                }
                UpdateCheckState.NO_UPDATE_AVAILABLE -> {
                    if (!silent) {
                        _isUpdateDialogVisible.value = true
                        _statusMessage.value = "Showing latest release info (v${config.latestVersion})"
                    }
                }
                else -> {}
            }
            onComplete?.invoke(status)
        }
    }

    /**
     * Triggers the update procedure (Play Store or secure HTTPS download & install).
     */
    fun startAppUpdate() {
        val config = appUpdateConfig.value ?: return
        viewModelScope.launch {
            appUpdateManager.startUpdate(config)
        }
    }

    fun startAppUpdateDownload(config: AppUpdateConfigEntity? = null) {
        val targetConfig = config ?: appUpdateConfig.value ?: return
        viewModelScope.launch {
            appUpdateManager.startUpdate(targetConfig)
        }
    }

    /**
     * Dismisses the update dialog for optional updates.
     */
    fun dismissUpdateDialog() {
        val config = appUpdateConfig.value
        val isMandatory = config != null && appUpdateManager.evaluateUpdateStatus(config) == UpdateCheckState.MANDATORY_UPDATE_AVAILABLE
        if (!isMandatory) {
            _isUpdateDialogVisible.value = false
            appUpdateManager.resetDownloadState()
        }
    }

    /**
     * Displays update dialog manually (e.g. from student profile or notification).
     */
    fun showUpdateDialog() {
        _isUpdateDialogVisible.value = true
    }

    /**
     * Admin/Instructor configuration update:
     * Publishes a new version configuration remotely which triggers real-time updates for all students.
     */
    fun adminPublishAppVersion(
        latestVersion: String,
        latestVersionCode: Int,
        minimumSupportedVersion: String,
        minimumSupportedVersionCode: Int,
        updateType: String,
        updateUrl: String,
        releaseNotes: String,
        updateTitle: String,
        updateMessage: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedConfig = AppUpdateConfigEntity(
                id = "primary_config",
                latestVersion = latestVersion.trim(),
                latestVersionCode = latestVersionCode,
                minimumSupportedVersion = minimumSupportedVersion.trim(),
                minimumSupportedVersionCode = minimumSupportedVersionCode,
                updateType = updateType.uppercase().trim(),
                updateUrl = updateUrl.trim(),
                releaseNotes = releaseNotes.trim(),
                updateTitle = updateTitle.trim().ifBlank { "New Update Available" },
                updateMessage = updateMessage.trim(),
                publishedDate = todayDateString,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            repository.saveAppUpdateConfig(updatedConfig)
            // Re-evaluate update state immediately
            checkForAppUpdates(silent = true)
            _statusMessage.value = "New version ${latestVersion.trim()} published! Update broadcasted to students."
            onSuccess()
        }
    }
}
