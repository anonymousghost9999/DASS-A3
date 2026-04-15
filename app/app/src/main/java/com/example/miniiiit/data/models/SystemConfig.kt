package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_config")
data class SystemConfig(
    @PrimaryKey
    val id: Int = 1,
    val language: String = "ENGLISH",
    val country: String = "INDIA",
    val currency: String = "INR",
    val timezone: String = "IST",
    val gradingSystem: String = "GPA",
    val idGenerationFormat: String = "AUTO",
    val updatedAt: Long = System.currentTimeMillis()
)
