package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val courseCode: String,
    val credits: Int = 4,
    val facultyId: Int,
    val isElective: Boolean = false,
    val maxStudents: Int = 100
)
