package com.example.miniiiit.data.repository

import com.example.miniiiit.data.models.User
import com.example.miniiiit.data.models.UserRole

data class CourseAssignment(
    val code: String,
    val name: String,
    val facultyUsername: String,
)

class InMemoryAuthDataSource {
    private val studentUsernames = setOf(
        "dhiraj",
        "viswaj",
        "harsha",
        "phani",
        "kartikeya",
        "rasputin",
        "johndoe",
    )

    private val users: List<User> = listOf(
        User(id = 1, username = "admin", password = "1234", email = "admin@ims.local", fullName = "Administrator", role = UserRole.ADMIN),
        User(id = 2, username = "dhiraj", password = "1234", email = "dhiraj@ims.local", fullName = "Dhiraj", role = UserRole.STUDENT),
        User(id = 3, username = "viswaj", password = "1234", email = "viswaj@ims.local", fullName = "Viswaj", role = UserRole.STUDENT),
        User(id = 4, username = "harsha", password = "1234", email = "harsha@ims.local", fullName = "Harsha", role = UserRole.STUDENT),
        User(id = 5, username = "phani", password = "1234", email = "phani@ims.local", fullName = "Phani", role = UserRole.STUDENT),
        User(id = 6, username = "kartikeya", password = "1234", email = "kartikeya@ims.local", fullName = "Kartikeya", role = UserRole.STUDENT),
        User(id = 7, username = "rasputin", password = "1234", email = "rasputin@ims.local", fullName = "Rasputin", role = UserRole.STUDENT),
        User(id = 8, username = "johndoe", password = "1234", email = "johndoe@ims.local", fullName = "John Doe", role = UserRole.STUDENT),
        User(id = 9, username = "raghureddy", password = "1234", email = "raghureddy@ims.local", fullName = "Raghu Reddy", role = UserRole.FACULTY),
        User(id = 10, username = "praveen", password = "1234", email = "praveen@ims.local", fullName = "Praveen", role = UserRole.FACULTY),
        User(id = 11, username = "pawan", password = "1234", email = "pawan@ims.local", fullName = "Pawan", role = UserRole.FACULTY),
        User(id = 12, username = "aniket", password = "1234", email = "aniket@ims.local", fullName = "Aniket", role = UserRole.FACULTY),
        User(id = 13, username = "jawahar", password = "1234", email = "jawahar@ims.local", fullName = "Jawahar", role = UserRole.FACULTY),
    )

    private val courseAssignments: List<CourseAssignment> = listOf(
        CourseAssignment("DASS", "Design and Analysis of Software Systems", "raghureddy"),
        CourseAssignment("ML", "Machine Learning", "praveen"),
        CourseAssignment("NA", "Numerical Algorithms", "pawan"),
        CourseAssignment("IHS", "Intro to human sciences", "aniket"),
        CourseAssignment("SMAI", "SMAI", "jawahar"),
    )

    fun authenticate(username: String, password: String): User? {
        return users.firstOrNull { it.username == username && it.password == password }
    }

    fun getCoursesForStudent(username: String): List<CourseAssignment> {
        if (username !in studentUsernames) return emptyList()

        // Rule: DASS, ML, NA, IHS for all students; SMAI only for rasputin and johndoe.
        return courseAssignments.filter { assignment ->
            assignment.code != "SMAI" || username == "rasputin" || username == "johndoe"
        }
    }

    fun getCoursesForFaculty(username: String): List<CourseAssignment> {
        return courseAssignments.filter { it.facultyUsername == username }
    }

    fun getAllUsers(): List<User> = users
}
