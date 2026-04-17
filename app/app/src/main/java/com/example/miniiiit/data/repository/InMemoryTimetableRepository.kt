package com.example.miniiiit.data.repository

import com.example.miniiiit.data.models.User
import com.example.miniiiit.data.models.UserRole

data class TimetableSlot(
    val id: Int,
    val dayLabel: String,
    val startTime: String,
    val endTime: String,
)

data class TimetableEntryView(
    val id: Int,
    val slotId: Int,
    val batch: String,
    val courseCode: String,
    val courseName: String,
    val facultyUsername: String,
    val facultyName: String,
    val room: String,
)

data class TimetableStudentNote(
    val username: String,
    val batch: String,
    val slotId: Int,
    val note: String,
)

data class TimetableValidationResult(
    val isAllowed: Boolean,
    val message: String,
)

class InMemoryTimetableRepository(
    private val authDataSource: InMemoryAuthDataSource,
) {
    companion object {
        val ALLOWED_ROOMS = listOf(
            "H-101", "H-102", "H-103", "H-104", "H-105",
            "H-201", "H-202", "H-203", "H-204", "H-205",
            "H-301", "H-302", "H-303", "H-304",
            "SH-1", "SH-2", "SH-3", "CR-1",
        )
    }

    private data class TimetableEntry(
        val id: Int,
        val slotId: Int,
        val batch: String,
        val courseCode: String,
        val facultyUsername: String,
        val room: String,
    )

    data class TimetableDraft(
        val slotId: Int,
        val batch: String,
        val courseCode: String,
        val facultyUsername: String,
        val room: String,
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

    private val subjectWeeklyLimit = 4
    private val facultyWeeklyLoadLimit = 16

    private val slots = listOf(
    // Monday
    TimetableSlot(1, "Mon", "08:30", "09:55"),
    TimetableSlot(2, "Mon", "10:05", "11:30"),
    TimetableSlot(3, "Mon", "11:40", "13:05"),
    TimetableSlot(4, "Mon", "14:00", "15:25"),
    TimetableSlot(5, "Mon", "15:35", "17:00"),
    TimetableSlot(6, "Mon", "17:00", "18:25"),

    // Tuesday
    TimetableSlot(7, "Tue", "08:30", "09:55"),
    TimetableSlot(8, "Tue", "10:05", "11:30"),
    TimetableSlot(9, "Tue", "11:40", "13:05"),
    TimetableSlot(10, "Tue", "14:00", "15:25"),
    TimetableSlot(11, "Tue", "15:35", "17:00"),
    TimetableSlot(12, "Tue", "17:00", "18:25"),

    // Wednesday
    TimetableSlot(13, "Wed", "08:30", "09:55"),
    TimetableSlot(14, "Wed", "10:05", "11:30"),
    TimetableSlot(15, "Wed", "11:40", "13:05"),
    TimetableSlot(16, "Wed", "17:00", "18:25"),

    // Thursday
    TimetableSlot(17, "Thu", "08:30", "09:55"),
    TimetableSlot(18, "Thu", "10:05", "11:30"),
    TimetableSlot(19, "Thu", "11:40", "13:05"),
    TimetableSlot(20, "Thu", "14:00", "15:25"),
    TimetableSlot(21, "Thu", "15:35", "17:00"),
    TimetableSlot(22, "Thu", "17:00", "18:25"),

    // Friday
    TimetableSlot(23, "Fri", "08:30", "09:55"),
    TimetableSlot(24, "Fri", "10:05", "11:30"),
    TimetableSlot(25, "Fri", "11:40", "13:05"),
    TimetableSlot(26, "Fri", "14:00", "15:25"),
    TimetableSlot(27, "Fri", "15:35", "17:00"),
    TimetableSlot(28, "Fri", "17:00", "18:25"),

    // Saturday
    TimetableSlot(29, "Sat", "08:30", "09:55"),
    TimetableSlot(30, "Sat", "10:05", "11:30"),
    TimetableSlot(31, "Sat", "11:40", "13:05"),
    TimetableSlot(32, "Sat", "17:00", "18:25"),
)
    private val entries = mutableListOf<TimetableEntry>()
    private val notes = mutableListOf<TimetableStudentNote>()
    private var nextEntryId = 1

    init {
        seedDefaultSchedule()
    }

    fun getBatches(): List<String> = listOf("CSE-A", "CSE-B")

    fun getSlots(): List<TimetableSlot> = slots

    fun getStudentBatch(username: String): String {
        return studentBatchMap[username] ?: "CSE-A"
    }

    fun getCourseAssignments(): List<CourseAssignment> {
        return authDataSource.getAllCourseAssignments().sortedBy { it.code }
    }

    fun getAllowedRooms(): List<String> = ALLOWED_ROOMS

    fun getEntriesForBatch(batch: String): List<TimetableEntryView> {
        return entries
            .filter { it.batch == batch }
            .map { it.toView() }
            .sortedBy { it.slotId }
    }

    fun getEntriesForFaculty(username: String): List<TimetableEntryView> {
        return entries
            .filter { it.facultyUsername == username }
            .map { it.toView() }
            .sortedBy { it.slotId }
    }

    fun getStudentNotes(username: String, batch: String): List<TimetableStudentNote> {
        return notes.filter { it.username == username && it.batch == batch }
    }

    fun getVisibleEntriesForUser(user: User, selectedBatch: String): List<TimetableEntryView> {
        return when (user.role) {
            UserRole.STUDENT -> getEntriesForBatch(getStudentBatch(user.username))
            else -> getEntriesForBatch(selectedBatch)
        }
    }

    fun canManageOfficialTimetable(user: User): Boolean {
        return user.role == UserRole.ADMIN || user.role == UserRole.FACULTY
    }

    private fun canManageDraft(user: User, draft: TimetableDraft): Boolean {
        return when (user.role) {
            UserRole.ADMIN -> true
            UserRole.FACULTY -> draft.facultyUsername == user.username
            else -> false
        }
    }

    private fun canManageEntry(user: User, entry: TimetableEntry): Boolean {
        return when (user.role) {
            UserRole.ADMIN -> true
            UserRole.FACULTY -> entry.facultyUsername == user.username
            else -> false
        }
    }

    fun validateDraft(
        draft: TimetableDraft,
        ignoreEntryId: Int? = null,
    ): TimetableValidationResult {
        if (!ALLOWED_ROOMS.contains(draft.room)) {
            return TimetableValidationResult(
                isAllowed = false,
                message = "Invalid room. Select one of the allowed room numbers.",
            )
        }

        val slotConflict = entries.firstOrNull {
            it.id != ignoreEntryId &&
                it.batch == draft.batch &&
                it.slotId == draft.slotId
        }
        if (slotConflict != null) {
            return TimetableValidationResult(
                isAllowed = false,
                message = "Collision: ${draft.batch} already has ${slotConflict.courseCode} in this slot.",
            )
        }

        val facultyConflict = entries.firstOrNull {
            it.id != ignoreEntryId &&
                it.facultyUsername == draft.facultyUsername &&
                it.slotId == draft.slotId
        }
        if (facultyConflict != null) {
            return TimetableValidationResult(
                isAllowed = false,
                message = "Collision: faculty already assigned in this slot.",
            )
        }

        val subjectCount = entries.count {
            it.id != ignoreEntryId &&
                it.batch == draft.batch &&
                it.courseCode.equals(draft.courseCode, ignoreCase = true)
        }
        if (subjectCount + 1 > subjectWeeklyLimit) {
            return TimetableValidationResult(
                isAllowed = false,
                message = "Subject weekly limit exceeded for ${draft.courseCode}.",
            )
        }

        val facultyLoad = entries.count {
            it.id != ignoreEntryId && it.facultyUsername == draft.facultyUsername
        }
        if (facultyLoad + 1 > facultyWeeklyLoadLimit) {
            return TimetableValidationResult(
                isAllowed = false,
                message = "Faculty weekly workload limit exceeded.",
            )
        }

        return TimetableValidationResult(
            isAllowed = true,
            message = "Ready to assign.",
        )
    }

    fun createOrReplaceEntry(user: User, draft: TimetableDraft): TimetableValidationResult {
        if (!canManageOfficialTimetable(user)) {
            return TimetableValidationResult(false, "Access denied for official timetable edits.")
        }
        if (!canManageDraft(user, draft)) {
            return TimetableValidationResult(false, "Faculty can assign only their own courses.")
        }

        val validation = validateDraft(draft)
        if (!validation.isAllowed) return validation

        entries.removeAll { it.batch == draft.batch && it.slotId == draft.slotId }
        entries.add(
            TimetableEntry(
                id = nextEntryId++,
                slotId = draft.slotId,
                batch = draft.batch,
                courseCode = draft.courseCode,
                facultyUsername = draft.facultyUsername,
                room = draft.room,
            ),
        )

        return TimetableValidationResult(true, "Class assigned successfully.")
    }

    fun moveEntry(user: User, entryId: Int, targetSlotId: Int): TimetableValidationResult {
        if (!canManageOfficialTimetable(user)) {
            return TimetableValidationResult(false, "Access denied for official timetable edits.")
        }

        val current = entries.firstOrNull { it.id == entryId }
            ?: return TimetableValidationResult(false, "Selected class not found.")
        if (!canManageEntry(user, current)) {
            return TimetableValidationResult(false, "Faculty can move only their own classes.")
        }

        val draft = TimetableDraft(
            slotId = targetSlotId,
            batch = current.batch,
            courseCode = current.courseCode,
            facultyUsername = current.facultyUsername,
            room = current.room,
        )

        val validation = validateDraft(draft, ignoreEntryId = current.id)
        if (!validation.isAllowed) return validation

        entries.removeAll { it.id == current.id }
        entries.add(current.copy(slotId = targetSlotId))
        return TimetableValidationResult(true, "Class moved successfully.")
    }

    fun deleteEntry(user: User, entryId: Int): TimetableValidationResult {
        if (!canManageOfficialTimetable(user)) {
            return TimetableValidationResult(false, "Access denied for official timetable edits.")
        }

        val existing = entries.firstOrNull { it.id == entryId }
            ?: return TimetableValidationResult(false, "Class not found.")
        if (!canManageEntry(user, existing)) {
            return TimetableValidationResult(false, "Faculty can delete only their own classes.")
        }

        val removed = entries.removeIf { it.id == entryId }
        return if (removed) {
            TimetableValidationResult(true, "Class deleted successfully.")
        } else {
            TimetableValidationResult(false, "Class not found.")
        }
    }

    fun saveStudentNote(user: User, slotId: Int, noteText: String): TimetableValidationResult {
        if (user.role != UserRole.STUDENT) {
            return TimetableValidationResult(false, "Only students can add personal slot notes.")
        }

        val cleaned = noteText.trim()
        if (cleaned.isBlank()) {
            return TimetableValidationResult(false, "Note cannot be empty.")
        }

        val batch = getStudentBatch(user.username)
        notes.removeAll { it.username == user.username && it.batch == batch && it.slotId == slotId }
        notes.add(
            TimetableStudentNote(
                username = user.username,
                batch = batch,
                slotId = slotId,
                note = cleaned,
            ),
        )
        return TimetableValidationResult(true, "Personal slot note saved.")
    }

    private fun TimetableEntry.toView(): TimetableEntryView {
        val course = authDataSource.getAllCourseAssignments().firstOrNull {
            it.code.equals(courseCode, ignoreCase = true)
        }
        val faculty = authDataSource.getUserByUsername(facultyUsername)

        return TimetableEntryView(
            id = id,
            slotId = slotId,
            batch = batch,
            courseCode = courseCode,
            courseName = course?.name ?: courseCode,
            facultyUsername = facultyUsername,
            facultyName = faculty?.fullName ?: facultyUsername,
            room = room,
        )
    }

    private fun seedDefaultSchedule() {
        if (entries.isNotEmpty()) return

        val defaults = listOf(
            TimetableDraft(1, "CSE-A", "DASS", "raghureddy", "H-301"),
            TimetableDraft(2, "CSE-A", "ML", "praveen", "H-303"),
            TimetableDraft(4, "CSE-A", "NA", "pawan", "H-302"),
            TimetableDraft(5, "CSE-A", "IHS", "aniket", "H-201"),
            TimetableDraft(7, "CSE-B", "DASS", "raghureddy", "H-301"),
            TimetableDraft(8, "CSE-B", "ML", "praveen", "H-303"),
            TimetableDraft(10, "CSE-B", "NA", "pawan", "H-302"),
            TimetableDraft(11, "CSE-B", "IHS", "aniket", "H-201"),
        )

        defaults.forEach { draft ->
            val validation = validateDraft(draft)
            if (validation.isAllowed) {
                entries.add(
                    TimetableEntry(
                        id = nextEntryId++,
                        slotId = draft.slotId,
                        batch = draft.batch,
                        courseCode = draft.courseCode,
                        facultyUsername = draft.facultyUsername,
                        room = draft.room,
                    ),
                )
            }
        }
    }
}
