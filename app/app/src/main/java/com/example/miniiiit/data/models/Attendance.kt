package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class AttendanceStatus {
    PRESENT, ABSENT, ON_LEAVE
}

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: Int,
    val courseId: Int,
    val date: Long,
    val status: AttendanceStatus,
    val remarks: String = "",
    val markedBy: Int,
    val markedAt: Long = System.currentTimeMillis()
)
