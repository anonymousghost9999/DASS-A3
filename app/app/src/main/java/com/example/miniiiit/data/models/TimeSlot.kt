package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_slots")
data class TimeSlot(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: String,
    val endTime: String,
    val dayOfWeek: Int,
    val capacity: Int = 60,
    val createdAt: Long = System.currentTimeMillis()
)
