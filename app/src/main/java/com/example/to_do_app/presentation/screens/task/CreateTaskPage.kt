import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.to_do_app.domain.TaskActivity
import com.example.to_do_app.domain.User
import com.example.to_do_app.presentation.screens.user.AvatarPickerIcon
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.ProjectViewModel
import com.example.to_do_app.presentation.viewmodels.TaskActivityViewModel
import com.example.to_do_app.presentation.viewmodels.TaskViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.NotificationManager
import com.example.to_do_app.util.Screens
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewTaskPersonal(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    projectId: String,
    taskType: Int,
    navController: NavController,
    projectVM: ProjectViewModel = viewModel(),
    taskVM: TaskViewModel = viewModel(),
    authVM: AuthViewModel = viewModel(),
    taskActivityViewModel: TaskActivityViewModel = viewModel()
) {
//    Log.d("task type ", taskType.toString())
    val sheetState = rememberModalBottomSheetState()
    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var startDate = remember { mutableStateOf(LocalDateTime.now()) }
    var endDate = remember { mutableStateOf(LocalDateTime.now()) }
    var userList = remember { mutableStateListOf<String>() }
    var description by remember { mutableStateOf("") }
    val selectedDateTime = remember { mutableStateOf(LocalDateTime.now()) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val selectedMembers =
        savedStateHandle?.getLiveData<List<User>>("selected_members")?.observeAsState()
    var listMembers by remember { mutableStateOf<List<User>>(emptyList()) }



//    LaunchedEffect(selectedMembers?.value) {
//        selectedMembers?.value?.let { newMembers ->
//            // Xử lý khi có dữ liệu được trả về
//            listMembers = newMembers // hoặc append, tùy bạn
//        }
//    }

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
                var reminderEnabled by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 200.dp)
                ) {
                    Text(
                        text = "Reminder",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f)) // đẩy nút sang phải

                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFF4444),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray
                        )
                    )
                }


//                Column {
//                    Text(
//                        text = "Team Members",
//                        style = MaterialTheme.typography.displayMedium.copy(
//                            fontSize = 16.sp,
//                            fontFamily = FontFamily(Font(R.font.monasan_sb))
//                        ),
//                        modifier = Modifier.padding(start = 16.dp)
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(modifier = Modifier.padding(start = 16.dp)) {
//                        if (listMembers.isNotEmpty()) {
//                            listMembers.forEach { user ->
//                                AsyncImage(
//                                    model = if (user.imgUrl != null) user.imgUrl else "https://example.com/profile-image.jpg", // Replace with actual image URL
//                                    contentDescription = "Profile Picture",
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .clip(CircleShape),
//                                    contentScale = ContentScale.Crop,
//                                    placeholder = painterResource(R.drawable.pencil)
//                                )
//                            }
//                        }
//
//                        Box(
//                            modifier = Modifier
//                                .size(40.dp)
//                                .clip(CircleShape)
//                                .background(Color(0xFF7B61FF))
//                                .clickable {
//                                    navController.navigate(
//                                        Screens.AddTeamMembersPage.createRoute(
//                                            projectId = projectId
//                                        )
//                                    )
//                                },
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Add,
//                                contentDescription = "Add",
//                                tint = Color.White
//                            )
//                        }
//                    }
//                }

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
//                val listUserId = listMembers.map { it.userId }
                var listUserId: MutableList<String> = ArrayList()
                listUserId.add(currentUser?.userId ?: "")

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
                            type = "Personal"
                        )
                        taskVM.addTask(newTask)

                        // Create activity with automatic notifications
                        CoroutineScope(Dispatchers.Main).launch {
                            val result = NotificationManager.createActivityWithNotifications(
                                projectId = projectId,
                                taskId = newTask.id,
                                action = "TASK_CREATED",
                                note = "${currentUser?.name ?: ""} has created a new task: ${newTask.title}",
                                worker = currentUser?.userId ?: "",
                                additionalUserIds = listOf(
                                    "uFn2a1izcMOmQ6V61tVqRraZm823".trim(),
                                    "YOpq7Gc2IMXtaN0AaqrylDtCzuP2".trim()
                                )
                                // additionalUserIds = listUserId  // Notify task assignees
                            )
                            
                            if (result.isSuccess) {
                                Log.d("CreateTask", "Activity created with notifications")
                            } else {
                                Log.e("CreateTask", "Failed to create activity: ${result.exceptionOrNull()?.message}")
                            }
                        }

                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF000000),
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


