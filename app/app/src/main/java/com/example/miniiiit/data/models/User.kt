package com.example.miniiiit.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    ADMIN, FACULTY, STUDENT, PARENT, ALUMNI
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
