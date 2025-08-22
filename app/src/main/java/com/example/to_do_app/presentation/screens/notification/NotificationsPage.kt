package com.example.to_do_app.presentation.screens.notification

import BottomNavigation
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_app.R
import com.example.to_do_app.components.CategoryTopAppBar
import com.example.to_do_app.domain.Task
import com.example.to_do_app.domain.TaskActivity
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.TaskActivityViewModel
import com.example.to_do_app.presentation.viewmodels.TaskViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import kotlin.math.roundToInt

@Composable
fun NotificationsPage(
    navController: NavController,
    taskViewModel: TaskViewModel = viewModel(),
    taskActivityVM: TaskActivityViewModel = viewModel(),
    userVM: AuthViewModel = viewModel()
) {
    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }

    // Bắt đầu lắng nghe activity sau khi đã có currentUser
    LaunchedEffect(currentUser?.userId) {
        currentUser?.userId?.let { uid ->
            taskActivityVM.observeActivitiesByUserId(uid)
        }
    }

    val taskActivities by taskActivityVM.activities.collectAsState()

    LaunchedEffect(taskActivities) {
        Log.d("TaskActivities", "Có ${taskActivities.size} activity(s): $taskActivities")
    }
    var fabOffset by remember { mutableStateOf(Offset.Zero) }


    Scaffold(
        topBar = {
            CategoryTopAppBar(
                text = "Notification",
                onBackClick = {
                    navController.popBackStack()
                },
                iconPainter = painterResource(R.drawable.setting),
                onClick = {
                    taskViewModel.addTask(task = sampleTask)
                    taskActivityVM.addActivity(sampleTaskActivity)

                    //                    LaunchedEffect(String) {
                    //                        taskActivityViewModel.observeActivitiesByUserId(currentUser?.userId ?: "")
                    //                    }
                    Log.d("log ", "press button")
                }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = Color(0xFF5B5EF4),
                modifier = Modifier
                    .offset { IntOffset(fabOffset.x.roundToInt(), fabOffset.y.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // tránh propagate
                            fabOffset += dragAmount
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            //                .padding(horizontal = 16.dp)
        ) {
            // Notifications List
            NotificationsList()
        }
    }
}


@Composable
fun NotificationsList() {
    LazyColumn(
        //        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Today's notifications
        item {
            DateHeader(text = "Today")
        }

        items(todayNotifications) { notification ->
            NotificationItem(notification)
        }

        // Yesterday's notifications
        item {
            DateHeader(text = "Yesterday, Dec 19, 2024")
        }

        items(yesterdayNotifications) { notification ->
            NotificationItem(notification)
        }

        // Older notifications
        item {
            DateHeader(text = "Dec 18, 2024")
        }

        items(olderNotifications) { notification ->
            NotificationItem(notification)
        }
    }
}

@Composable
fun DateHeader(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 16.sp,
                color = Color.Gray
            ),
            modifier = Modifier.padding(end = 16.dp)
        )

        HorizontalDivider(
            color = Color(0xFFEEEEEE),
            thickness = 1.dp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun NotificationItem(notification: NotificationData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = notification.icon,
                contentDescription = null,
                tint = notification.iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Notification Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                    ),
                )

                if (notification.hasAlert) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = notification.alertEmoji,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification.description,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 14.sp,
                    color = Color.Gray
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification.time,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                ),
            )
        }

        // Notification Status (red dot or chevron)
        if (notification.isUnread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.rightarrow),
            contentDescription = "Details",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

// Data class for notifications
data class NotificationData(
    val title: String,
    val description: String,
    val time: String,
    val icon: ImageVector,
    val iconTint: Color,
    val isUnread: Boolean = false,
    val hasAlert: Boolean = false,
    val alertEmoji: String = ""
)

// Sample data
val todayNotifications = listOf(
    NotificationData(
        title = "New Security Update",
        description = "We've enhanced account protection. Update your security settings for improved security.",
        time = "09:25 AM",
        icon = Icons.Default.Info,
        iconTint = Color.Black,
        isUnread = true,
        hasAlert = true,
        alertEmoji = "⚠️"
    ),
    NotificationData(
        title = "Unusual Activity Detected",
        description = "We've noticed unusual activity on your account. Review transactions for your security.",
        time = "07:38 AM",
        icon = Icons.Default.Home,
        iconTint = Color.Black,
        isUnread = true,
        hasAlert = true,
        alertEmoji = "🚨"
    )
)

val yesterdayNotifications = listOf(
    NotificationData(
        title = "New Login from a Device",
        description = "Your account was accessed from a new device. Was this you?",
        time = "12:32 PM",
        icon = Icons.Default.Lock,
        iconTint = Color.Black,
        hasAlert = true,
        alertEmoji = "🔑"
    ),
    NotificationData(
        title = "Let's Get Back On Track",
        description = "Haven't logged in in a while? A fresh Pomodoro can restart your flow.",
        time = "10:15 AM",
        icon = Icons.Default.Refresh,
        iconTint = Color.Black,
        hasAlert = true,
        alertEmoji = "🎯"
    )
)

val olderNotifications = listOf(
    NotificationData(
        title = "Scheduled Maintenance",
        description = "Focusy will be undergoing maintenance on Dec 23, 2024, from 2 AM - 4 AM.",
        time = "4:25 PM",
        icon = Icons.Default.Settings,
        iconTint = Color.Black,
        hasAlert = true,
        alertEmoji = "⚙️"
    ),
    NotificationData(
        title = "Focusy Anniversary Gift",
        description = "Celebrate with us! We've credited a special reward to your account.",
        time = "10:09 AM",
        icon = Icons.Default.Star,
        iconTint = Color.Black,
        hasAlert = true,
        alertEmoji = "🎉"
    ),
    NotificationData(
        title = "Welcome to Focusy",
        description = "Get started with your productivity journey!",
        time = "9:00 AM",
        icon = Icons.Default.Star,
        iconTint = Color.Black,
        hasAlert = true,
        alertEmoji = "🎉"
    )
)

@Preview(showBackground = true)
@Composable
fun NotificationsPagePreview() {
    To_do_appTheme {
//        NotificationsPage()
    }
}


val sampleTask = Task(
    id = "task_002",
    title = "Thiết kế trang chủ",
    description = "Thiết kế giao diện và bố cục cho trang chủ của ứng dụng",
    project = "Website Quản lý công việc",
    assignee = listOf("user_123", "user_456"), // danh sách ID người được giao
    creator = "user_001", // ID người tạo
    status = TaskStatus.IN_PROGRESS,
    priority = TaskPriority.HIGH,
    dateStart = "2025-07-30",
    dateDue = "2025-08-05",
    tags = listOf("UI", "Frontend", "Quan trọng"),
    subTask = listOf(
        Task(
            id = "task_002_1",
            title = "Thiết kế Header",
            description = "Tạo phần đầu trang với logo và menu",
            project = "Website Quản lý công việc",
            assignee = listOf("user_123"),
            creator = "user_001",
            status = TaskStatus.PENDING,
            priority = TaskPriority.MEDIUM,
            dateStart = "2025-07-30",
            dateDue = "2025-08-01",
            tags = listOf("UI", "Header"),
            subTask = emptyList()
        )
    )
)

val sampleTaskActivity = TaskActivity(
    id = "activity_002",
    taskId = "task_002",
    notifiedUserIds = listOf("u123", "u456"),
    action = "Cập nhật trạng thái",
    timestamp = "2025-07-31T08:45:00",
    note = "Trạng thái task chuyển từ TODO sang IN_PROGRESS"
)