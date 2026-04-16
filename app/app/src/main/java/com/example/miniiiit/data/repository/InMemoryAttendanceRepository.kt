package com.example.miniiiit.data.repository

import com.example.miniiiit.data.models.AttendanceStatus
import com.example.miniiiit.data.models.User
import com.example.miniiiit.data.models.UserRole
import java.util.Calendar

data class AttendanceCourse(
    val code: String,
    val name: String,
)

data class AttendanceStudent(
    val username: String,
    val fullName: String,
    val batch: String,
)

data class AttendanceRecord(
    val username: String,
    val studentFullName: String,
    val batch: String,
    val courseCode: String,
    val dateMillis: Long,
    val status: AttendanceStatus,
    val remarks: String,
    val markedBy: String,
)

data class StudentCourseAttendanceSummary(
    val courseCode: String,
    val courseName: String,
    val total: Int,
    val present: Int,
    val absent: Int,
)

class InMemoryAttendanceRepository(
    private val authDataSource: InMemoryAuthDataSource,
) {
    data class AttendanceMarkInput(
        val username: String,
        val status: AttendanceStatus,
        val remarks: String,
    )

    private val studentBatchMap = mapOf(
        "dhiraj" to "CSE-A",
        "viswaj" to "CSE-A",
        "harsha" to "CSE-A",
        "phani" to "CSE-B",
        "kartikeya" to "CSE-B",
        "rasputin" to "CSE-B",
        "johndoe" to "CSE-B",
    )

    private val records = mutableListOf<AttendanceRecord>()

    init {
        seedDemoAttendance()
    }

    fun getBatches(): List<String> = listOf("CSE-A", "CSE-B")

    fun getStudentsForBatch(batch: String): List<AttendanceStudent> {
        val students = authDataSource.getAllUsers().filter { it.role == UserRole.STUDENT }
        return students
            .mapNotNull { user ->
                val userBatch = studentBatchMap[user.username] ?: return@mapNotNull null
                if (userBatch != batch) return@mapNotNull null
                AttendanceStudent(
                    username = user.username,
                    fullName = user.fullName,
                    batch = userBatch,
                )
            }
            .sortedBy { it.fullName }
    }

    fun getCoursesForContext(batch: String, username: String, role: UserRole): List<AttendanceCourse> {
        val mapped = when (role) {
            UserRole.FACULTY, UserRole.ADMIN -> {
                val studentUser = getStudentsForBatch(batch).firstOrNull()?.username
                if (studentUser == null) emptyList() else authDataSource.getCoursesForStudent(studentUser)
            }
            UserRole.STUDENT -> authDataSource.getCoursesForStudent(username)
            else -> emptyList()
        }

        return mapped
            .map { AttendanceCourse(it.code, it.name) }
            .distinctBy { it.code }
            .sortedBy { it.code }
    }

    fun markAttendance(
        markedBy: String,
        batch: String,
        courseCode: String,
        dateMillis: Long,
        marks: List<AttendanceMarkInput>,
    ) {
        val dayStart = startOfDay(dateMillis)
        val dayEnd = dayStart + DAY_IN_MILLIS

        records.removeAll {
            it.batch == batch &&
                it.courseCode.equals(courseCode, ignoreCase = true) &&
                it.dateMillis in dayStart until dayEnd
        }

        marks.forEach { mark ->
            val user = authDataSource.getAllUsers().firstOrNull { it.username == mark.username } ?: return@forEach
            val resolvedBatch = studentBatchMap[mark.username] ?: batch
            records.add(
                AttendanceRecord(
                    username = mark.username,
                    studentFullName = user.fullName,
                    batch = resolvedBatch,
                    courseCode = courseCode.uppercase(),
                    dateMillis = dayStart,
                    status = mark.status,
                    remarks = mark.remarks,
                    markedBy = markedBy,
                ),
            )
        }
    }

    fun getDailyReport(viewer: User, dateMillis: Long): List<AttendanceRecord> {
        val dayStart = startOfDay(dateMillis)
        val dayEnd = dayStart + DAY_IN_MILLIS
        return records
            .filter { it.dateMillis in dayStart until dayEnd }
            .filter { isVisibleTo(it, viewer) }
            .sortedWith(compareBy({ it.courseCode }, { it.studentFullName }))
    }

    fun getMonthlyReport(viewer: User, monthText: String): List<AttendanceRecord> {
        return records
            .filter { formatMonth(it.dateMillis) == monthText }
            .filter { isVisibleTo(it, viewer) }
            .sortedWith(compareBy({ it.dateMillis }, { it.courseCode }, { it.studentFullName }))
    }

    fun getSubjectWiseReport(viewer: User, courseCode: String): List<AttendanceRecord> {
        return records
            .filter { it.courseCode.equals(courseCode, ignoreCase = true) }
            .filter { isVisibleTo(it, viewer) }
            .sortedWith(compareBy({ it.dateMillis }, { it.studentFullName }))
    }

    fun getAttendanceForContext(
        viewer: User,
        batch: String,
        courseCode: String,
        dateMillis: Long,
    ): List<AttendanceRecord> {
        val dayStart = startOfDay(dateMillis)
        val dayEnd = dayStart + DAY_IN_MILLIS
        return records
            .filter {
                it.batch == batch &&
                    it.courseCode.equals(courseCode, ignoreCase = true) &&
                    it.dateMillis in dayStart until dayEnd
            }
            .filter { isVisibleTo(it, viewer) }
            .sortedBy { it.studentFullName }
    }

    fun getAvailableCourseCodes(viewer: User): List<String> {
        val codes = when (viewer.role) {
            UserRole.ADMIN, UserRole.FACULTY -> {
                val users = authDataSource.getAllUsers()
                val studentCodes = users
                    .filter { it.role == UserRole.STUDENT }
                    .flatMap { authDataSource.getCoursesForStudent(it.username) }
                    .map { it.code }
                studentCodes
            }
            UserRole.STUDENT -> authDataSource.getCoursesForStudent(viewer.username).map { it.code }
            else -> emptyList()
        }

        return codes.distinct().sorted()
    }

    fun getStudentCourseSummary(username: String): List<StudentCourseAttendanceSummary> {
        val courses = authDataSource.getCoursesForStudent(username)

        return courses.map { course ->
            val courseRecords = records.filter {
                it.username == username && it.courseCode.equals(course.code, ignoreCase = true)
            }
            StudentCourseAttendanceSummary(
                courseCode = course.code,
                courseName = course.name,
                total = courseRecords.size,
                present = courseRecords.count { it.status == AttendanceStatus.PRESENT },
                absent = courseRecords.count { it.status == AttendanceStatus.ABSENT },
            )
        }
    }

    fun getStudentCourseRecords(username: String, courseCode: String): List<AttendanceRecord> {
        return records
            .filter { it.username == username && it.courseCode.equals(courseCode, ignoreCase = true) }
            .sortedByDescending { it.dateMillis }
    }

    private fun isVisibleTo(record: AttendanceRecord, viewer: User): Boolean {
        return when (viewer.role) {
            UserRole.ADMIN, UserRole.FACULTY -> true
            UserRole.STUDENT -> record.username == viewer.username
            else -> false
        }
    }

    private fun startOfDay(millis: Long): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun formatMonth(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        return String.format("%04d-%02d", year, month)
    }

    private fun seedDemoAttendance() {
        if (records.isNotEmpty()) return

        val today = startOfDay(System.currentTimeMillis())
        val users = authDataSource.getAllUsers().filter { it.role == UserRole.STUDENT }

        users.forEach { student ->
            val batch = studentBatchMap[student.username] ?: "CSE-A"
            val courses = authDataSource.getCoursesForStudent(student.username)

            courses.forEachIndexed { courseIndex, course ->
                for (weekIndex in 0 until 8) {
                    val date = today - (weekIndex * 7L * DAY_IN_MILLIS)
                    val status = when ((weekIndex + courseIndex) % 7) {
                        0 -> AttendanceStatus.ABSENT
                        1 -> AttendanceStatus.LEAVE_APPLIED
                        2 -> AttendanceStatus.LEAVE_APPROVED
                        else -> AttendanceStatus.PRESENT
                    }

                    records.add(
                        AttendanceRecord(
                            username = student.username,
                            studentFullName = student.fullName,
                            batch = batch,
                            courseCode = course.code,
                            dateMillis = date,
                            status = status,
                            remarks = if (status == AttendanceStatus.LEAVE_APPLIED) "Leave request submitted" else if (status == AttendanceStatus.LEAVE_APPROVED) "Leave approved" else "",
                            markedBy = "admin",
                        ),
                    )
                }
            }
        }
    }

    companion object {
        private const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L
    }
}
