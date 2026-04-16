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
        TimetableSlot(1, "Mon", "09:00", "10:00"),
        TimetableSlot(2, "Mon", "10:15", "11:15"),
        TimetableSlot(3, "Mon", "11:30", "12:30"),
        TimetableSlot(4, "Tue", "09:00", "10:00"),
        TimetableSlot(5, "Tue", "10:15", "11:15"),
        TimetableSlot(6, "Tue", "11:30", "12:30"),
        TimetableSlot(7, "Wed", "09:00", "10:00"),
        TimetableSlot(8, "Wed", "10:15", "11:15"),
        TimetableSlot(9, "Wed", "11:30", "12:30"),
        TimetableSlot(10, "Thu", "09:00", "10:00"),
        TimetableSlot(11, "Thu", "10:15", "11:15"),
        TimetableSlot(12, "Thu", "11:30", "12:30"),
        TimetableSlot(13, "Fri", "09:00", "10:00"),
        TimetableSlot(14, "Fri", "10:15", "11:15"),
        TimetableSlot(15, "Fri", "11:30", "12:30"),
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
            TimetableDraft(1, "CSE-A", "DASS", "raghureddy", "R-301"),
            TimetableDraft(2, "CSE-A", "ML", "praveen", "R-305"),
            TimetableDraft(4, "CSE-A", "NA", "pawan", "R-302"),
            TimetableDraft(5, "CSE-A", "IHS", "aniket", "R-201"),
            TimetableDraft(7, "CSE-B", "DASS", "raghureddy", "R-301"),
            TimetableDraft(8, "CSE-B", "ML", "praveen", "R-305"),
            TimetableDraft(10, "CSE-B", "NA", "pawan", "R-302"),
            TimetableDraft(11, "CSE-B", "IHS", "aniket", "R-201"),
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
