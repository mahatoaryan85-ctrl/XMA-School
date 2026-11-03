package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        InstructorEntity::class,
        BatchEntity::class,
        AttendanceEntity::class,
        TrainingVideoEntity::class,
        VideoProgressEntity::class,
        FeePaymentEntity::class,
        AnnouncementEntity::class,
        AppNotificationEntity::class,
        PerformanceNoteEntity::class,
        AppUpdateConfigEntity::class,
        DailyStepEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class XmaDatabase : RoomDatabase() {
    abstract fun xmaDao(): XmaDao

    companion object {
        @Volatile
        private var INSTANCE: XmaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): XmaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XmaDatabase::class.java,
                    "xma_school_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(XmaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class XmaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.xmaDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    ensureBatchesSeeded(database.xmaDao())
                    ensureInstructorsSeeded(database.xmaDao())
                    ensureStudentsSeeded(database.xmaDao())
                    ensureVideosSeeded(database.xmaDao())
                    ensureAppUpdateConfigSeeded(database.xmaDao())
                    ensureFeeAnnouncementSeeded(database.xmaDao())
                    ensureAttendanceRewardAnnouncementSeeded(database.xmaDao())
                }
            }
        }
    }
}

suspend fun ensureBatchesSeeded(dao: XmaDao) {
    try {
        dao.deleteBatch("batch_a")
        dao.deleteBatch("batch_b")
        dao.deleteBatch("batch_c")
        val batches = listOf(
            BatchEntity(
                id = "batch_head",
                name = "Mega Sports Complex (Main Dojo)",
                timing = "5:00 PM – 6:30 PM",
                days = "Mon, Wed, Fri",
                instructorName = "Sensei Raj Sanchu",
                branchName = "Mega Sports Complex, Memco more, Chanchani Colony, Dhanbad, Jharkhand 826001"
            ),
            BatchEntity(
                id = "batch_apartment",
                name = "Apartment Branch",
                timing = "6:00 PM – 7:30 PM",
                days = "Tue, Thu, Sat",
                instructorName = "Sensei Ashish",
                branchName = "Apartment Branch"
            ),
            BatchEntity(
                id = "batch_sindri",
                name = "Sindri Branch",
                timing = "7:00 PM – 8:30 PM",
                days = "Mon to Sat",
                instructorName = "Sensei Sambhu",
                branchName = "Sindri Branch"
            )
        )
        batches.forEach { dao.insertBatch(it) }
    } catch (e: Exception) {
        // Ignored
    }
}

suspend fun ensureStudentsSeeded(dao: XmaDao) {
    try {
        val dummyOldIds = listOf("student001", "student002", "student003", "student004", "student005", "student006", "student007", "student008")
        dummyOldIds.forEach { dao.deleteStudent(it) }
    } catch (e: Exception) {
        // Ignored
    }

    val students = listOf(
        // 1. Piyush kumar
        StudentEntity(
            studentId = "Piyush06",
            fullName = "Piyush kumar",
            pin = "1234",
            dob = "2011-06-30",
            gender = "Male",
            parentName = "Jageshwar Singh",
            parentMobile = "+91 6206303447",
            studentMobile = "6206303447",
            address = "Thakur, khuli Dhaya, Dhanbad Jharkhand",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-01-15",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Orange Belt"
        ),
        // 2. Pawni kumari
        StudentEntity(
            studentId = "Pawni07",
            fullName = "Pawni kumari",
            pin = "1234",
            dob = "2014-03-25",
            gender = "Female",
            parentName = "Sundar Singh",
            parentMobile = "+91 7050870423",
            studentMobile = "7050870423",
            address = "THANKU KHULI DHAYA, DHANBAD JHARKHAND",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-02-10",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Orange Belt"
        ),
        // Pawani07 alias
        StudentEntity(
            studentId = "Pawani07",
            fullName = "Pawni kumari",
            pin = "1234",
            dob = "2014-03-25",
            gender = "Female",
            parentName = "Sundar Singh",
            parentMobile = "+91 7050870423",
            studentMobile = "7050870423",
            address = "Thakur kulhi dhaiya dhanbad",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-02-10",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Orange Belt"
        ),
        // 3. Riya Kumari
        StudentEntity(
            studentId = "Riya03",
            fullName = "Riya Kumari",
            pin = "1234",
            dob = "2013-09-14",
            gender = "Female",
            parentName = "Nand Kishor Rawani",
            parentMobile = "+91 6206500109",
            studentMobile = "6206500109",
            address = "Thankur Khuli, Dhaya Dhanbad Jharkhand",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-03-05",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Yellow Belt"
        ),
        // 4. Rajveer Kumar Rawani
        StudentEntity(
            studentId = "Rajveer08",
            fullName = "Rajveer Kumar Rawani",
            pin = "1234",
            dob = "2012-08-19",
            gender = "Male",
            parentName = "Nand Kishor Rawani",
            parentMobile = "+91 6206500109",
            studentMobile = "6206500109",
            address = "Thakur Khuli Dhaya Dhanbad, Jharkhand",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-03-05",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Orange Belt"
        ),
        // 5. Rajan kumar
        StudentEntity(
            studentId = "Rajan05",
            fullName = "Rajan kumar",
            pin = "1234",
            dob = "2012-01-18",
            gender = "Male",
            parentName = "Birendra Kumar",
            parentMobile = "+91 9241273500",
            studentMobile = "9241273500",
            address = "Thakur Khuli Dhaya Dhanbad Jharkhand",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-01-20",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Paid",
            accountStatus = "Active",
            beltRank = "Orange Belt"
        ),
        // 6. Rohit Kumar
        StudentEntity(
            studentId = "Rohit09",
            fullName = "Rohit Kumar",
            pin = "1234",
            dob = "2010-04-12",
            gender = "Male",
            parentName = "Sanjay Kumar Prasad",
            parentMobile = "+91 9204755758",
            studentMobile = "9204755758",
            address = "R.K one Roraband Sindri",
            batchId = "batch_sindri",
            batchName = "Sindri Branch",
            joiningDate = "2023-09-10",
            instructorName = "Sensei Sambhu",
            monthlyFee = 600.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Brown Belt"
        ),
        // 7. Tripti Chandrawanshi
        StudentEntity(
            studentId = "Tripti04",
            fullName = "Tripti Chandrawanshi",
            pin = "1234",
            dob = "2010-11-05",
            gender = "Female",
            parentName = "Ram Chandra Prasad",
            parentMobile = "+91 8789983978",
            studentMobile = "8789983978",
            address = "Katrash Road, Matkuria Jatt Mohalla Dhanbad",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2023-10-15",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Brown Belt"
        ),
        // 8. Ayushi Kumari
        StudentEntity(
            studentId = "Ayushi02",
            fullName = "Ayushi Kumari",
            pin = "1234",
            dob = "2011-07-22",
            gender = "Female",
            parentName = "Uday Kumar",
            parentMobile = "+91 9546444047",
            studentMobile = "9546444047",
            address = "Babudih near Jila School, Dhanbad Jharkhand",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2023-11-01",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Brown Belt"
        ),
        // 9. Shivam Kumar singh
        StudentEntity(
            studentId = "Shivam10",
            fullName = "Shivam Kumar singh",
            pin = "1234",
            dob = "2013-05-18",
            gender = "Male",
            parentName = "Anil kumar singh",
            parentMobile = "+91 6205366568",
            studentMobile = "6205366568",
            address = "Dhanbad bhuli A bolck",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-04-11",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "Yellow Belt"
        ),
        // 10. Kushagra Singh
        StudentEntity(
            studentId = "Kushagra11",
            fullName = "Kushagra Singh",
            pin = "1234",
            dob = "2014-08-20",
            gender = "Male",
            parentName = "Kamalesh Kumar singh",
            parentMobile = "+91 6205130647",
            studentMobile = "6205130647",
            address = "Tapowan colony , saraidhela Dhanbad",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-06-01",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Pending",
            accountStatus = "Active",
            beltRank = "White Belt"
        ),
        // 11. Prachi
        StudentEntity(
            studentId = "Prachi01",
            fullName = "Prachi",
            pin = "1234",
            dob = "2012-04-10",
            gender = "Female",
            parentName = "Mrs. Sharma",
            parentMobile = "+91 98765 00001",
            studentMobile = "9876500001",
            address = "Flat 101, Shanti Nagar, Dhanbad",
            batchId = "batch_head",
            batchName = "Head Branch",
            joiningDate = "2024-02-15",
            instructorName = "Sensei Raj Sanchu",
            monthlyFee = 500.0,
            feeDueDate = "5th of every month",
            feeStatus = "Paid",
            accountStatus = "Active",
            beltRank = "Yellow Belt"
        )
    )
    students.forEach { student ->
        try {
            dao.insertStudent(student)
            // Seed realistic selfie attendance consistency history if none exists
            if (dao.getAttendanceCountForStudent(student.studentId) == 0) {
                val sampleDates = listOf(
                    "2026-08-17", "2026-08-19", "2026-08-21", "2026-08-24", "2026-08-26",
                    "2026-08-28", "2026-08-31", "2026-09-02", "2026-09-04", "2026-09-07", "2026-09-09"
                )
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                sampleDates.forEachIndexed { index, dt ->
                    val isAbsent = (index == 4)
                    val isLate = (index == 7)
                    val status = if (isAbsent) "Absent" else if (isLate) "Late" else "Present"
                    val time = if (isLate) "17:18:40" else "16:58:20"
                    val parsedTime = try { sdf.parse("$dt $time")?.time ?: System.currentTimeMillis() } catch (e: Exception) { System.currentTimeMillis() }
                    dao.insertAttendance(
                        AttendanceEntity(
                            studentId = student.studentId,
                            studentName = student.fullName,
                            date = dt,
                            time = time,
                            timestamp = parsedTime,
                            selfieUri = "camerax_selfie_${student.studentId}_$dt",
                            status = status,
                            verificationScore = if (isAbsent) 0 else (96..99).random(),
                            isVerified = !isAbsent,
                            remarks = if (isAbsent) "Excused Absence" else if (isLate) "Checked in after warmup • Verified" else "Live CameraX Selfie Verified"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Ignored
        }
    }
}

suspend fun ensureInstructorsSeeded(dao: XmaDao) {
    try {
        dao.insertInstructor(
            InstructorEntity(
                email = "Raj7007",
                fullName = "Sensei Raj Sanchu",
                password = "7714",
                phone = "+91 98765 77007",
                role = "Chief Instructor (Main Branch - Head Branch)"
            )
        )
        dao.insertInstructor(
            InstructorEntity(
                email = "Ashish99",
                fullName = "Sensei Ashish",
                password = "2244",
                phone = "+91 98765 88099",
                role = "Branch Instructor (Apartment Branch)"
            )
        )
        dao.insertInstructor(
            InstructorEntity(
                email = "Ashish01",
                fullName = "Sensei Ashish",
                password = "1234",
                phone = "+91 98765 88001",
                role = "Branch Instructor (Apartment Branch)"
            )
        )
        dao.insertInstructor(
            InstructorEntity(
                email = "Sambhu01",
                fullName = "Sensei Sambhu",
                password = "1234",
                phone = "+91 98765 88002",
                role = "Branch Instructor (Sindri Branch)"
            )
        )
        dao.insertInstructor(
            InstructorEntity(
                email = "admin@xmaschool.com",
                fullName = "Master Vikram Sen",
                password = "admin123",
                phone = "+91 98765 43210",
                role = "Head Master & Administrator"
            )
        )
    } catch (e: Exception) {
        // Ignored
    }
}

suspend fun ensureAppUpdateConfigSeeded(dao: XmaDao) {
    try {
        val existing = dao.getAppUpdateConfigSync()
        if (existing == null || existing.latestVersionCode < 8 || existing.latestVersion != "1.6.0") {
            dao.insertOrUpdateAppUpdateConfig(
                AppUpdateConfigEntity(
                    id = "primary_config",
                    latestVersion = "1.6.0",
                    latestVersionCode = 8,
                    minimumSupportedVersion = "1.0.0",
                    minimumSupportedVersionCode = 1,
                    updateType = "OPTIONAL",
                    updateUrl = "https://github.com/martial-arts/xma-school/releases/latest/download/xma_school_v1_6_0.apk",
                    releaseNotes = "• Ask Gemini AI: Dedicated Martial Arts & Diet Coach for Kata, Kumite, Flexibility Drills & Nutrition\n• 100% Attendance Reward: Wireless Neckband FREE for students with 100% attendance\n• YouTube Training Player: Real thumbnails & dual-mode in-app and YouTube app playback\n• Student Profile Photo: Upload & update martial arts avatar directly\n• Headquarters: Mega Sports Complex, Memco More, Dhanbad Dojo under Sensei Raj Sanchu",
                    updateTitle = "XMA Official Release v1.6.0",
                    updateMessage = "New update v1.6.0: Ask Gemini AI Coach is live, plus 100% Attendance Wireless Neckband FREE reward!",
                    publishedDate = "2026-09-21",
                    lastUpdatedTimestamp = System.currentTimeMillis()
                )
            )
        }
    } catch (e: Exception) {
        // Ignored
    }
}

suspend fun ensureFeeAnnouncementSeeded(dao: XmaDao) {
    try {
        val existing = dao.getAnnouncementsSync()
        val hasFeeNotice = existing.any { it.title.contains("Monthly Training Fee", ignoreCase = true) }
        if (!hasFeeNotice) {
            dao.insertAnnouncement(
                AnnouncementEntity(
                    title = "📢 Monthly Training Fee Payment Notice",
                    message = "Dear Student/Parent, please pay your monthly training fee on time to continue your regular classes without interruption.",
                    targetBatch = "All",
                    date = "2026-09-10",
                    authorName = "Head Master & Administrator"
                )
            )
        }
    } catch (e: Exception) {
        // Ignored
    }
}

suspend fun ensureAttendanceRewardAnnouncementSeeded(dao: XmaDao) {
    try {
        val existing = dao.getAnnouncementsSync()
        val hasRewardNotice = existing.any { it.title.contains("100% Attendance", ignoreCase = true) || it.title.contains("Neckband", ignoreCase = true) }
        if (!hasRewardNotice) {
            dao.insertAnnouncement(
                AnnouncementEntity(
                    title = "🎧 100% Attendance Special Reward: Wireless Neckband FREE!",
                    message = "Academy Special Offer: 100% Attendance wale sabhi students ko Academy ki taraf se Wireless Neckband (Bluetooth Wireless) bilkul FREE diya jayega! Har class attend karein aur month-end par Sensei se apna wireless neckband reward prapt karein.",
                    targetBatch = "All",
                    date = "2026-09-15",
                    authorName = "Sensei Raj Sanchu (Chief Instructor)"
                )
            )
        }
    } catch (e: Exception) {
        // Ignored
    }
}

suspend fun ensureVideosSeeded(dao: XmaDao) {
    val wkfVideos = listOf(
        // ================= WKF KATA (Official World Karate Federation Katas) =================
        TrainingVideoEntity(
            id = "wkf_kata_01",
            title = "WKF Kata: Chatanyara Kushanku (Ryo Kiyuna - Olympic Gold)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "7 mins",
            durationSeconds = 420,
            description = "Legendary Okinawan Shito-Ryu master Kata performed by 4x WKF World Champion & Olympic Gold Medalist Ryo Kiyuna. Ultimate technical precision, breathing, and explosive kiai.",
            instructorName = "Ryo Kiyuna (WKF Champion)",
            videoUrl = "https://www.youtube.com/watch?v=0_u6R7_nO_Y"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_02",
            title = "WKF Kata: Anan Dai (Kiyou Shimizu - 2x World Champion)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "6 mins",
            durationSeconds = 360,
            description = "Ryuei-ryu master Kata by 2-time World Champion Kiyou Shimizu. Explosive double thrusts, open-hand blocks, and sharp speed variations.",
            instructorName = "Kiyou Shimizu (World Champion)",
            videoUrl = "https://www.youtube.com/watch?v=kY31R93iKFc"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_03",
            title = "WKF Kata: Papuren (Sandra Sánchez - World & Olympic Gold)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "6 mins",
            durationSeconds = 360,
            description = "Shito-ryu masterpiece by Sandra Sánchez. Elegant white crane posture, piercing open-hand nukite, and razor-sharp rotational kime.",
            instructorName = "Sandra Sánchez (Olympic Gold)",
            videoUrl = "https://www.youtube.com/watch?v=i9Yc_2fXkC8"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_04",
            title = "WKF Kata: Suparinpei (108 Techniques - World Final)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "8 mins",
            durationSeconds = 480,
            description = "Pinnacle Goju/Shito Kata with 108 movements: circular mawashi uke, hook punches, stamping kicks, and intricate self-defense applications.",
            instructorName = "WKF World Championships",
            videoUrl = "https://www.youtube.com/watch?v=gT8wL-s5m7o"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_05",
            title = "WKF Kata: Kanku Sho (Shotokan High Flying Competition Form)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "5 mins",
            durationSeconds = 300,
            description = "Dynamic Shotokan competition Kata featuring high jumping kicks (Tobi Geri), back-fist whips (Uraken), and rapid directional changes.",
            instructorName = "WKF World Karate Federation",
            videoUrl = "https://www.youtube.com/watch?v=q6g4jE0pMCo"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_06",
            title = "WKF Kata: Gojushiho Sho (54 Steps Master Performance)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "6 mins",
            durationSeconds = 360,
            description = "Elite Shotokan Kata featuring eagle-beak strikes (Keiko), spear fingers, and low Zenkutsu Dachi transitions in WKF competition.",
            instructorName = "WKF Karate1 Premier League",
            videoUrl = "https://www.youtube.com/watch?v=hZ-L5-1V_v4"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_07",
            title = "WKF Kata: Unsu (Cloud Hands - 360 Degree Aerial Spin)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "5 mins",
            durationSeconds = 300,
            description = "The most athletic Shotokan Kata with double ground sweeps, floor roundhouse kicks, and dramatic 360-degree jumping turn.",
            instructorName = "WKF World Championships",
            videoUrl = "https://www.youtube.com/watch?v=e_6Y5X3XUv8"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_08",
            title = "WKF Kata: Enpi (Flying Swallow - Elevation & Velocity)",
            category = "WKF KATA",
            difficulty = "Intermediate",
            duration = "5 mins",
            durationSeconds = 300,
            description = "Rapid elevation changes mimicking a swallow in flight. Emphasizes rising elbow strikes (Empi), grasping, and swift turns.",
            instructorName = "Sensei Raj Sanchu",
            videoUrl = "https://www.youtube.com/watch?v=W-L1i-XGk60"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_09",
            title = "WKF Kata: Bassai Dai (Storming the Fortress - Official Standard)",
            category = "WKF KATA",
            difficulty = "Intermediate",
            duration = "6 mins",
            durationSeconds = 360,
            description = "Fundamental Black Belt tournament Kata. Powerful hip snaps, double forearm blocks, mountain punch (Yama Zuki), and sweeping defenses.",
            instructorName = "Sensei Raj Sanchu",
            videoUrl = "https://www.youtube.com/watch?v=rF8n-k2H5nU"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_10",
            title = "WKF Kata: Heian Shodan to Godan (Complete 1 to 5 Syllabus)",
            category = "WKF KATA",
            difficulty = "Beginner",
            duration = "14 mins",
            durationSeconds = 840,
            description = "All five basic Heian / Pinan foundation Katas (Form 1 to 5) taught step-by-step for Kyu belt examinations under WKF standard.",
            instructorName = "Sensei Raj Sanchu",
            videoUrl = "https://www.youtube.com/watch?v=zJg5lGkF5qI"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_11",
            title = "WKF Kata: Gankaku (Crane on a Rock - Balance & Side Kicks)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "6 mins",
            durationSeconds = 360,
            description = "Standing on one leg in Tsuruashi Dachi, launching piercing side thrust kicks (Yoko Geri) and lightning counter strikes.",
            instructorName = "WKF World Karate Federation",
            videoUrl = "https://www.youtube.com/watch?v=B9Jk7o6zZ8s"
        ),
        TrainingVideoEntity(
            id = "wkf_kata_12",
            title = "WKF Team Kata & Bunkai (World Championship Gold)",
            category = "WKF KATA",
            difficulty = "Advanced",
            duration = "9 mins",
            durationSeconds = 540,
            description = "Electrifying 3-person synchronized Kata followed by realistic, full-contact martial bunkai applications and takedowns.",
            instructorName = "WKF World Championships",
            videoUrl = "https://www.youtube.com/watch?v=lO-f3F4p1A8"
        ),

        // ================= WKF KUMITE (Official Championship, Scoring & Techniques) =================
        TrainingVideoEntity(
            id = "wkf_kumite_01",
            title = "WKF Kumite: World Championships Best Highlights & Ippons",
            category = "WKF KUMITE",
            difficulty = "Advanced",
            duration = "10 mins",
            durationSeconds = 600,
            description = "Exciting compilation of the cleanest 3-point Ippons, rapid counter-punches, head kicks, and buzzer-beater finishes from WKF World Championships.",
            instructorName = "WKF World Championships",
            videoUrl = "https://www.youtube.com/watch?v=N6C5n9o1_jQ"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_02",
            title = "WKF Kumite: Rules & Scoring System (Yuko, Waza-ari, Ippon)",
            category = "WKF KUMITE",
            difficulty = "Beginner",
            duration = "8 mins",
            durationSeconds = 480,
            description = "Official guide to WKF point scoring: 1 pt Yuko (punch), 2 pts Waza-ari (body kick), 3 pts Ippon (head kick or sweep + punch), warnings and Senshu rule.",
            instructorName = "WKF Referees Commission",
            videoUrl = "https://www.youtube.com/watch?v=mD3Sg7s4-Lg"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_03",
            title = "WKF Kumite: Rafael Aghayev Masterclass (Footwork & Feints)",
            category = "WKF KUMITE",
            difficulty = "Advanced",
            duration = "12 mins",
            durationSeconds = 720,
            description = "5-time World Champion Rafael Aghayev teaches bouncing footwork, deceptive level changes, inside pocket blitzes, and unblockable timing.",
            instructorName = "Rafael Aghayev (5x World Champion)",
            videoUrl = "https://www.youtube.com/watch?v=8l-6Gf-k9oY"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_04",
            title = "WKF Kumite: Kizami Zuki & Gyaku Zuki Lightning Speed Drills",
            category = "WKF KUMITE",
            difficulty = "Beginner",
            duration = "7 mins",
            durationSeconds = 420,
            description = "High-speed drills for the lead jab (Kizami) and rear reverse punch (Gyaku Zuki) that score in more than 60% of all international matches.",
            instructorName = "Sensei Raj Sanchu",
            videoUrl = "https://www.youtube.com/watch?v=c0d-M9z6Xh8"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_05",
            title = "WKF Kumite: Ashi Barai (Foot Sweeps) & Takedowns for Ippon",
            category = "WKF KUMITE",
            difficulty = "Intermediate",
            duration = "9 mins",
            durationSeconds = 540,
            description = "How to catch opponents during weight transfer, execute legal WKF sweeps (Ashi Barai), and land downward punches on the grounded opponent.",
            instructorName = "Sensei Sambhu",
            videoUrl = "https://www.youtube.com/watch?v=5W_9bF4o6vY"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_06",
            title = "WKF Kumite: Ura Mawashi Geri (Hook Kick) Master Execution",
            category = "WKF KUMITE",
            difficulty = "Advanced",
            duration = "8 mins",
            durationSeconds = 480,
            description = "Scoring high-value 3-point Ippons using hidden lead-leg hook kicks and spinning reverse hook kicks behind the opponent's guard.",
            instructorName = "Sensei Ashish",
            videoUrl = "https://www.youtube.com/watch?v=f7D9c-5wM8k"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_07",
            title = "WKF Kumite: Tactical Match Strategy & Senshu Management",
            category = "WKF KUMITE",
            difficulty = "Intermediate",
            duration = "9 mins",
            durationSeconds = 540,
            description = "Scoring the crucial first point (Senshu), ring control, drawing out reckless attacks, clock management, and protecting lead under pressure.",
            instructorName = "Master Vikram Sen",
            videoUrl = "https://www.youtube.com/watch?v=P9c3y1L5oK4"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_08",
            title = "WKF Kumite: World Karate Championship Grand Finals",
            category = "WKF KUMITE",
            difficulty = "Advanced",
            duration = "15 mins",
            durationSeconds = 900,
            description = "World championship final clashes showcasing supreme reflexes, distance management (Ma-ai), blitz speed, and mental fortitude.",
            instructorName = "WKF World Karate Federation",
            videoUrl = "https://www.youtube.com/watch?v=kX5b9L3jO_A"
        ),
        TrainingVideoEntity(
            id = "wkf_kumite_09",
            title = "WKF Kumite: Defence, Parrying & Sen no Sen Counter-Attacks",
            category = "WKF KUMITE",
            difficulty = "Intermediate",
            duration = "8 mins",
            durationSeconds = 480,
            description = "Interception drills: redirecting straight attacks with open-hand parries and scoring instantaneously into the opening as the opponent commits forward.",
            instructorName = "Sensei Raj Sanchu",
            videoUrl = "https://www.youtube.com/watch?v=W-L1i-XGk60"
        ),

        // ================= KICKBOXING, SELF DEFENCE & FITNESS =================
        TrainingVideoEntity(
            id = "kb_01",
            title = "Kickboxing: Stance, Guard & Low Kick Drills",
            category = "KICKBOXING",
            difficulty = "Beginner",
            duration = "7 mins",
            durationSeconds = 420,
            description = "Dutch-style tight high guard, hip rotation for inside and outside low kicks, and head movement slipping drills.",
            instructorName = "Sensei Sambhu",
            videoUrl = "https://www.youtube.com/watch?v=c0d-M9z6Xh8"
        ),
        TrainingVideoEntity(
            id = "kb_02",
            title = "Kickboxing: Combination Punching & Roundhouse Finishes",
            category = "KICKBOXING",
            difficulty = "Intermediate",
            duration = "9 mins",
            durationSeconds = 540,
            description = "Jab-cross-hook combinations leading to devastating liver strikes and roundhouse kicks to the ribs and head.",
            instructorName = "Sensei Sambhu",
            videoUrl = "https://www.youtube.com/watch?v=8l-6Gf-k9oY"
        ),
        TrainingVideoEntity(
            id = "sd_01",
            title = "Self Defence: Street Grab Escapes & Wrist Releases",
            category = "SELF DEFENCE",
            difficulty = "Beginner",
            duration = "8 mins",
            durationSeconds = 480,
            description = "Quick, instinctive wrist releases, leverage against the attacker's thumb, palm strikes, and creating immediate escape distance.",
            instructorName = "Sensei Ashish",
            videoUrl = "https://www.youtube.com/watch?v=5W_9bF4o6vY"
        ),
        TrainingVideoEntity(
            id = "fit_01",
            title = "Martial Arts Conditioning: Explosive Cardio & Core Strength",
            category = "FITNESS",
            difficulty = "Intermediate",
            duration = "15 mins",
            durationSeconds = 900,
            description = "Full body circuit: sprawl burpees, mountain climbers, core rotational planks, and high knee shadow-striking.",
            instructorName = "Master Vikram Sen",
            videoUrl = "https://www.youtube.com/watch?v=N6C5n9o1_jQ"
        )
    )

    wkfVideos.forEach { video ->
        dao.insertVideo(video)
    }
}

suspend fun populateInitialData(dao: XmaDao) {
    // 1. Instructors
    dao.insertInstructor(
        InstructorEntity(
            email = "Raj7007",
            fullName = "Sensei Raj Sanchu",
            password = "7714",
            phone = "+91 98765 77007",
            role = "Chief Instructor (Main Branch - Head Branch)"
        )
    )
    dao.insertInstructor(
        InstructorEntity(
            email = "Ashish99",
            fullName = "Sensei Ashish",
            password = "2244",
            phone = "+91 98765 88099",
            role = "Branch Instructor (Apartment Branch)"
        )
    )
    dao.insertInstructor(
        InstructorEntity(
            email = "Ashish01",
            fullName = "Sensei Ashish",
            password = "1234",
            phone = "+91 98765 88001",
            role = "Branch Instructor (Apartment Branch)"
        )
    )
    dao.insertInstructor(
        InstructorEntity(
            email = "Sambhu01",
            fullName = "Sensei Sambhu",
            password = "1234",
            phone = "+91 98765 88002",
            role = "Branch Instructor (Sindri Branch)"
        )
    )
    dao.insertInstructor(
        InstructorEntity(
            email = "admin@xmaschool.com",
            fullName = "Master Vikram Sen",
            password = "admin123",
            phone = "+91 98765 43210",
            role = "Head Master & Administrator"
        )
    )

    // 2. Batches
    val batches = listOf(
        BatchEntity("batch_head", "Mega Sports Complex (Main Dojo)", "5:00 PM – 6:30 PM", "Mon, Wed, Fri", "Sensei Raj Sanchu", "Mega Sports Complex, Memco more, Chanchani Colony, Dhanbad, Jharkhand 826001"),
        BatchEntity("batch_apartment", "Apartment Branch", "6:00 PM – 7:30 PM", "Tue, Thu, Sat", "Sensei Ashish", "Apartment Branch"),
        BatchEntity("batch_sindri", "Sindri Branch", "7:00 PM – 8:30 PM", "Mon to Sat", "Sensei Sambhu", "Sindri Branch")
    )
    batches.forEach { dao.insertBatch(it) }

    // 3. Students (Real students seeded from school register)
    ensureStudentsSeeded(dao)

    // 4. Official WKF Kata & Kumite Training Videos (YouTube Links)
    ensureVideosSeeded(dao)

    // 5. Initial video progress for student001
    val sampleProgress = listOf(
        VideoProgressEntity(studentId = "student001", videoId = "wkf_kata_01", watchPercentage = 100, isCompleted = true, completedDate = "2026-08-25"),
        VideoProgressEntity(studentId = "student001", videoId = "wkf_kata_02", watchPercentage = 70, isCompleted = false, lastPositionSeconds = 462),
        VideoProgressEntity(studentId = "student001", videoId = "wkf_kumite_01", watchPercentage = 100, isCompleted = true, completedDate = "2026-08-28"),
        VideoProgressEntity(studentId = "student001", videoId = "wkf_kumite_02", watchPercentage = 45, isCompleted = false, lastPositionSeconds = 270),
        VideoProgressEntity(studentId = "student001", videoId = "kb_01", watchPercentage = 100, isCompleted = true, completedDate = "2026-08-29"),
        VideoProgressEntity(studentId = "student001", videoId = "kb_02", watchPercentage = 80, isCompleted = false, lastPositionSeconds = 384),
        VideoProgressEntity(studentId = "student001", videoId = "sd_01", watchPercentage = 60, isCompleted = false, lastPositionSeconds = 288),
        VideoProgressEntity(studentId = "student001", videoId = "fit_01", watchPercentage = 100, isCompleted = true, completedDate = "2026-08-30")
    )
    sampleProgress.forEach { dao.insertOrUpdateProgress(it) }

    // 6. Realistic Attendance History for student001 and classmates
    val pastDates = listOf(
        "2026-08-01", "2026-08-03", "2026-08-05", "2026-08-08", "2026-08-10",
        "2026-08-12", "2026-08-15", "2026-08-17", "2026-08-19", "2026-08-22",
        "2026-08-24", "2026-08-26", "2026-08-29", "2026-09-01"
    )
    pastDates.forEachIndexed { index, date ->
        val status = if (index == 4 || index == 11) "Absent" else "Present"
        dao.insertAttendance(
            AttendanceEntity(
                studentId = "student001",
                studentName = "Aarav Sharma",
                date = date,
                time = "17:04:15",
                selfieUri = "selfie_aarav_$index",
                status = status,
                verificationScore = if (status == "Present") 97 else 0,
                isVerified = (status == "Present")
            )
        )
    }

    // Other students' attendance for today
    val otherTodayAttendees = listOf(
        Pair("student002", "Priya Patel"),
        Pair("student004", "Sneha Reddy"),
        Pair("student005", "Kabir Singh"),
        Pair("student007", "Vivaan Gupta"),
        Pair("student008", "Zoya Khan")
    )
    otherTodayAttendees.forEach { (id, name) ->
        dao.insertAttendance(
            AttendanceEntity(
                studentId = id,
                studentName = name,
                date = "2026-09-02",
                time = "17:02:10",
                selfieUri = "selfie_${id}_today",
                status = "Present",
                verificationScore = 98,
                isVerified = true
            )
        )
    }

    // 7. Fee Payments History
    dao.insertPayment(
        FeePaymentEntity(
            studentId = "student001",
            studentName = "Aarav Sharma",
            month = "July 2026",
            amount = 2500.0,
            paymentDate = "2026-07-04",
            paymentMethod = "UPI",
            transactionId = "UPI-XMA-99281726",
            status = "Paid",
            receiptNumber = "REC-2026-07-001"
        )
    )
    dao.insertPayment(
        FeePaymentEntity(
            studentId = "student001",
            studentName = "Aarav Sharma",
            month = "August 2026",
            amount = 2500.0,
            paymentDate = "2026-08-05",
            paymentMethod = "Cash",
            transactionId = "CASH-REC-1049",
            status = "Paid",
            receiptNumber = "REC-2026-08-001"
        )
    )

    // 8. Announcements
    dao.insertAnnouncement(
        AnnouncementEntity(
            title = "📢 Monthly Training Fee Payment Notice",
            message = "Dear Student/Parent, please pay your monthly training fee on time to continue your regular classes without interruption.",
            targetBatch = "All",
            date = "2026-09-10",
            authorName = "Head Master & Administrator"
        )
    )
    dao.insertAnnouncement(
        AnnouncementEntity(
            title = "🎧 100% Attendance Special Reward: Wireless Neckband FREE!",
            message = "Academy Special Offer: 100% Attendance wale sabhi students ko Academy ki taraf se Wireless Neckband (Bluetooth Wireless) bilkul FREE diya jayega! Har class attend karein aur month-end par Sensei se apna wireless neckband reward prapt karein.",
            targetBatch = "All",
            date = "2026-09-15",
            authorName = "Sensei Raj Sanchu (Chief Instructor)"
        )
    )
    dao.insertAnnouncement(
        AnnouncementEntity(
            title = "🥋 Belt Grading & Examination Schedule",
            message = "Upcoming Yellow & Green Belt evaluation will take place on Saturday, 26th September at 10:00 AM. Please ensure full uniform (Dogi) and clean belt.",
            targetBatch = "All",
            date = "2026-09-01",
            authorName = "Master Vikram Sen"
        )
    )
    dao.insertAnnouncement(
        AnnouncementEntity(
            title = "🥊 Weekend Kumite Sparring Camp",
            message = "Special sparring strategy session for Batch A & B this Sunday morning from 8:00 AM to 10:00 AM. Bring mouth guards and shin guards.",
            targetBatch = "XMA BATCH A",
            date = "2026-08-28",
            authorName = "Sensei Ryu"
        )
    )

    // 9. Notifications
    dao.insertNotification(
        AppNotificationEntity(
            studentId = "student001",
            title = "Monthly Fee Reminder",
            message = "Your monthly XMA SCHOOL training fee of ₹2,500 is due on 5th September. Please pay to avoid late fees.",
            type = "FEE_REMINDER",
            timestamp = System.currentTimeMillis() - 3600000L * 4
        )
    )
    dao.insertNotification(
        AppNotificationEntity(
            studentId = null,
            title = "New Training Video Uploaded!",
            message = "Master Vikram has uploaded 'Core Strength & Punching Power' in the Fitness Library.",
            type = "VIDEO",
            timestamp = System.currentTimeMillis() - 3600000L * 24
        )
    )
    dao.insertNotification(
        AppNotificationEntity(
            studentId = "student001",
            title = "Selfie Attendance Reminder",
            message = "Don't forget to mark your selfie attendance before your 5:00 PM batch today!",
            type = "ATTENDANCE",
            timestamp = System.currentTimeMillis() - 3600000L * 2
        )
    )

    // 10. Performance Notes
    dao.insertPerformanceNote(
        PerformanceNoteEntity(
            studentId = "student001",
            date = "2026-08-28",
            instructorName = "Master Vikram Sen",
            note = "Excellent focus during Heian Shodan kata practice. Great stance depth.",
            tag = "Good discipline"
        )
    )
    dao.insertPerformanceNote(
        PerformanceNoteEntity(
            studentId = "student001",
            date = "2026-08-20",
            instructorName = "Sensei Ryu",
            note = "Need to work on hip rotation for roundhouse kick. Keep hands up in guard position.",
            tag = "Improve flexibility"
        )
    )
    dao.insertPerformanceNote(
        PerformanceNoteEntity(
            studentId = "student001",
            date = "2026-08-10",
            instructorName = "Master Vikram Sen",
            note = "Outstanding attendance record this month. Showing true martial artist dedication.",
            tag = "Excellent attendance"
        )
    )

    // 11. Initial App Update Configuration (v1.6.0 Production Release)
    dao.insertOrUpdateAppUpdateConfig(
        AppUpdateConfigEntity(
            id = "primary_config",
            latestVersion = "1.6.0",
            latestVersionCode = 8,
            minimumSupportedVersion = "1.0.0",
            minimumSupportedVersionCode = 1,
            updateType = "OPTIONAL",
            updateUrl = "https://github.com/martial-arts/xma-school/releases/latest/download/xma_school_v1_6_0.apk",
            releaseNotes = "• Ask Gemini AI: Dedicated Martial Arts & Diet Coach for Kata, Kumite, Flexibility Drills & Nutrition\n• 100% Attendance Reward: Wireless Neckband FREE for students with 100% attendance\n• YouTube Training Player: Real thumbnails & dual-mode in-app and YouTube app playback\n• Student Profile Photo: Upload & update martial arts avatar directly\n• Headquarters: Mega Sports Complex, Memco More, Dhanbad Dojo under Sensei Raj Sanchu",
            updateTitle = "XMA Official Release v1.6.0",
            updateMessage = "New Major Update! Ask Gemini AI Coach is live, plus 100% Attendance Wireless Neckband FREE reward!",
            publishedDate = "2026-09-21",
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
    )

    // Global update announcement
    dao.insertAnnouncement(
        AnnouncementEntity(
            title = "🚀 New App Update v1.6.0 Live!",
            message = "New features rolled out:\n1. Ask Gemini AI: Your personal Martial Arts & Diet Sensei.\n2. 100% Attendance Reward: Wireless Neckband FREE!\n3. YouTube Video Player enhancements with HD thumbnails.\n4. Student Profile Photo avatar update.",
            targetBatch = "ALL BATCHES",
            date = "2026-09-21",
            authorName = "Sensei Raj Sanchu"
        )
    )
}
