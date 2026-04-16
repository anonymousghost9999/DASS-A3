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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.miniiiit.data.models.User
import com.example.miniiiit.data.repository.InMemoryAuthDataSource
import com.example.miniiiit.ui.theme.MiniIiitTheme

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
                    selectedFeature = it
                    currentScreen = AppScreen.DashboardStub
                },
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
        DashboardFeatureItem("Time Table", "Stub module", Icons.Default.CalendarMonth),
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
        DashboardFeatureItem("Multiple Dashboards", "Stub module", Icons.Default.Home),
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
                        "This is a dashboard stub page for $title. End-to-end logic intentionally not implemented.",
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

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MiniIiitTheme {
        MiniImsApp()
    }
}
