package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
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
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val studentId: String,
    val batchId: Int,
    val enrollmentDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
