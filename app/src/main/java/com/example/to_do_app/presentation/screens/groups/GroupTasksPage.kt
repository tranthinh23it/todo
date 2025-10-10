package com.example.to_do_app.presentation.screens.groups

import BottomNavigation
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_app.R
import com.example.to_do_app.components.CategoryTopAppBar
import com.example.to_do_app.domain.Project
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.ProjectViewModel
import com.example.to_do_app.presentation.viewmodels.TeamViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun GroupTasksPage(
    navController: NavController,
    teamVM: TeamViewModel = viewModel(),
    authVM: AuthViewModel = viewModel(),
    projectVM: ProjectViewModel = viewModel()
) {
    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }

    val team by teamVM.selectedTeam.collectAsState()
    LaunchedEffect(String) {
        teamVM.getTeamById(currentUser?.team ?: "")
    }

    val projects by projectVM.projects.collectAsState()
    LaunchedEffect(String) {
        projectVM.getProjectsByTeamId(team?.id ?: "")
    }

    var fabOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedTab by remember { mutableStateOf(0) }
    var editingId by remember { mutableStateOf<String?>(null) }

    // Track ID của item mới được tạo
    var newlyCreatedId by remember { mutableStateOf<String?>(null) }

    // Auto-edit item mới ngay khi nó xuất hiện trong list
    LaunchedEffect(projects, newlyCreatedId) {
        newlyCreatedId?.let { newId ->
            // Kiểm tra item mới đã có trong projects chưa
            if (projects.any { it.id == newId }) {
                delay(100) // Delay nhẹ để UI update
                editingId = newId
                newlyCreatedId = null // Reset sau khi set
            }
        }
    }

    Scaffold(
        topBar = {
            CategoryTopAppBar(
                text = "Group Project",
                onBackClick = { navController.popBackStack() },
                iconPainter = painterResource(R.drawable.setting),
                onClick = { /* TODO */ }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color(0xFFF5F5F5),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newId = UUID.randomUUID().toString()
                    val newProject = Project(
                        id = newId,
                        name = "", // Empty name để trigger auto-edit
                        description = " ",
                        dateStart = LocalDateTime.now().toString(),
                        dateEnd = "",
                        priority = TaskPriority.MEDIUM,
                        status = TaskStatus.PENDING,
                        members = listOf(),
                        progress = 0f,
                        team = team?.id ?: ""
                    )
                    newlyCreatedId = newId // Set ID trước khi add
                    projectVM.addProject(newProject)
                },
                containerColor = Color(0xFF5B5EF4),
                modifier = Modifier
                    .offset { IntOffset(fabOffset.x.roundToInt(), fabOffset.y.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
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
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    GroupInfoSection()

                    FilterTabs(
                        selectedTabIndex = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val filterProjects = projects.filter { project ->
                        when (selectedTab) {
                            0 -> true
                            1 -> project.status == TaskStatus.PENDING
                            2 -> project.status == TaskStatus.COMPLETED
                            3 -> project.status == TaskStatus.OVERDUE
                            else -> true
                        }
                    }

                    GroupTasksList(
                        tasks = filterProjects,
                        navController = navController,
                        editingId = editingId,
                        onEditDone = { editingId = null },
                        onRequestEdit = { id -> editingId = id }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupTasksList(
    tasks: List<Project>,
    navController: NavController,
    editingId: String?,
    onEditDone: () -> Unit,
    onRequestEdit: (String) -> Unit,
    projectVM: ProjectViewModel = viewModel()
) {
    // FIX: Thêm LazyListState để control scroll
    val listState = rememberLazyListState()

    // Scroll đến item đang edit với smooth animation
    LaunchedEffect(editingId) {
        editingId?.let { id ->
            val index = tasks.indexOfFirst { it.id == id }
            if (index >= 0) {
                delay(150) // Delay để LazyColumn update
                listState.animateScrollToItem(
                    index = index,
                    scrollOffset = -100 // Scroll thêm một chút để item không bị che
                )
            }
        }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { project ->
            GroupTaskItem(
                project = project,
                isEditable = editingId == project.id,
                onNameChange = { newName ->
                    projectVM.updateProject(project.copy(name = newName))
                },
                onFinishEditing = onEditDone,
                onClickListener = { onRequestEdit(project.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTaskItem(
    project: Project,
    isEditable: Boolean = false,
    onNameChange: (String) -> Unit = {},
    onFinishEditing: () -> Unit = {},
    onClickListener: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by rememberSaveable(project.id) { mutableStateOf(project.name) }

    // Sync text với project.name khi không edit
    LaunchedEffect(project.name, isEditable) {
        if (!isEditable) {
            text = project.name
        }
    }

    // FIX: Đơn giản hóa focus logic - tăng delay cho stable
    LaunchedEffect(isEditable) {
        if (isEditable) {
            text = project.name
            delay(400) // Tăng delay để scroll + render hoàn thành
            try {
                focusRequester.requestFocus()
                delay(150) // Delay trước khi show keyboard
                keyboardController?.show()
            } catch (e: Exception) {
                Log.e("GroupTaskItem", "Focus failed: ${e.message}")
            }
        } else {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    fun commitIfChanged() {
        val trimmed = text.trim()
        if (trimmed.isNotEmpty() && trimmed != project.name) {
            onNameChange(trimmed)
        }
        onFinishEditing()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(
                if (isEditable) {
                    Modifier // Không có gesture khi đang edit
                } else {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = { onClickListener() })
                    }
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (project.priority) {
                TaskPriority.HIGH -> Color(0xFFFFF0F0)
                TaskPriority.MEDIUM -> Color(0xFFFFF8E1)
                TaskPriority.LOW -> Color(0xFFE8F5E9)
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isEditable) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            singleLine = true,
                            placeholder = { Text("Enter project name") },
                            textStyle = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState ->
                                    if (!focusState.isFocused && text.trim() != project.name) {
                                        commitIfChanged()
                                    }
                                },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    commitIfChanged()
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Transparent,   // ẩn viền khi focus
                                unfocusedBorderColor = Color.Transparent, // ẩn viền khi không focus
                                disabledBorderColor = Color.Transparent,  // ẩn viền khi disabled
                                errorBorderColor = Color.Transparent      // ẩn viền khi lỗi
                            )
                        )
                    } else {
                        Text(
                            text = if (project.name.isBlank()) "Unnamed Project" else project.name,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (project.status) {
                                TaskStatus.COMPLETED -> Color(0xFFE6F7FF)
                                TaskStatus.IN_PROGRESS -> Color(0xFFE0F2F1)
                                TaskStatus.PENDING -> Color(0xFFFFF0F0)
                                TaskStatus.OVERDUE -> Color(0xFFFFEBEE)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (project.status) {
                            TaskStatus.COMPLETED -> "Completed"
                            TaskStatus.IN_PROGRESS -> "In Progress"
                            TaskStatus.PENDING -> "Pending"
                            TaskStatus.OVERDUE -> "Overdue"
                        },
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = when (project.status) {
                            TaskStatus.COMPLETED -> Color(0xFF0288D1)
                            TaskStatus.IN_PROGRESS -> Color(0xFF00897B)
                            TaskStatus.PENDING -> Color(0xFFFF6D00)
                            TaskStatus.OVERDUE -> Color(0xFFD32F2F)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = project.description,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (project.status == TaskStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = project.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF5B5EF4),
                    trackColor = Color(0xFFEEE6FF)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = project.dateEnd.ifEmpty { "No date" },
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        ),
                        color = Color.DarkGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 0 until minOf(project.members.size, 3)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = (-4 * i).dp)
                                .clip(CircleShape)
                                .background(memberColors[i % memberColors.size])
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = project.members[i].first().toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (project.members.size > 3) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = (-12).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9E9E9E))
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${project.members.size - 3}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupInfoSection(
    teamVM: TeamViewModel = viewModel(),
    authVM: AuthViewModel = viewModel(),
    projectVM: ProjectViewModel = viewModel()
) {
    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }
//    Log.d("CurrentUser", "currentUser: $currentUser")
    Log.d("GroupInfoSection", "currentUser: ${currentUser?.team}")
    val team by teamVM.selectedTeam.collectAsState()
    LaunchedEffect(String) {
        teamVM.getTeamById(currentUser?.team ?: "1XBGIW0NJZ8wKOeQvakx")
    }
    Log.d("GroupInfoSection", "team: $team")

    val members = team?.members?.size
//    Log.d("GroupInfoSection", "members: $members")

    LaunchedEffect(team?.id) {
        if (!team?.id.isNullOrEmpty()) {
            projectVM.getProjectsByTeamId(team!!.id)
        }
    }
    val projects by projectVM.projects.collectAsState()
    Log.d("GroupInfoSection", "projects: $projects")


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        // Group Icon
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5B5EF4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Group",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = team?.name ?: "Team Name",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))


            Text(
                text = "${members} members \n·${projects.size} active tasks",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 15.sp,
                    fontFamily = FontFamily((Font(R.font.monasan_regular)))
                ),
            )
        }

        // Members Preview
        Row {
            for (i in 0..2) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = (-8 * i).dp)
                        .clip(CircleShape)
                        .background(memberColors[i % memberColors.size])
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = memberInitials[i],
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
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
                    text = "+5",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FilterTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("All Tasks", "In Progress", "Completed", "Overdue")

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
        containerColor = Color(0xFFF5F5F5),
        contentColor = Color(0xFF5B5EF4),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 3.dp,
                color = Color(0xFF5B5EF4)
            )
        },
        modifier = Modifier.padding(start = 10.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                        ),
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

// Helper function to get category icon
@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Design" -> Icons.Default.Person
        "Development" -> Icons.Default.Delete
        "Marketing" -> Icons.Default.MailOutline
        "Research" -> Icons.Default.Search
        "Meeting" -> Icons.Default.Add
        else -> Icons.Default.AccountCircle
    }
}

// Enums for task status and priority


// Data class for Group Task
data class GroupTask(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val progress: Float = 0f,
    val dueDate: String,
    val assignedTo: List<String>,
    val commentsCount: Int
)

// Sample data
val memberInitials = listOf("JD", "MS", "AK")
val memberColors = listOf(
    Color(0xFF5B5EF4),
    Color(0xFFFF6D00),
    Color(0xFF43A047),
    Color(0xFFE91E63)
)

// Sample tasks
val sampleGroupTasks = listOf(
    GroupTask(
        id = "1",
        title = "Design User Interface for Dashboard",
        description = "Create wireframes and high-fidelity mockups for the main dashboard screen with all required components.",
        category = "Design",
        status = TaskStatus.IN_PROGRESS,
        priority = TaskPriority.HIGH,
        progress = 0.7f,
        dueDate = "May 15, 2023",
        assignedTo = listOf("John", "Sarah"),
        commentsCount = 8
    ),
    GroupTask(
        id = "2",
        title = "Implement API Integration",
        description = "Connect the app with backend services and implement data fetching and synchronization.",
        category = "Development",
        status = TaskStatus.PENDING,
        priority = TaskPriority.MEDIUM,
        dueDate = "May 20, 2023",
        assignedTo = listOf("Mike", "Anna", "David"),
        commentsCount = 3
    ),
    GroupTask(
        id = "3",
        title = "Fix Navigation Bug",
        description = "Resolve the issue with back navigation not working correctly in nested screens.",
        category = "Development",
        status = TaskStatus.COMPLETED,
        priority = TaskPriority.LOW,
        dueDate = "May 10, 2023",
        assignedTo = listOf("Mike"),
        commentsCount = 5
    ),
    GroupTask(
        id = "4",
        title = "Weekly Team Meeting",
        description = "Discuss project progress, blockers, and next steps with the entire development team.",
        category = "Meeting",
        status = TaskStatus.OVERDUE,
        priority = TaskPriority.HIGH,
        dueDate = "May 8, 2023",
        assignedTo = listOf("John", "Mike", "Sarah", "Anna", "David", "Lisa"),
        commentsCount = 0
    ),
    GroupTask(
        id = "5",
        title = "Create Marketing Materials",
        description = "Design and prepare promotional materials for the app launch including social media posts and press release.",
        category = "Marketing",
        status = TaskStatus.IN_PROGRESS,
        priority = TaskPriority.MEDIUM,
        progress = 0.3f,
        dueDate = "May 25, 2023",
        assignedTo = listOf("Sarah", "Lisa"),
        commentsCount = 2
    )
)

@Preview(showBackground = true)
@Composable
fun GroupProjectPagePreview() {
    To_do_appTheme {
//        GroupProjectPage()
    }
}
