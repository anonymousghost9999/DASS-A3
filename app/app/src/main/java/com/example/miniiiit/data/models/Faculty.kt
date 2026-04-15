package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "faculty",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Faculty(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val employeeId: String,
    val department: String = "ACADEMICS",
    val joinDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
