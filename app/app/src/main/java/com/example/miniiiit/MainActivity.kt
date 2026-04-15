package com.example.miniiiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miniiiit.data.models.User
import com.example.miniiiit.data.models.UserRole
import com.example.miniiiit.data.repository.InMemoryAuthDataSource
import com.example.miniiiit.ui.theme.MiniIiitTheme

private val AppBlue = Color(0xFF1D4ED8)
private val AppLightBlue = Color(0xFFEFF6FF)
private val AppTextDark = Color(0xFF0F172A)
private val AppTextMuted = Color(0xFF475569)
private val AppError = Color(0xFFB91C1C)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniIiitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MiniImsApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MiniImsApp(modifier: Modifier = Modifier) {
    val authDataSource = remember { InMemoryAuthDataSource() }
    var loggedInUser by remember { mutableStateOf<User?>(null) }

    if (loggedInUser == null) {
        LoginScreen(
            modifier = modifier,
            onLoginSuccess = { loggedInUser = it },
            authDataSource = authDataSource,
        )
    } else {
        DashboardScreen(
            modifier = modifier,
            authDataSource = authDataSource,
            user = loggedInUser!!,
            onLogout = { loggedInUser = null },
        )
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    authDataSource: InMemoryAuthDataSource,
    onLoginSuccess: (User) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppLightBlue),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = AppBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("IMS Portal", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = AppTextDark)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("Sign in to continue to your dashboard", color = AppTextMuted)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.trim() },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val user = authDataSource.authenticate(username, password)
                        if (user == null) {
                            loginError = "Invalid username or password"
                        } else {
                            loginError = null
                            onLoginSuccess(user)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Demo password for all accounts: 1234", color = AppTextMuted, fontSize = 13.sp)
                if (loginError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = loginError!!, color = AppError, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    user: User,
    authDataSource: InMemoryAuthDataSource,
    onLogout: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Welcome, ${user.fullName}", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = AppTextDark)
                    Text("${user.username} • ${user.role}", color = AppTextMuted)
                }
                TextButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = AppBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Logout", color = AppBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (user.role) {
            UserRole.ADMIN -> AdminDashboard(authDataSource)
            UserRole.FACULTY -> FacultyDashboard(user = user, authDataSource = authDataSource)
            UserRole.STUDENT -> StudentDashboard(user = user, authDataSource = authDataSource)
            else -> Text("No dashboard for this role")
        }
    }
}

@Composable
fun AdminDashboard(authDataSource: InMemoryAuthDataSource) {
    val allUsers = authDataSource.getAllUsers()
    val students = allUsers.count { it.role == UserRole.STUDENT }
    val faculty = allUsers.count { it.role == UserRole.FACULTY }

    Text("Admin Dashboard", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Total Users",
            value = allUsers.size.toString(),
            icon = { Icon(Icons.Default.Groups, contentDescription = null) },
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Students",
            value = students.toString(),
            icon = { Icon(Icons.Default.School, contentDescription = null) },
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Faculty",
            value = faculty.toString(),
            icon = { Icon(Icons.Default.Badge, contentDescription = null) },
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Role",
            value = "Admin",
            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
        )
    }
}

@Composable
fun FacultyDashboard(user: User, authDataSource: InMemoryAuthDataSource) {
    val courses = authDataSource.getCoursesForFaculty(user.username)

    Text("Faculty Dashboard", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    Spacer(modifier = Modifier.height(10.dp))

    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Assigned Courses", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            if (courses.isEmpty()) {
                Text("No courses mapped")
            } else {
                courses.forEach {
                    AssistChip(
                        onClick = {},
                        label = { Text("${it.code} • ${it.name}") },
                        leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, tint = AppBlue) },
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun StudentDashboard(user: User, authDataSource: InMemoryAuthDataSource) {
    val courses = authDataSource.getCoursesForStudent(user.username)

    Text("Student Dashboard", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    Spacer(modifier = Modifier.height(10.dp))

    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Enrolled Courses", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            courses.forEach {
                Text("${it.code} - ${it.name}")
                Text("Faculty: ${it.facultyUsername}", color = AppTextMuted, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: @Composable () -> Unit,
) {
    ElevatedCard(modifier = modifier, shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            icon()
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = AppTextDark)
            Text(title, color = AppTextMuted)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MiniIiitTheme {
        MiniImsApp()
    }
}