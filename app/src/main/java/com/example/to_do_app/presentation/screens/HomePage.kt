package com.example.to_do_app.presentation.screens

import BottomNavigation
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.to_do_app.R
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.ProjectViewModel
import com.example.to_do_app.presentation.viewmodels.TaskViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens
import com.example.to_do_app.util.TaskStatus
import formatDate1
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomePage(
    navController: NavController,
    taskVM: TaskViewModel = viewModel(),
    projectVM: ProjectViewModel = viewModel(),
    userVM: AuthViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            BottomNavigation(navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add new task */ },
                containerColor = Color(0xFF5B5EF4)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)   // quan trọng: tránh đè bởi FAB/system bars
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 96.dp) // chừa chỗ cho FAB
        ) {
            item { WelcomeHeader() }
            item { QuickStatsRow(taskVM) }
            item { TodayTasksSection() }
            item { RecentProjectsSection() }
            item { QuickActionsSection(navController) }
            item { WeeklyProgressSection() }
            item {
                MyTeamSection(onViewTeamClick = {
                    navController.navigate(Screens.KanbanBoardPage.route)
                })
            }
        }
    }
}

@Composable
fun WelcomeHeader(
    userVM: AuthViewModel = viewModel()
) {
    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        val localTime = LocalDateTime.now()
        val greeting = when (localTime.hour) {
            in 0..11 -> "Good Morning! 👋"
            in 12..17 -> "Good Afternoon! ☀️"
            else -> "Good Evening! 🌙"
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ready to tackle your tasks today?",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_regular))
                    ),
                    color = Color.Gray
                )
            }

            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5B5EF4)),
                contentAlignment = Alignment.Center
            ) {
                if (currentUser?.imgUrl != null) {
                    AsyncImage(
                        model = currentUser?.imgUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(60.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

            }
        }
    }
}


@Composable
fun QuickStatsRow(
    taskVM: TaskViewModel,
    userVM: AuthViewModel = viewModel()
) {
    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }

    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByUserId(currentUser?.userId ?: "uFn2a1izcMOmQ6V61tVqRraZm823")
//        taskVM.getTasksByUserId( "uFn2a1izcMOmQ6V61tVqRraZm823")
    }
    Log.d("QuickStatsRow", "tasks=${tasks}")
    val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val filterTask = tasks.filter {
        formatDate1(it.dateStart) == today
    }

    val completedTasks = filterTask.filter { it.status == TaskStatus.COMPLETED }
    val inProgressTasks = filterTask.filter { it.status == TaskStatus.IN_PROGRESS }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Total Tasks",
            value = filterTask.size.toString(),
            icon = Icons.Default.Assignment,
            color = Color(0xFF5B5EF4),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Completed",
            value = completedTasks.size.toString(),
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF43A047),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "In Progress",
            value = inProgressTasks.size.toString(),
            icon = Icons.Default.Schedule,
            color = Color(0xFFFF6D00),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(start = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                color = Color.Black
            )
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TodayTasksSection(
    navController: NavController = rememberNavController(),
    taskVM: TaskViewModel = viewModel(),
    userVM: AuthViewModel = viewModel()
) {
    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }

    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByUserId(currentUser?.userId ?: "")
    }

    val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val filterTask = tasks.filter {
        formatDate1(it.dateStart) == today
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Tasks",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    )
                )
                TextButton(onClick = {
                    navController.navigate(Screens.MyTaskPage.route)
                }) {
                    Text(
                        text = "View All",
                        color = Color(0xFF5B5EF4),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filterTask.isNotEmpty()) {
                val randomTasks = filterTask.shuffled().take(3)
                randomTasks.forEach { task ->
                    TaskItem(
                        task.title,
                        task.priority.toString(),
                        getPriorityColor(task.priority.toString())
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.emptybox),
                        contentDescription = "Profile",
                        tint = Color.LightGray,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No tasks today!!",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        color = Color.LightGray
                    )
                }
            }


            // Sample tasks
//            TaskItem("Review project proposal", "High", Color(0xFFE91E63))
//            TaskItem("Team meeting at 2 PM", "Medium", Color(0xFFFF6D00))
//            TaskItem("Update documentation", "Low", Color(0xFF43A047))
        }
    }
}

fun getPriorityColor(priority: String): Color {
    return when (priority) {
        "HIGH" -> Color(0xFFE91E63)
        "MEDIUM" -> Color(0xFFFF6D00)
        "LOW" -> Color(0xFF43A047)
        else -> Color.Gray
    }
}

@Composable
fun TaskItem(title: String, priority: String, priorityColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(priorityColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                )
            )
            Text(
                text = priority,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                color = priorityColor
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "View task",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun RecentProjectsSection(
    projectVM: ProjectViewModel = viewModel(),
    userVM: AuthViewModel = viewModel()
) {
    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }

    val projects by projectVM.projects.collectAsState()
    LaunchedEffect(String) {
        projectVM.getProjectsByUserId(currentUser?.userId ?: "")
    }

    Column {
        Text(
            text = "Recent Projects",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        // Random 3 project và gán thêm màu ngẫu nhiên cho từng project
        val colors = listOf(
            Color(0xFF5B5EF4),
            Color(0xFFFF6D00),
            Color(0xFF43A047),
            Color(0xFFE91E63),
            Color(0xFF00BCD4),
            Color(0xFF9C27B0)
        )

        val randomProjectsWithColors = remember(projects) {
            projects.shuffled().take(3).map { project ->
                val color = colors.random()
                project to color
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(randomProjectsWithColors) { (project, color) ->
                ProjectCard(
                    name = project.name,
                    progress = project.progress,
                    color = color
                )
            }
        }
    }
}

@Composable
fun ProjectCard(name: String, progress: Float, color: Color) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = name,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}% Complete",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    text = "New Task",
                    color = Color(0xFF5B5EF4),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("newTask")
                    }
                )
                QuickActionButton(
                    icon = Icons.Default.Group,
                    text = "Team Chat",
                    color = Color(0xFF43A047),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screens.KanbanBoardPage.route)
                    }
                )
                QuickActionButton(
                    icon = Icons.Default.Assessment,
                    text = "Reports",
                    color = Color(0xFFFF6D00),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screens.ReportPage.route)
                    }
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = { /* TODO */ },
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                )
            )
        }
    }
}

private fun parseDateStart(s: String?): LocalDate? = runCatching {
    if (s.isNullOrBlank()) null else LocalDate.parse(s.trim().take(10)) // "yyyy-MM-dd"
}.getOrNull()

@Composable
fun WeeklyProgressSection(
    userVM: AuthViewModel = viewModel(),
    taskVM: TaskViewModel = viewModel()
) {
    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }
    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByUserId(currentUser?.userId ?: "")
    }

    // Tính tuần hiện tại: Mon..Sun
    val today = LocalDate.now()
    val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
    val endOfWeek = startOfWeek.plusDays(6)

    // Đếm task theo từng ngày trong tuần (Mon=0..Sun=6)
    val counts = IntArray(7)
    for (t in tasks) {
        val d = com.example.to_do_app.presentation.screens.task.parseDateStart(t.dateStart) ?: continue
        if (!d.isBefore(startOfWeek) && !d.isAfter(endOfWeek)) {
            val idx = d.dayOfWeek.value - 1 // MONDAY=1 -> 0
            counts[idx]++
        }
    }
    val maxCount = counts.maxOrNull() ?: 0
    val progress: List<Float> =
        if (maxCount == 0) List(7) { 0f } else counts.map { it.toFloat() / maxCount }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "This Week Progress",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Simple progress bars for each day
            val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
//            val progress = listOf(0.8f, 0.6f, 0.9f, 0.4f, 0.7f, 0.3f, 0.5f)

            weekDays.forEachIndexed { index, day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day,
                        modifier = Modifier.width(40.dp),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        )
                    )
                    LinearProgressIndicator(
                        progress = progress[index],
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp),
                        color = Color(0xFF5B5EF4),
                        trackColor = Color(0xFF5B5EF4).copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(progress[index] * 100).toInt()}%",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MyTeamSection(
    onViewTeamClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = {
            onViewTeamClick()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Team",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    )
                )
                TextButton(onClick = onViewTeamClick) {
                    Text(
                        text = "View Team",
                        color = Color(0xFF5B5EF4),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Team Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF5B5EF4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Team",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Development Team",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        )
                    )
                    Text(
                        text = "8 members • 12 active tasks",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        ),
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Team Members Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Members avatars
                Row {
                    val memberInitials = listOf("JD", "MS", "AK", "TW")
                    val memberColors = listOf(
                        Color(0xFF5B5EF4),
                        Color(0xFFFF6D00),
                        Color(0xFF43A047),
                        Color(0xFFE91E63)
                    )

                    memberInitials.forEachIndexed { index, initials ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = (-6 * index).dp)
                                .clip(CircleShape)
                                .background(memberColors[index])
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                color = Color.White,
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                                )
                            )
                        }
                    }

                    // More members indicator
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-24).dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9E9E9E))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+4",
                            color = Color.White,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            )
                        )
                    }
                }

                // Team stats
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Team Progress",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        ),
                        color = Color.Gray
                    )
                    Text(
                        text = "75%",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        color = Color(0xFF43A047)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    To_do_appTheme {
//        HomePage()
    }
}
