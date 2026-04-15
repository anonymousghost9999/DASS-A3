package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "timetable_entries",
    foreignKeys = [
        ForeignKey(
            entity = TimeSlot::class,
            parentColumns = ["id"],
            childColumns = ["timeSlotId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Batch::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TimeTableEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeSlotId: Int,
    val courseId: Int,
    val facultyId: Int,
    val batchId: Int,
    val room: String = "",
    val validFrom: Long,
    val validUntil: Long,
    val createdAt: Long = System.currentTimeMillis()
)
