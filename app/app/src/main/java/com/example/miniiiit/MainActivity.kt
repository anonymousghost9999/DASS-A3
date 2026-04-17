package com.example.miniiiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miniiiit.data.models.AttendanceStatus
import com.example.miniiiit.data.models.User
import com.example.miniiiit.data.models.UserRole
import com.example.miniiiit.data.repository.CourseAssignment
import com.example.miniiiit.data.repository.InMemoryAttendanceRepository
import com.example.miniiiit.data.repository.InMemoryAuthDataSource
import com.example.miniiiit.data.repository.InMemoryTimetableRepository
import com.example.miniiiit.data.repository.TimetableEntryView
import com.example.miniiiit.ui.theme.MiniIiitTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val AppBg = Color(0xFFF8FAFC)
private val AppSurface = Color(0xFFFFFFFF)
private val AppSurfaceSoft = Color(0xFFEFF6FF)
private val AppPrimary = Color(0xFF2563EB)
private val AppPrimaryDark = Color(0xFF1D4ED8)
private val AppTextDark = Color(0xFF0F172A)
private val AppTextMuted = Color(0xFF64748B)
private val AppBorder = Color(0xFFDBEAFE)
private val AppError = Color(0xFFDC2626)

enum class AppScreen {
    Login,
    Dashboard,
    Attendance,
    TimeTable,
    DashboardStub,
}

data class DashboardFeatureItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
)

enum class DashboardTab {
    Dashboard,
    Modules,
    Settings,
}

enum class AttendanceTab {
    Mark,
    Reports,
}

enum class AttendanceReportType {
    Daily,
    Monthly,
    Subject,
}

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
    val attendanceRepository = remember { InMemoryAttendanceRepository(authDataSource) }
    val timetableRepository = remember { InMemoryTimetableRepository(authDataSource) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentScreen by remember { mutableStateOf(AppScreen.Login) }
    var selectedFeature by remember { mutableStateOf("") }

    when (currentScreen) {
        AppScreen.Login -> LoginScreen(
            modifier = modifier,
            authDataSource = authDataSource,
            onLoginSuccess = {
                currentUser = it
                currentScreen = AppScreen.Dashboard
            },
        )
        AppScreen.Dashboard -> currentUser?.let { user ->
            ModuleDashboardScreen(
                modifier = modifier,
                user = user,
                onOpenFeature = {
                    if (it == "Attendance") {
                        currentScreen = AppScreen.Attendance
                    } else if (it == "Time Table") {
                        currentScreen = AppScreen.TimeTable
                    } else {
                        selectedFeature = it
                        currentScreen = AppScreen.DashboardStub
                    }
                },
                onLogout = {
                    currentUser = null
                    currentScreen = AppScreen.Login
                },
            )
        }
        AppScreen.Attendance -> currentUser?.let { user ->
            AttendanceModuleScreen(
                modifier = modifier,
                user = user,
                attendanceRepository = attendanceRepository,
                onBack = { currentScreen = AppScreen.Dashboard },
                onLogout = {
                    currentUser = null
                    currentScreen = AppScreen.Login
                },
            )
        }
        AppScreen.TimeTable -> currentUser?.let { user ->
            TimeTableModuleScreen(
                modifier = modifier,
                user = user,
                timetableRepository = timetableRepository,
                onBack = { currentScreen = AppScreen.Dashboard },
                onLogout = {
                    currentUser = null
                    currentScreen = AppScreen.Login
                },
            )
        }
        AppScreen.DashboardStub -> currentUser?.let { user ->
            ModulePageScreen(
                modifier = modifier,
                user = user,
                title = selectedFeature,
                subtitle = "Dashboard feature stub",
                icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = AppPrimary) },
                onBack = { currentScreen = AppScreen.Dashboard },
                onLogout = {
                    currentUser = null
                    currentScreen = AppScreen.Login
                },
            )
        }
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
            .background(AppBg),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = AppPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("IMS Portal", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = AppTextDark)
                        Text("Sign in to continue", color = AppTextMuted, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.trim() },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AppTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AppTextMuted) },
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
                    Text("Login", color = Color.White)
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
fun ModuleDashboardScreen(
    modifier: Modifier = Modifier,
    user: User,
    onOpenFeature: (String) -> Unit,
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(DashboardTab.Dashboard) }

    val dashboardFeatures = listOf(
        DashboardFeatureItem("Latest News", "Show updates immediately after login", Icons.Default.Article),
    )

    val moduleFeatures = listOf(
        DashboardFeatureItem("Time Table", "Drag-drop scheduling with collision checks", Icons.Default.CalendarMonth),
        DashboardFeatureItem("Messaging", "Stub module", Icons.Default.Article),
        DashboardFeatureItem("Attendance", "Stub module", Icons.Default.Assignment),
        DashboardFeatureItem("Student Admission", "Stub module", Icons.Default.Person),
        DashboardFeatureItem("Courses and Batches", "Stub module", Icons.Default.MenuBook),
        DashboardFeatureItem("Examination", "Stub module", Icons.Default.Assignment),
        DashboardFeatureItem("Human Resources", "Stub module", Icons.Default.Groups),
        DashboardFeatureItem("User Management", "Stub module", Icons.Default.Person),
        DashboardFeatureItem("News Management", "Stub module", Icons.Default.Article),
        DashboardFeatureItem("Student Details", "Stub module", Icons.Default.Person),
        DashboardFeatureItem("Finance", "Stub module", Icons.Default.Settings),
    )

    val settingFeatures = listOf(
        DashboardFeatureItem("Settings", "Language, time zone, and currency (stub)", Icons.Default.Settings),
    )

    val activeFeatures = when (selectedTab) {
        DashboardTab.Dashboard -> dashboardFeatures
        DashboardTab.Modules -> moduleFeatures
        DashboardTab.Settings -> settingFeatures
    }

    val filteredFeatures = activeFeatures.filter {
        searchQuery.isBlank() ||
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.subtitle.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg),
        containerColor = AppBg,
        bottomBar = {
            NavigationBar(containerColor = AppSurface) {
                NavigationBarItem(
                    selected = selectedTab == DashboardTab.Dashboard,
                    onClick = { selectedTab = DashboardTab.Dashboard },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Dashboard") },
                )
                NavigationBarItem(
                    selected = selectedTab == DashboardTab.Modules,
                    onClick = { selectedTab = DashboardTab.Modules },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Modules") },
                )
                NavigationBarItem(
                    selected = selectedTab == DashboardTab.Settings,
                    onClick = { selectedTab = DashboardTab.Settings },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBg)
                .padding(innerPadding)
                .padding(14.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Dashboard", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = AppTextDark)
                        Text("Welcome, ${user.fullName}", color = AppTextMuted)
                    }
                    TextButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = AppPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Logout", color = AppPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search current tab") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppTextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = when (selectedTab) {
                    DashboardTab.Dashboard -> "Dashboard"
                    DashboardTab.Modules -> "Modules"
                    DashboardTab.Settings -> "Settings"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextDark,
            )

            Spacer(modifier = Modifier.height(10.dp))

            filteredFeatures.forEach { feature ->
                ModuleCard(
                    title = feature.title,
                    subtitle = feature.subtitle,
                    icon = feature.icon,
                    onClick = { onOpenFeature(feature.title) },
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (selectedTab == DashboardTab.Dashboard && searchQuery.isBlank()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("New semester registration opens Monday", color = AppTextDark, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Dashboard news feed stub", color = AppTextMuted, fontSize = 13.sp)
                    }
                }
            }

            if (filteredFeatures.isEmpty()) {
                Text("No feature found for this tab.", color = AppTextMuted)
            }
        }
    }
}

@Composable
fun ModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .background(AppSurfaceSoft, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = AppPrimary)
            }

            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = AppTextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = AppTextMuted, fontSize = 13.sp)
            }
            Text(
                text = "Open",
                color = AppPrimaryDark,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun ModulePageScreen(
    modifier: Modifier = Modifier,
    user: User,
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(14.dp),
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = AppPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", color = AppPrimary)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = AppPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Logout", color = AppPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(AppSurfaceSoft, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) { icon() }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppTextDark)
                        Text(subtitle, color = AppTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Logged in as ${user.username}", color = AppTextMuted)
                Spacer(modifier = Modifier.height(8.dp))

                if (title == "Settings") {
                    SettingsFormStub()
                } else {
                    Text(
                        "Not in assignment scope.",
                        color = AppTextDark,
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsFormStub() {
    var language by remember { mutableStateOf("English") }
    var timeZone by remember { mutableStateOf("Asia/Kolkata") }
    var currency by remember { mutableStateOf("INR") }

    Text("Basic Settings", fontWeight = FontWeight.SemiBold, color = AppTextDark)
    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = language,
        onValueChange = { language = it },
        label = { Text("Language") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = timeZone,
        onValueChange = { timeZone = it },
        label = { Text("Time Zone") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = currency,
        onValueChange = { currency = it },
        label = { Text("Currency") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    Spacer(modifier = Modifier.height(12.dp))

    Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
        Text("Save")
    }
}

@Composable
fun AttendanceModuleScreen(
    modifier: Modifier = Modifier,
    user: User,
    attendanceRepository: InMemoryAttendanceRepository,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val isStudent = user.role == UserRole.STUDENT
    var selectedTab by remember { mutableStateOf(if (isStudent) AttendanceTab.Reports else AttendanceTab.Mark) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(14.dp),
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = AppPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back", color = AppPrimary)
                }
                Text("Attendance", color = AppTextDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = AppPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Logout", color = AppPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isStudent) {
            TabRow(selectedTabIndex = 0) {
                Tab(
                    selected = true,
                    onClick = { selectedTab = AttendanceTab.Reports },
                    text = { Text("Reports") },
                )
            }
        } else {
            TabRow(selectedTabIndex = if (selectedTab == AttendanceTab.Mark) 0 else 1) {
                Tab(
                    selected = selectedTab == AttendanceTab.Mark,
                    onClick = { selectedTab = AttendanceTab.Mark },
                    text = { Text("Mark") },
                )
                Tab(
                    selected = selectedTab == AttendanceTab.Reports,
                    onClick = { selectedTab = AttendanceTab.Reports },
                    text = { Text("Reports") },
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            AttendanceTab.Mark -> {
                if (isStudent) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
                    ) {
                        Text(
                            text = "Students cannot mark attendance. Please use Reports to view your attendance.",
                            modifier = Modifier.padding(14.dp),
                            color = AppTextMuted,
                        )
                    }
                } else {
                    AttendanceMarkScreen(
                        user = user,
                        attendanceRepository = attendanceRepository,
                    )
                }
            }
            AttendanceTab.Reports -> AttendanceReportsScreen(
                user = user,
                attendanceRepository = attendanceRepository,
            )
        }
    }
}

@Composable
fun AttendanceMarkScreen(
    user: User,
    attendanceRepository: InMemoryAttendanceRepository,
) {
    val batches = remember { attendanceRepository.getBatches() }
    var selectedBatch by remember { mutableStateOf(batches.firstOrNull().orEmpty()) }
    var selectedCourse by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(formatDate(System.currentTimeMillis())) }
    var saveMessage by remember { mutableStateOf("") }

    val students = remember(selectedBatch) { attendanceRepository.getStudentsForBatch(selectedBatch) }
    val courses = remember(selectedBatch, user.username, user.role) {
        attendanceRepository.getCoursesForContext(selectedBatch, user.username, user.role)
    }

    if (selectedCourse.isBlank() && courses.isNotEmpty()) {
        selectedCourse = courses.first().code
    }

    val statusMap = remember(selectedBatch, selectedCourse, dateText) { mutableStateMapOf<String, AttendanceStatus>() }
    val remarksMap = remember(selectedBatch, selectedCourse, dateText) { mutableStateMapOf<String, String>() }

    val selectedDateMillis = parseDateToMillis(dateText)
    val existingRecords = remember(selectedBatch, selectedCourse, dateText, user.username, user.role) {
        if (selectedDateMillis == null || selectedCourse.isBlank() || selectedBatch.isBlank()) {
            emptyList()
        } else {
            attendanceRepository.getAttendanceForContext(
                viewer = user,
                batch = selectedBatch,
                courseCode = selectedCourse,
                dateMillis = selectedDateMillis,
            )
        }
    }

    LaunchedEffect(students, selectedBatch, selectedCourse, dateText, existingRecords) {
        statusMap.clear()
        remarksMap.clear()

        students.forEach { student ->
            statusMap[student.username] = AttendanceStatus.PRESENT
            remarksMap[student.username] = ""
        }

        existingRecords.forEach { record ->
            statusMap[record.username] = record.status
            remarksMap[record.username] = record.remarks
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Mark Attendance", color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Edit the current class record and save changes in one step.", color = AppTextMuted, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))

                AttendanceSectionLabel(text = "Batch")
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    batches.forEach { batch ->
                        SelectionPill(
                            text = batch,
                            selected = selectedBatch == batch,
                            onClick = { selectedBatch = batch },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AttendanceSectionLabel(text = "Course")
                Spacer(modifier = Modifier.height(6.dp))
                if (courses.isEmpty()) {
                    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft)) {
                        Text(
                            "No courses available for this context.",
                            modifier = Modifier.padding(12.dp),
                            color = AppTextMuted,
                            fontSize = 12.sp,
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        courses.forEach { course ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (selectedCourse == course.code) AppSurfaceSoft else AppSurface,
                                ),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(course.code, color = AppTextDark, fontWeight = FontWeight.SemiBold)
                                        Text(course.name, color = AppTextMuted, fontSize = 12.sp)
                                    }
                                    SelectionPill(
                                        text = if (selectedCourse == course.code) "Selected" else "Select",
                                        selected = selectedCourse == course.code,
                                        onClick = { selectedCourse = course.code },
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AttendanceSectionLabel(text = "Date")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectionPill(text = "Today", selected = false, onClick = { dateText = formatDate(System.currentTimeMillis()) })
                    SelectionPill(text = "Yesterday", selected = false, onClick = { dateText = formatDate(System.currentTimeMillis() - (24L * 60L * 60L * 1000L)) })
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (existingRecords.isNotEmpty()) {
                    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFEFF6FF))) {
                        Text(
                            "Editing existing attendance: ${existingRecords.size} records loaded",
                            modifier = Modifier.padding(12.dp),
                            color = AppPrimaryDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                } else {
                    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft)) {
                        Text(
                            "No saved record found for this batch, course, and date. You are creating a new entry.",
                            modifier = Modifier.padding(12.dp),
                            color = AppTextMuted,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Students",
            color = AppTextDark,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        students.forEach { student ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.fullName, color = AppTextDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(student.username, color = AppTextMuted, fontSize = 12.sp)
                        }
                        AttendanceStatusBadge(statusMap[student.username] ?: AttendanceStatus.PRESENT)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AttendanceStatusChip(
                            label = "P",
                            selected = statusMap[student.username] == AttendanceStatus.PRESENT,
                            onClick = { statusMap[student.username] = AttendanceStatus.PRESENT },
                        )
                        AttendanceStatusChip(
                            label = "A",
                            selected = statusMap[student.username] == AttendanceStatus.ABSENT,
                            onClick = { statusMap[student.username] = AttendanceStatus.ABSENT },
                        )
                        AttendanceStatusChip(
                            label = "LA",
                            selected = statusMap[student.username] == AttendanceStatus.LEAVE_APPLIED,
                            onClick = { statusMap[student.username] = AttendanceStatus.LEAVE_APPLIED },
                        )
                        AttendanceStatusChip(
                            label = "LP",
                            selected = statusMap[student.username] == AttendanceStatus.LEAVE_APPROVED,
                            onClick = { statusMap[student.username] = AttendanceStatus.LEAVE_APPROVED },
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = remarksMap[student.username].orEmpty(),
                        onValueChange = { remarksMap[student.username] = it },
                        label = { Text("Remark (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                val dateMillis = parseDateToMillis(dateText)
                if (dateMillis == null || selectedCourse.isBlank() || selectedBatch.isBlank()) {
                    saveMessage = "Please enter valid batch, course, and date."
                } else {
                    attendanceRepository.markAttendance(
                        markedBy = user.username,
                        batch = selectedBatch,
                        courseCode = selectedCourse,
                        dateMillis = dateMillis,
                        marks = students.map {
                            InMemoryAttendanceRepository.AttendanceMarkInput(
                                username = it.username,
                                status = statusMap[it.username] ?: AttendanceStatus.PRESENT,
                                remarks = remarksMap[it.username].orEmpty(),
                            )
                        },
                    )
                    saveMessage = if (existingRecords.isNotEmpty()) {
                        "Attendance updated and saved for ${students.size} students."
                    } else {
                        "Attendance saved for ${students.size} students."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (existingRecords.isNotEmpty()) "Update and Save" else "Save Attendance")
        }

        if (saveMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft)) {
                Text(
                    saveMessage,
                    modifier = Modifier.padding(12.dp),
                    color = AppPrimaryDark,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun AttendanceSectionLabel(text: String) {
    Text(text, color = AppTextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
fun SelectionPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (selected) AppPrimary else AppSurfaceSoft,
            contentColor = if (selected) Color.White else AppPrimary,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AttendanceStatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(38.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = when (label) {
                "P" -> if (selected) Color(0xFF059669) else Color(0xFFD1FAE5)
                "A" -> if (selected) Color(0xFFDC2626) else Color(0xFFFEE2E2)
                "LA" -> if (selected) Color(0xFFD97706) else Color(0xFFFEF3C7)
                else -> if (selected) Color(0xFF4C1D95) else Color(0xFFEDE9FE)
            },
            contentColor = when (label) {
                "P" -> if (selected) Color.White else Color(0xFF047857)
                "A" -> if (selected) Color.White else Color(0xFFB91C1C)
                "LA" -> if (selected) Color.White else Color(0xFF92400E)
                else -> if (selected) Color.White else Color(0xFF5B21B6)
            },
        ),
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun AttendanceReportsScreen(
    user: User,
    attendanceRepository: InMemoryAttendanceRepository,
) {
    if (user.role == UserRole.STUDENT) {
        StudentAttendanceReportScreen(user = user, attendanceRepository = attendanceRepository)
        return
    }

    var reportType by remember { mutableStateOf(AttendanceReportType.Daily) }
    var dateText by remember { mutableStateOf(formatDate(System.currentTimeMillis())) }
    var monthText by remember { mutableStateOf(formatMonth(System.currentTimeMillis())) }
    val availableCourseCodes = remember(user.username, user.role) {
        attendanceRepository.getAvailableCourseCodes(user)
    }
    var courseCode by remember { mutableStateOf(availableCourseCodes.firstOrNull().orEmpty()) }

    val reportRecords = remember(reportType, dateText, monthText, courseCode, user.username, user.role) {
        when (reportType) {
            AttendanceReportType.Daily -> {
                parseDateToMillis(dateText)?.let {
                    attendanceRepository.getDailyReport(user, it)
                } ?: emptyList()
            }
            AttendanceReportType.Monthly -> attendanceRepository.getMonthlyReport(user, monthText)
            AttendanceReportType.Subject -> attendanceRepository.getSubjectWiseReport(user, courseCode)
        }
    }

    val presentCount = reportRecords.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = reportRecords.count { it.status == AttendanceStatus.ABSENT }
    val leaveCount = reportRecords.count {
        it.status == AttendanceStatus.LEAVE_APPLIED || it.status == AttendanceStatus.LEAVE_APPROVED
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Attendance Reports", color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Filter and review attendance records", color = AppTextMuted, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReportTypeChip(
                        title = "Daily",
                        selected = reportType == AttendanceReportType.Daily,
                        onClick = { reportType = AttendanceReportType.Daily },
                    )
                    ReportTypeChip(
                        title = "Monthly",
                        selected = reportType == AttendanceReportType.Monthly,
                        onClick = { reportType = AttendanceReportType.Monthly },
                    )
                    ReportTypeChip(
                        title = "Subject",
                        selected = reportType == AttendanceReportType.Subject,
                        onClick = { reportType = AttendanceReportType.Subject },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (reportType) {
                    AttendanceReportType.Daily -> {
                        OutlinedTextField(
                            value = dateText,
                            onValueChange = { dateText = it },
                            label = { Text("Date (yyyy-MM-dd)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                    AttendanceReportType.Monthly -> {
                        OutlinedTextField(
                            value = monthText,
                            onValueChange = { monthText = it },
                            label = { Text("Month (yyyy-MM)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                    AttendanceReportType.Subject -> {
                        if (availableCourseCodes.isEmpty()) {
                            Text("No courses available for this user.", color = AppTextMuted)
                        } else {
                            Text("Select Course", color = AppTextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            availableCourseCodes.forEach { code ->
                                TextButton(
                                    onClick = { courseCode = code },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = if (courseCode == code) "* $code" else code,
                                        color = if (courseCode == code) AppPrimary else AppTextDark,
                                        fontWeight = if (courseCode == code) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Summary", color = AppTextDark, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttendanceMetricCard(modifier = Modifier.weight(1f), title = "Total", value = reportRecords.size.toString())
                    AttendanceMetricCard(modifier = Modifier.weight(1f), title = "Present", value = presentCount.toString())
                    AttendanceMetricCard(modifier = Modifier.weight(1f), title = "Absent", value = absentCount.toString())
                    AttendanceMetricCard(modifier = Modifier.weight(1f), title = "Leave", value = leaveCount.toString())
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (reportRecords.isEmpty()) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("No records found", color = AppTextDark, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Try a different date, month, or course filter.", color = AppTextMuted, fontSize = 12.sp)
                }
            }
        } else {
            Text(
                text = "Records",
                color = AppTextDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))

            reportRecords.forEach { record ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.studentFullName,
                                    color = AppTextDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                )
                                Text(
                                    text = record.username,
                                    color = AppTextMuted,
                                    fontSize = 12.sp,
                                )
                            }
                            AttendanceStatusBadge(record.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AttendanceInfoPill("${record.courseCode}")
                            AttendanceInfoPill(formatDate(record.dateMillis))
                        }

                        if (record.remarks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Remark",
                                color = AppTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = record.remarks,
                                color = AppTextDark,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportTypeChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (selected) AppPrimary else AppSurfaceSoft,
            contentColor = if (selected) Color.White else AppPrimary,
        ),
    ) {
        Text(title, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AttendanceMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = AppTextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = AppTextDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AttendanceInfoPill(text: String) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = AppPrimaryDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun AttendanceStatusBadge(status: AttendanceStatus) {
    val background = when (status) {
        AttendanceStatus.PRESENT -> Color(0xFFD1FAE5)
        AttendanceStatus.ABSENT -> Color(0xFFFEE2E2)
        AttendanceStatus.LEAVE_APPLIED -> Color(0xFFFEF3C7)
        AttendanceStatus.LEAVE_APPROVED -> Color(0xFFEDE9FE)
    }
    val textColor = when (status) {
        AttendanceStatus.PRESENT -> Color(0xFF047857)
        AttendanceStatus.ABSENT -> Color(0xFFB91C1C)
        AttendanceStatus.LEAVE_APPLIED -> Color(0xFF92400E)
        AttendanceStatus.LEAVE_APPROVED -> Color(0xFF5B21B6)
    }

    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = background)) {
        Text(
            text = status.name.replace('_', ' '),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun StudentAttendanceReportScreen(
    user: User,
    attendanceRepository: InMemoryAttendanceRepository,
) {
    var selectedCourseCode by remember { mutableStateOf<String?>(null) }
    val summary = remember(user.username) { attendanceRepository.getStudentCourseSummary(user.username) }
    val detailRecords = remember(user.username, selectedCourseCode) {
        selectedCourseCode?.let { attendanceRepository.getStudentCourseRecords(user.username, it) } ?: emptyList()
    }
    val selectedCourseName = summary.firstOrNull { it.courseCode == selectedCourseCode }?.courseName.orEmpty()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("My Attendance", color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tap a course for detailed attendance", color = AppTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))
                AttendanceInfoPill(text = "${formatMonth(System.currentTimeMillis())} Semester")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Course Name", modifier = Modifier.weight(1.8f), color = AppTextDark, fontWeight = FontWeight.SemiBold)
                    Text("Total", modifier = Modifier.weight(0.6f), color = AppTextMuted, fontWeight = FontWeight.SemiBold)
                    Text("Present", modifier = Modifier.weight(0.8f), color = AppTextMuted, fontWeight = FontWeight.SemiBold)
                    Text("Absent", modifier = Modifier.weight(0.7f), color = AppTextMuted, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                summary.forEach { item ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (selectedCourseCode == item.courseCode) AppSurfaceSoft else AppSurface,
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCourseCode = item.courseCode }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(item.courseName, modifier = Modifier.weight(1.8f), color = AppTextDark)
                            Text(item.total.toString(), modifier = Modifier.weight(0.6f), color = AppTextMuted)
                            Text(item.present.toString(), modifier = Modifier.weight(0.8f), color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                            Text(item.absent.toString(), modifier = Modifier.weight(0.7f), color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        if (selectedCourseCode != null) {
            Spacer(modifier = Modifier.height(10.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(selectedCourseName, color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(selectedCourseCode.orEmpty(), color = AppTextMuted, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LegendDot(Color(0xFF22C55E), "Present")
                        LegendDot(Color(0xFFEF4444), "Absent")
                        LegendDot(Color(0xFFF59E0B), "Leave (Applied)")
                        LegendDot(Color(0xFF4C1D95), "Leave (Approved)")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Date", modifier = Modifier.weight(1f), color = AppTextDark, fontWeight = FontWeight.SemiBold)
                        Text("Status", modifier = Modifier.weight(1f), color = AppTextDark, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    detailRecords.forEach { record ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(formatDate(record.dateMillis), modifier = Modifier.weight(1f), color = AppTextDark)
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    AttendanceStatusBadge(record.status)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .background(color, RoundedCornerShape(50))
                .padding(6.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = AppTextMuted, fontSize = 11.sp)
    }
}

@Composable
fun TimeTableModuleScreen(
    modifier: Modifier = Modifier,
    user: User,
    timetableRepository: InMemoryTimetableRepository,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val isStudent = user.role == UserRole.STUDENT
    val canManage = timetableRepository.canManageOfficialTimetable(user)

    val batches = remember { timetableRepository.getBatches() }
    val slots = remember { timetableRepository.getSlots() }
    val courses = remember { timetableRepository.getCourseAssignments() }
    val assignableCourses = remember(courses, user.role, user.username) {
        when (user.role) {
            UserRole.FACULTY -> courses.filter { it.facultyUsername == user.username }
            else -> courses
        }
    }
    val fixedStudentBatch = remember(user.username) { timetableRepository.getStudentBatch(user.username) }

    var selectedBatch by remember {
        mutableStateOf(if (isStudent) fixedStudentBatch else batches.firstOrNull().orEmpty())
    }
    var selectedCourseCode by remember { mutableStateOf(assignableCourses.firstOrNull()?.code.orEmpty()) }
    var room by remember { mutableStateOf("R-101") }
    var selectedMoveEntryId by remember { mutableStateOf<Int?>(null) }
    var statusMessage by remember { mutableStateOf("Drag-and-drop flow: pick class, then drop on a slot.") }
    var noteSlotId by remember { mutableStateOf(slots.firstOrNull()?.id ?: 1) }
    var noteText by remember { mutableStateOf("") }
    var refreshTick by remember { mutableStateOf(0) }

    if (selectedCourseCode.isBlank() && assignableCourses.isNotEmpty()) {
        selectedCourseCode = assignableCourses.first().code
    }

    if (selectedCourseCode.isNotBlank() && assignableCourses.none { it.code == selectedCourseCode }) {
        selectedCourseCode = assignableCourses.firstOrNull()?.code.orEmpty()
    }

    val effectiveBatch = if (isStudent) fixedStudentBatch else selectedBatch
    val visibleEntries = remember(effectiveBatch, refreshTick, user.username, user.role) {
        timetableRepository.getVisibleEntriesForUser(user, effectiveBatch)
    }
    val entriesBySlot = remember(visibleEntries) { visibleEntries.associateBy { it.slotId } }
    val selectedCourse = assignableCourses.firstOrNull { it.code == selectedCourseCode }
    val studentNotes = remember(user.username, effectiveBatch, refreshTick) {
        timetableRepository.getStudentNotes(user.username, effectiveBatch)
    }
    val studentNoteBySlot = remember(studentNotes) { studentNotes.associateBy { it.slotId } }

    LaunchedEffect(noteSlotId, refreshTick) {
        noteText = studentNoteBySlot[noteSlotId]?.note.orEmpty()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(14.dp),
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = AppPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back", color = AppPrimary)
                }
                Text("Time Table", color = AppTextDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = AppPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Logout", color = AppPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        if (canManage) "Scheduler Workspace" else "Weekly Schedule",
                        color = AppTextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (canManage) {
                            "Admin manages all classes. Faculty can assign, move, replace, and delete only their own classes."
                        } else {
                            "Student view is read-only for official schedule."
                        },
                        color = AppTextMuted,
                        fontSize = 12.sp,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Batch", color = AppTextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (isStudent) {
                        AttendanceInfoPill(text = effectiveBatch)
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            batches.forEach { batch ->
                                SelectionPill(
                                    text = batch,
                                    selected = selectedBatch == batch,
                                    onClick = { selectedBatch = batch },
                                )
                            }
                        }
                    }

                    if (canManage) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Drag Source (Pick Class)", color = AppTextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))

                        if (assignableCourses.isEmpty()) {
                            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft)) {
                                Text(
                                    text = "No assignable courses available for this faculty account.",
                                    modifier = Modifier.padding(10.dp),
                                    color = AppTextMuted,
                                    fontSize = 12.sp,
                                )
                            }
                        } else {
                            assignableCourses.forEach { course ->
                                TimetableCourseDragCard(
                                    course = course,
                                    selected = selectedCourseCode == course.code,
                                    onSelect = {
                                        selectedCourseCode = course.code
                                        selectedMoveEntryId = null
                                        statusMessage = "Picked ${course.code}. Drop into a free slot."
                                    },
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = room,
                            onValueChange = { room = it },
                            label = { Text("Room") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = AppSurfaceSoft)) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.DragIndicator, contentDescription = null, tint = AppPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(statusMessage, color = AppPrimaryDark, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            slots.forEach { slot ->
                val existing = entriesBySlot[slot.id]
                val preview = previewValidationForSlot(
                    canManage = canManage,
                    selectedMoveEntryId = selectedMoveEntryId,
                    existingEntries = visibleEntries,
                    selectedCourse = selectedCourse,
                    selectedBatch = effectiveBatch,
                    room = room,
                    slotId = slot.id,
                    timetableRepository = timetableRepository,
                )

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${slot.dayLabel} ${slot.startTime}-${slot.endTime}",
                                color = AppTextDark,
                                fontWeight = FontWeight.SemiBold,
                            )
                            AttendanceInfoPill(text = "Slot ${slot.id}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (existing == null) {
                            Text("No class assigned", color = AppTextMuted, fontSize = 12.sp)
                        } else {
                            TimetableEntrySummary(entry = existing)
                            if (canManage) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    SelectionPill(
                                        text = if (selectedMoveEntryId == existing.id) "Move selected" else "Move",
                                        selected = selectedMoveEntryId == existing.id,
                                        onClick = {
                                            selectedMoveEntryId = existing.id
                                            statusMessage = "Picked ${existing.courseCode} for move. Drop in target slot."
                                        },
                                    )
                                    TextButton(
                                        onClick = {
                                            val result = timetableRepository.deleteEntry(user, existing.id)
                                            statusMessage = result.message
                                            if (result.isAllowed) {
                                                if (selectedMoveEntryId == existing.id) {
                                                    selectedMoveEntryId = null
                                                }
                                                refreshTick += 1
                                            }
                                        },
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = AppError)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Delete", color = AppError)
                                    }
                                }
                            }
                        }

                        if (canManage) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = if (preview.isAllowed) "Drop target ready" else preview.message,
                                    color = if (preview.isAllowed) Color(0xFF047857) else AppError,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val moveEntryId = selectedMoveEntryId
                                        if (moveEntryId != null) {
                                            val result = timetableRepository.moveEntry(
                                                user = user,
                                                entryId = moveEntryId,
                                                targetSlotId = slot.id,
                                            )
                                            statusMessage = result.message
                                            if (result.isAllowed) {
                                                selectedMoveEntryId = null
                                                refreshTick += 1
                                            }
                                        } else {
                                            val selected = selectedCourse ?: return@Button
                                            val result = timetableRepository.createOrReplaceEntry(
                                                user = user,
                                                draft = InMemoryTimetableRepository.TimetableDraft(
                                                    slotId = slot.id,
                                                    batch = effectiveBatch,
                                                    courseCode = selected.code,
                                                    facultyUsername = selected.facultyUsername,
                                                    room = room,
                                                ),
                                            )
                                            statusMessage = result.message
                                            if (result.isAllowed) {
                                                refreshTick += 1
                                            }
                                        }
                                    },
                                    enabled = preview.isAllowed,
                                ) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (selectedMoveEntryId != null) "Drop Move" else "Drop Class")
                                }
                            }
                        }
                    }
                }
            }

            if (isStudent) {
                Spacer(modifier = Modifier.height(10.dp))

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = AppSurface),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Personal Slot Notes", color = AppTextDark, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Add reminders without modifying official timetable.", color = AppTextMuted, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            slots.take(5).forEach { slot ->
                                SelectionPill(
                                    text = "S${slot.id}",
                                    selected = noteSlotId == slot.id,
                                    onClick = { noteSlotId = slot.id },
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text("Note for slot $noteSlotId") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val result = timetableRepository.saveStudentNote(
                                    user = user,
                                    slotId = noteSlotId,
                                    noteText = noteText,
                                )
                                statusMessage = result.message
                                if (result.isAllowed) {
                                    refreshTick += 1
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Save Personal Note")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableCourseDragCard(
    course: CourseAssignment,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (selected) AppSurfaceSoft else AppSurface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${course.code} - ${course.name}", color = AppTextDark, fontWeight = FontWeight.SemiBold)
                Text("Faculty: ${course.facultyUsername}", color = AppTextMuted, fontSize = 12.sp)
            }
            AttendanceInfoPill(text = if (selected) "Dragging" else "Pick")
        }
    }
}

@Composable
private fun TimetableEntrySummary(entry: TimetableEntryView) {
    Column {
        Text("${entry.courseCode} - ${entry.courseName}", color = AppTextDark, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Batch: ${entry.batch}", color = AppTextMuted, fontSize = 12.sp)
        Text("Faculty: ${entry.facultyName}", color = AppTextMuted, fontSize = 12.sp)
        Text("Room: ${entry.room}", color = AppTextMuted, fontSize = 12.sp)
    }
}

private fun previewValidationForSlot(
    canManage: Boolean,
    selectedMoveEntryId: Int?,
    existingEntries: List<TimetableEntryView>,
    selectedCourse: CourseAssignment?,
    selectedBatch: String,
    room: String,
    slotId: Int,
    timetableRepository: InMemoryTimetableRepository,
) = when {
    !canManage -> com.example.miniiiit.data.repository.TimetableValidationResult(
        isAllowed = false,
        message = "Read-only role.",
    )
    selectedMoveEntryId != null -> {
        val source = existingEntries.firstOrNull { it.id == selectedMoveEntryId }
        if (source == null) {
            com.example.miniiiit.data.repository.TimetableValidationResult(false, "Select a valid class to move.")
        } else {
            timetableRepository.validateDraft(
                draft = InMemoryTimetableRepository.TimetableDraft(
                    slotId = slotId,
                    batch = source.batch,
                    courseCode = source.courseCode,
                    facultyUsername = source.facultyUsername,
                    room = source.room,
                ),
                ignoreEntryId = source.id,
            )
        }
    }
    selectedCourse == null -> com.example.miniiiit.data.repository.TimetableValidationResult(
        false,
        "Pick a class to drag.",
    )
    else -> timetableRepository.validateDraft(
        draft = InMemoryTimetableRepository.TimetableDraft(
            slotId = slotId,
            batch = selectedBatch,
            courseCode = selectedCourse.code,
            facultyUsername = selectedCourse.facultyUsername,
            room = room,
        ),
    )
}

private fun parseDateToMillis(text: String): Long? {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.isLenient = false
        formatter.parse(text)?.time
    } catch (_: Exception) {
        null
    }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(millis)
}

private fun formatMonth(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    return formatter.format(millis)
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MiniIiitTheme {
        MiniImsApp()
    }
}
