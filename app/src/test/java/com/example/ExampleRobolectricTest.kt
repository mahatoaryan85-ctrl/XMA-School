package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.XmaDatabase
import com.example.data.repository.XmaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("XMA SCHOOL", appName)
  }

  @Test
  fun `authenticate Instructor Raj7007`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, XmaDatabase::class.java).build()
    val repo = XmaRepository(db.xmaDao())
    repo.ensureDefaultInstructors()
    val authResult = repo.authenticateInstructor("Raj7007", "7714")
    assertTrue(authResult.isSuccess)
    val instructor = authResult.getOrNull()
    assertEquals("Raj7007", instructor?.email)
    assertEquals("Sensei Raj Sanchu", instructor?.fullName)
    db.close()
  }

  @Test
  fun `authenticate all 7 new students with password Xma@123`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, XmaDatabase::class.java).build()
    val repo = XmaRepository(db.xmaDao())
    repo.ensureDefaultStudents()

    val studentIds = listOf(
      "Prachi01",
      "Ayushi02",
      "Riya03",
      "Tripti04",
      "Rajan05",
      "Piyush06",
      "Pawani07"
    )

    for (id in studentIds) {
      val result = repo.authenticateStudent(id, "Xma@123")
      assertTrue("Authentication failed for $id", result.isSuccess)
      val student = result.getOrNull()
      assertTrue(student != null)
      assertTrue(student?.fullName?.isNotBlank() == true)
    }

    db.close()
  }

  @Test
  fun `verify selfie attendance marking and bitmap generation`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, XmaDatabase::class.java).build()
    val repo = XmaRepository(db.xmaDao())
    repo.ensureDefaultStudents()

    // Test helper bitmap generation
    val bitmap = com.example.ui.components.createEmulatedSelfieBitmap("Prachi")
    assertTrue(bitmap.width > 0)
    assertTrue(bitmap.height > 0)

    // Mark attendance with selfie
    val attendanceResult = repo.markAttendanceWithSelfie(
      studentId = "Prachi01",
      studentName = "Prachi",
      selfieUri = "camerax_selfie_Prachi01_test"
    )
    assertTrue("Mark attendance failed", attendanceResult.isSuccess)
    val record = attendanceResult.getOrNull()
    assertEquals("Prachi01", record?.studentId)
    assertEquals("Present", record?.status)
    assertTrue("Verification score should be >= 90", (record?.verificationScore ?: 0) >= 90)
    assertTrue("Timestamp should be greater than zero", (record?.timestamp ?: 0L) > 0L)
    assertTrue("Remarks should not be empty", record?.remarks?.isNotEmpty() == true)

    // Verify retrieving list of records from Room database
    val dao = db.xmaDao()
    val count = dao.getAttendanceCountForStudent("Prachi01")
    assertTrue("Attendance count should be at least 1", count >= 1)

    db.close()
  }

  @Test
  fun `verify custom upi routing for Sensei Sambhu and Sensei Ashish`() {
    // Sensei Sambhu test
    val sambhuInfoByName = com.example.ui.components.UpiPaymentHelper.getInstructorUpiInfo("Sensei Sambhu", null, null)
    assertEquals("8539049019@ptsbi", sambhuInfoByName.upiId)

    val sambhuInfoByBranch = com.example.ui.components.UpiPaymentHelper.getInstructorUpiInfo(null, "Sindri Branch", "batch_sindri")
    assertEquals("8539049019@ptsbi", sambhuInfoByBranch.upiId)

    // Sensei Ashish test
    val ashishInfoByName = com.example.ui.components.UpiPaymentHelper.getInstructorUpiInfo("Sensei Ashish", null, null)
    assertEquals("9631284765@ybl", ashishInfoByName.upiId)

    val ashishInfoByBranch = com.example.ui.components.UpiPaymentHelper.getInstructorUpiInfo(null, "Apartment Branch", "batch_apartment")
    assertEquals("9631284765@ybl", ashishInfoByBranch.upiId)
  }
}

