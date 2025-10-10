import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import com.example.to_do_app.R
import com.example.to_do_app.components.CustomInputField
import com.example.to_do_app.components.DateTimePickerField
import com.example.to_do_app.domain.Task
import com.example.to_do_app.domain.User
import com.example.to_do_app.presentation.screens.user.AvatarPickerIcon
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.ProjectViewModel
import com.example.to_do_app.presentation.viewmodels.TaskActivityViewModel
import com.example.to_do_app.presentation.viewmodels.TaskViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import java.time.LocalDateTime
import java.util.UUID
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.example.to_do_app.domain.Notification
import com.example.to_do_app.presentation.viewmodels.NotificationViewModel
import com.example.to_do_app.util.NotificationManager
import com.google.firebase.Timestamp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewTaskBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    projectId: String,
    taskType: Int,
    navController: NavController,
    projectVM: ProjectViewModel = viewModel(),
    taskVM: TaskViewModel = viewModel(),
    authVM: AuthViewModel = viewModel(),
    taskActivityViewModel: TaskActivityViewModel = viewModel(),
    notificationVM : NotificationViewModel = viewModel()
) {

    Log.d("CreateNewTask", "=== FUNCTION CALLED ===")
    Log.d("CreateNewTask", "showSheet: $showSheet, projectId: $projectId, taskType: $taskType")

    val sheetState = rememberModalBottomSheetState()
    val currentUser by authVM.currentUser.observeAsState()

    LaunchedEffect(Unit) {
        try {
            Log.d("CreateNewTask", "LaunchedEffect triggered - fetching current user")
            authVM.fetchAndSetCurrentUser()
            Log.d("CreateNewTask", "Current user fetch completed")
        } catch (e: Exception) {
            Log.e("CreateNewTask", "Error in LaunchedEffect: ${e.message}", e)
        }
    }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var startDate = remember { mutableStateOf(LocalDateTime.now()) }
    var endDate = remember { mutableStateOf(LocalDateTime.now()) }
    var userList = remember { mutableStateListOf<String>() }
    var description by remember { mutableStateOf("") }
    val selectedDateTime = remember { mutableStateOf(LocalDateTime.now()) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val selectedIds by savedStateHandle
        ?.getLiveData<ArrayList<String>>("selected_members_ids")
        ?.observeAsState()
        ?: remember { mutableStateOf<ArrayList<String>?>(null) }

    var listMembers by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(selectedIds) {
        selectedIds?.let { ids ->
            val users = ids.mapNotNull { id -> authVM.getUserById(id) }
            listMembers = users
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(modifier = Modifier.padding(start = 16.dp)) {
                    Box(
                        modifier = Modifier
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AsyncImage(
                            model = "https://example.com/profile-image.jpg",
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.pencil)
                        )
                        AvatarPickerIcon { uri ->
                            selectedImage = uri
                        }
                    }
                    Column {
                        Text(
                            text = "Title",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CustomInputField(
                            value = title,
                            onValueChange = { title = it },
                            label = "New Task",
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "From",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DateTimePickerField(
                            selectedDateTime = startDate.value,
                            onDateTimeSelected = { startDate.value = it },
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "To",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DateTimePickerField(
                            selectedDateTime = endDate.value,
                            onDateTimeSelected = { endDate.value = it },
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "Team Members",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.padding(start = 16.dp)) {
                        if (listMembers.isNotEmpty()) {
                            listMembers.forEach { user ->
                                AsyncImage(
                                    model = if (user.imgUrl != null) user.imgUrl else "https://example.com/profile-image.jpg", // Replace with actual image URL
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.pencil)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7B61FF))
                                .clickable {
                                    navController.navigate(
                                        Screens.AddTeamMembersPage.createRoute(
                                            projectId = projectId
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomInputField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description"
                    )
                }
                val listUserId = listMembers.map { it.userId }
                val taskStatusOnType = when (taskType) {
                    1 -> TaskStatus.PENDING
                    2 -> TaskStatus.IN_PROGRESS
                    3 -> TaskStatus.COMPLETED
                    else -> TaskStatus.OVERDUE
                }
                val randomPriority = Random.nextInt(1, 4)
                val taskPriorityRandom = when (randomPriority) {
                    1 -> TaskPriority.LOW
                    2 -> TaskPriority.MEDIUM
                    3 -> TaskPriority.HIGH
                    else -> TaskPriority.LOW
                }

                //                fun formatDate(state: MutableState<LocalDateTime>): String {
//                    val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.US)
//                    return state.value.format(formatter)
//                }
                fun formatDate(dateTime: MutableState<LocalDateTime>): String {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    return dateTime.value.format(formatter)  // Truy cập .value để lấy giá trị của MutableState
                }

                Button(
                    onClick = {
                        val newTask = Task(
                            id = "task_${UUID.randomUUID()}",
                            title = title,
                            description = description,
                            project = projectId,
                            assignee = listUserId,
                            creator = currentUser?.userId ?: "",
                            status = taskStatusOnType,
                            priority = taskPriorityRandom,
                            dateStart = formatDate(startDate),
                            dateDue = formatDate(endDate),
                            type = "Team"
                        )
                        taskVM.addTask(newTask)

                        listUserId.forEach { userId ->
                            val newNotification = Notification(
                                id = "notif_${UUID.randomUUID()}",
                                recipientId = userId,
                                type = "TASK_ASSIGNED",
                                message = "You have been assigned to a new task: ${newTask.title}",
                                time = Timestamp.now(),
                                seen = false,
                                projectId = projectId,
                                taskId = newTask.id,
                                activityId = newTask.id, // nếu có activity riêng, thay bằng activity.id
                                deeplink = "todoapp://projects/$projectId/tasks/${newTask.id}",
                                expireAt = null
                                // nếu Notification có trường recipient/userId thì thêm:
                                // recipientId = userId
                            )
                            notificationVM.addNotification(newNotification)
                            // notificationsRepo.addForUser(userId, newNotification)
                        }
                        // Create activity with automatic notifications
                        CoroutineScope(Dispatchers.Main).launch {
                            // Ensure we have users to notify
                            val usersToNotify = if (listUserId.isNotEmpty()) {
                                listUserId
                            } else {
                                // If no assignees, notify at least 2 test users
                                listOf(
                                    "uFn2a1izcMOmQ6V61tVqRraZm823",
                                    "YOpq7Gc2IMXtaN0AaqrylDtCzuP2"
                                )
                            }

                            // Add currentUser for testing (remove this in production)
                            val allUsersToNotify =
                                usersToNotify + listOf(currentUser?.userId ?: "")

                            Log.d("CreateNewTask", "=== DEBUG START ===")
                            Log.d("CreateNewTask", "Users to notify: $usersToNotify")
                            Log.d(
                                "CreateNewTask",
                                "All users to notify (including currentUser): $allUsersToNotify"
                            )
                            Log.d(
                                "CreateNewTask",
                                "Current user: ${currentUser?.name} (${currentUser?.userId})"
                            )
                            Log.d("CreateNewTask", "=== DEBUG END ===")

                            val result = NotificationManager.createActivityWithNotifications(
                                projectId = projectId,
                                taskId = newTask.id,
                                action = "TASK_CREATED",
                                note = "${currentUser?.name ?: ""} has created a new task: ${newTask.title}",
                                worker = currentUser?.userId ?: "",
                                additionalUserIds = allUsersToNotify
                            )

                            if (result.isSuccess) {
                                Log.d(
                                    "CreateNewTask",
                                    "Activity created with notifications for ${usersToNotify.size} users"
                                )
                            } else {
                                Log.e(
                                    "CreateNewTask",
                                    "Failed to create activity: ${result.exceptionOrNull()?.message}"
                                )
                            }
                        }

                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C63FF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "Create Task",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun prviw() {
    To_do_appTheme {
//        CreateNewTaskBottomSheet(
//            onDismiss = {},
//            showSheet = false
//        )
    }
}


fun formatDateToShortMonthDay(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
    return date.format(formatter)
}

fun formatDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime.format(formatter)
}


fun formatToMMMdd(dateString: String): String {
    val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")  // Định dạng đầu vào
    val formatterOutput =
        DateTimeFormatter.ofPattern("MMM-dd", java.util.Locale.ENGLISH)  // Định dạng đầu ra

    // Phân tích chuỗi thành LocalDateTime
    val dateTime = LocalDateTime.parse(dateString, formatterInput)

    // Chuyển đổi thành chuỗi theo định dạng MMM-dd
    return dateTime.format(formatterOutput)
}

fun formatDate2(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return dateTime.format(formatter)
}

fun formatDate1(dateString: String): String {
    val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")  // Định dạng đầu vào
    val formatterOutput = DateTimeFormatter.ofPattern("yyyy-MM-dd")  // Định dạng đầu ra

    // Phân tích chuỗi thành LocalDateTime
    val dateTime = LocalDateTime.parse(dateString, formatterInput)

    // Chuyển đổi thành chuỗi theo định dạng MMM-dd
    return dateTime.format(formatterOutput)
}

fun parseToLocalDateTime(datetime: String): LocalDateTime? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        LocalDateTime.parse(datetime, formatter)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}