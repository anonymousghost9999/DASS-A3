package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batches")
data class Batch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val year: Int,
    val branch: String,
    val createdAt: Long = System.currentTimeMillis()
)
