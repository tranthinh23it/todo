package com.example.to_do_app.presentation.screens.groups

import BottomNavigation
import CreateNewTaskBottomSheet
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.filled.Assignment
//import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.draw.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.to_do_app.R
import com.example.to_do_app.components.DeleteTaskDialog2
import com.example.to_do_app.domain.Task
import com.example.to_do_app.domain.TaskActivity
import com.example.to_do_app.domain.TimeEvent
import com.example.to_do_app.domain.TimeLine
import com.example.to_do_app.domain.User
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.ProjectViewModel
import com.example.to_do_app.presentation.viewmodels.TaskActivityViewModel
import com.example.to_do_app.presentation.viewmodels.TaskViewModel
import com.example.to_do_app.presentation.viewmodels.TimeLineViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import formatDate
import formatDate2
import formatToMMMdd
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanBoardPage(
    navController: NavController,
) {
    val projectId = "twY60Loioxs0UoSTuaia"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            // AppBar / TopBar của bạn

        },
        bottomBar = {
            BottomNavigation(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: open create-task sheet */ },
                containerColor = Color(0xFF5B5EF4)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        }
        // Nếu cần Snackbars:
        // snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() }) }
    ) { innerPadding ->
        // Nội dung chính
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)   // ❗ phải dùng padding từ Scaffold
                .padding(16.dp)
        ) {
            KanbanTopBar(projectId)
            Spacer(modifier = Modifier.height(16.dp))
            KanbanTabRow(projectId)
        }
    }
}


@Composable
fun KanbanTopBar(
    projectId: String,
    projectViewModel: ProjectViewModel = viewModel()
) {
    val project by projectViewModel.selectedProject.collectAsState()
    LaunchedEffect(String) {
        projectViewModel.getProjectById(projectId)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF5B5EF4),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = project?.name ?: "Project Name",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE0F7FA))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = project?.status.toString(),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_regular))
                            ),
                            color = Color(0xFF00ACC1),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Team members
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..4) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-8 * i).dp)
                            .clip(CircleShape)
                            .background(kanbanMemberColors[i % kanbanMemberColors.size])
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kanbanMemberInitials[i % kanbanMemberInitials.size],
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = (-40).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9E9E9E))
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+3",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(40.dp))

                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF5B5EF4)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Member",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun KanbanTabRow(projectId: String) {
    var selectedTabIndex by remember { mutableStateOf(1) }
    val tabs = listOf("Overview", "List", "Board", "Timeline", "Calendar", "Dashboard")

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 3.dp,
                    color = Color.Black
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )
                    }
                )
            }
        }

        // Tab content
        when (selectedTabIndex) {
            0 -> OverviewTab(projectId ?: "")
            1 -> ListTab(projectId ?: "")
            2 -> BoardTab(projectId ?: "") // Kanban Board
            3 -> TimelineTab(projectId ?: "")
            4 -> CalendarTab()
            5 -> DashboardTab()
        }
    }
}

@Composable
fun BoardTab(
    projectId: String,
    projectVM: ProjectViewModel = viewModel(),
    taskVM: TaskViewModel = viewModel(),
    taskActivityVM: TaskActivityViewModel = viewModel(),
    authVM: AuthViewModel = viewModel(),
) {
    val project by projectVM.selectedProject.collectAsState()

    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByProjectId(projectId)
    }
    Log.d("tasks size", tasks.size.toString())

    // State cho các filter - mặc định là ngày hiện tại
    var selectedDateFilter by remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    }
    Log.d("Selected date filter", selectedDateFilter)
    var selectedStatusFilter by remember { mutableStateOf("All Tasks") }

    // Function để format date hiển thị
    fun formatRelativeDate(date: LocalDate): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        return when {
            date == today -> "Today"
            date == yesterday -> "Yesterday"
            date == tomorrow -> "Tomorrow"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
                date.format(formatter)
            }
        }
    }

    fun getDateOnly(dateTime: String): String {
        return if (dateTime.isNotEmpty() && dateTime.length >= 10) {
            dateTime.substring(0, 10)  // Cắt lấy phần ngày (yyyy-MM-dd)
        } else {
            ""  // Trả về chuỗi rỗng nếu không hợp lệ
        }
    }

    // Hàm filter tasks dựa trên các filter đã chọn
    val filteredTasks = remember(tasks, selectedStatusFilter, selectedDateFilter) {
        var result = tasks

        // Filter theo status
        when (selectedStatusFilter) {
            "Todo" -> result = result.filter { it.status == TaskStatus.PENDING }
            "In Progress" -> result = result.filter { it.status == TaskStatus.IN_PROGRESS }
            "Done" -> result = result.filter { it.status == TaskStatus.COMPLETED }
            "All Tasks" -> { /* Keep all tasks */
            }
        }

        // Filter theo date
        if (selectedDateFilter.isNotEmpty()) {
            result = result.filter { task ->
                Log.d("DateStart Filter", " ${task.dateStart}")
                Log.d("hehee", "${getDateOnly(task.dateStart) == selectedDateFilter}")
                Log.d("duma m", "${getDateOnly(task.dateStart)}")
                getDateOnly(task.dateStart) == selectedDateFilter

            }
            Log.d("DateFilter", "Applied date filter: $selectedDateFilter")
        }

        result
    }
    Log.d("filteredTasks size", filteredTasks.size.toString())
    val toDoTask by rememberUpdatedState(filteredTasks.filter { it.status == TaskStatus.PENDING })
    val inProgressTask by rememberUpdatedState(filteredTasks.filter { it.status == TaskStatus.IN_PROGRESS })
    val doneTask by rememberUpdatedState(filteredTasks.filter { it.status == TaskStatus.COMPLETED })

    // Kanban Board content
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Filter Row với callback xử lý data
        KanbanFilterRow(
            onDateFilterChanged = { dateFilter ->
                selectedDateFilter = dateFilter
                // Convert date để hiển thị relative text trong log
                val selectedLocalDate = try {
                    LocalDate.parse(dateFilter, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (e: Exception) {
                    LocalDate.now()
                }
                val displayText = formatRelativeDate(selectedLocalDate)
                Log.d(
                    "KanbanBoard",
                    "Date filter changed - Display: $displayText, Filter: $dateFilter"
                )
            },
            onTaskStatusChanged = { statusFilter ->
                selectedStatusFilter = statusFilter
                Log.d("KanbanBoard", "Status filter changed: $statusFilter")
                // Tasks sẽ được filter lại tự động do remember(tasks, selectedStatusFilter)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Kanban Board với filtered tasks
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                when (selectedStatusFilter) {
                    "To do" -> KanbanColumn("To Do", toDoTask, projectId)
                    "In Progress" -> KanbanColumn("In Progress", inProgressTask, projectId)
                    "Done" -> KanbanColumn("Done", doneTask, projectId)
                    else -> {
                        KanbanColumn("To Do", toDoTask, projectId)
                        KanbanColumn("In Progress", inProgressTask, projectId)
                        KanbanColumn("Done", doneTask, projectId)
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanFilterRow(
    onDateFilterChanged: (String) -> Unit,  // Callback để xử lý ngày chọn
    onTaskStatusChanged: (String) -> Unit    // Callback để xử lý trạng thái task
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedTaskStatus by remember { mutableStateOf("All Tasks") }
    val context = LocalContext.current
    
    // Trigger callback với ngày hiện tại khi component được load lần đầu
    LaunchedEffect(Unit) {
        val todayFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        onDateFilterChanged(todayFormatted)
    }

    // Function to format date as relative text
    fun formatRelativeDate(date: LocalDate): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        return when {
            date == today -> "Today"
            date == yesterday -> "Yesterday"
            date == tomorrow -> "Tomorrow"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
                date.format(formatter)
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        calendar.time = java.util.Date.from(
            selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()
        )

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                // Format date cho callback (yyyy-MM-dd format để filter)
                val dateForFilter = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                onDateFilterChanged(dateForFilter)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        LaunchedEffect(Unit) {
            datePickerDialog.show()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Date info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    showDatePicker = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    tint = Color(0xFF5B5EF4),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Created ${formatRelativeDate(selectedDate)}",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            // Right side - Filter buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }

                Box {
                    FilterChip(
                        selected = true,
                        onClick = {
                            expanded = true
                        },
                        label = {
                            Text(
                                selectedTaskStatus,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                                ),
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (selectedTaskStatus) {
                                    "All Tasks" -> Icons.Default.CheckCircle
                                    "Todo" -> Icons.Default.Schedule
                                    "In Progress" -> Icons.Default.PlayArrow
                                    "Done" -> Icons.Default.Done
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Show options",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFFF5F5F5),
                            labelColor = Color.DarkGray,
                            iconColor = Color(0xFF5B5EF4),
                            selectedContainerColor = Color(0xFFEEE6FF),
                            selectedLabelColor = Color(0xFF5B5EF4)
                        ),
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White)
                            .width(140.dp) // Fixed width based on longest option "In Progress"
                    ) {
                        listOf("All Tasks", "Todo", "In Progress", "Done").forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                                        ),
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (option) {
                                            "All Tasks" -> Icons.Default.CheckCircle
                                            "Todo" -> Icons.Default.Schedule
                                            "In Progress" -> Icons.Default.PlayArrow
                                            "Done" -> Icons.Default.Done
                                            else -> Icons.Default.CheckCircle
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (selectedTaskStatus == option) Color(0xFF5B5EF4) else Color.Gray
                                    )
                                },
                                onClick = {
                                    selectedTaskStatus = option
                                    onTaskStatusChanged(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Sort",
                        tint = Color.Gray
                    )
                }

                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanColumn(
    title: String, tasks: List<Task>, projectId: String,
    navController: NavController = rememberNavController()
) {
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
    var deleteAble by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth() // Thay đổi từ width(280.dp) thành fillMaxWidth()
            .wrapContentHeight(), // Thay đổi từ fillMaxHeight() để phù hợp với nội dung
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Column Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${tasks.size})",
                        color = Color.Gray,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_regular))
                        ),
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            deleteAble = !deleteAble
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.editing),
                            contentDescription = "Add Task",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tasks
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tasks.forEach { task ->
                    KanbanTaskCard(task, deleteAble,projectId)
                }


                // Add Task Button
                OutlinedButton(
                    onClick = { showSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Task",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                    )
                }
            }

            CreateNewTaskBottomSheet(
                navController = navController,
                showSheet = showSheet,
                projectId = projectId,
                taskType = when (title) {
                    "To Do" -> 1
                    "In Progress" -> 2
                    "Done" -> 3
                    else -> 0
                },
                onDismiss = { showSheet = false },
                )
        }
    }
}

@Composable
fun KanbanTaskCard(
    task: Task, displayDelete: Boolean,projectId : String,
    taskVM: TaskViewModel = viewModel(),
    taskActivityVM: TaskActivityViewModel= viewModel(),
    authVM: AuthViewModel = viewModel()
) {
    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DeleteTaskDialog2(
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                taskVM.deleteTask(task.id)

                val newTaskActivity = TaskActivity(
                    id = "task_${UUID.randomUUID()}",
                    taskId = task.id,
                    notifiedUserIds = emptyList(),
                    action = "${currentUser?.name ?:""} has removed a task",
                    timestamp = formatDate(LocalDateTime.now()),
                    note = "${currentUser?.name ?:""} has removed a new task",
                    worker = currentUser?.userId ?: "",
                    projectId = projectId
                )

                taskActivityVM.addActivity(newTaskActivity)
            }
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Task Title with Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.status == TaskStatus.COMPLETED) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF5B5EF4),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Not Completed",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
                Spacer(modifier = Modifier.weight(1f))
                if (displayDelete == true) {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Not Completed",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                showDialog = true
                            }
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))
            fun convertToTaskPriority(priority: String): TaskPriority {
                return when (priority) {
                    "MEDIUM" -> TaskPriority.MEDIUM
                    "HIGH" -> TaskPriority.HIGH
                    "LOW" -> TaskPriority.LOW
                    else -> TaskPriority.LOW // Mặc định nếu không khớp
                }
            }

            fun convertTaskPriorityToString(priority: TaskPriority): String {
                return when (priority) {
                    TaskPriority.HIGH -> "Cao"
                    TaskPriority.MEDIUM -> "Trung bình"
                    TaskPriority.LOW -> "Thấp"
                }
            }

            // Task Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (task.priority != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (task.priority) {
                                    TaskPriority.HIGH -> Color(0xFFE57373)
                                    TaskPriority.MEDIUM -> Color(0xFFFFB74D)
                                    TaskPriority.LOW -> Color(0xFF81C784)
                                    else -> Color.Gray
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = task.priority.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Task Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Assigned To
                if (task.assignee != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(kanbanMemberColors[task.assignee.size % kanbanMemberColors.size])
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kanbanMemberInitials[task.assignee.size % kanbanMemberInitials.size],
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )
                    }
                }

                // Due Date
                if (task.dateDue != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatToMMMdd(task.dateStart),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                            color = Color.Gray
                        )
                    }
                }

                // Comments Count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// Data Models
data class KanbanColumn(
    val id: String,
    val title: String,
    val tasks: List<KanbanTask>
)

data class KanbanTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: KanbanTaskPriority? = null,
    val status: String? = null,
    val dueDate: String? = null,
    val assignedTo: Int? = null
)

// Sample Data
val kanbanColumns = listOf(
    KanbanColumn(
        id = "todo",
        title = "To do",
        tasks = listOf(
            KanbanTask(
                id = "1",
                title = "Contact client for outline",
                isCompleted = false,
                assignedTo = 0
            ),
            KanbanTask(
                id = "2",
                title = "Contact client for outline",
                isCompleted = false,
                assignedTo = 2
            ),
            KanbanTask(
                id = "3",
                title = "Contact client for outline",
                isCompleted = false,
                assignedTo = 4
            )
        )
    ),
    KanbanColumn(
        id = "doing",
        title = "Doing",
        tasks = listOf(
            KanbanTask(
                id = "4",
                title = "Hold introductory meeting",
                isCompleted = false,
                priority = KanbanTaskPriority.MEDIUM,
                status = "At Risk",
                dueDate = "12-14 Jul",
                assignedTo = 1
            ),
            KanbanTask(
                id = "5",
                title = "Set final deadline",
                isCompleted = false,
                priority = KanbanTaskPriority.HIGH,
                status = "Off Track",
                assignedTo = 3
            ),
            KanbanTask(
                id = "6",
                title = "Determine project goal",
                isCompleted = false,
                priority = KanbanTaskPriority.LOW,
                status = "On Track",
                dueDate = "10-13 Jul",
                assignedTo = 0
            )
        )
    ),
    KanbanColumn(
        id = "done",
        title = "Done",
        tasks = listOf(
            KanbanTask(
                id = "7",
                title = "Determine project goal",
                isCompleted = true,
                priority = KanbanTaskPriority.LOW,
                status = "On Track",
                dueDate = "10-13 Jul",
                assignedTo = 2
            ),
            KanbanTask(
                id = "8",
                title = "Hold introductory meeting",
                isCompleted = true,
                priority = KanbanTaskPriority.MEDIUM,
                status = "At Risk",
                dueDate = "12-14 Jul",
                assignedTo = 1
            )
        )
    ),
    KanbanColumn(
        id = "untitled",
        title = "Untitled section",
        tasks = listOf()
    )
)

// Enums for task priority
enum class KanbanTaskPriority { HIGH, MEDIUM, LOW }

// Sample data for members
val kanbanMemberInitials = listOf("JD", "MS", "AK", "TW", "RB")
val kanbanMemberColors = listOf(
    Color(0xFF5B5EF4),
    Color(0xFFFF6D00),
    Color(0xFF43A047),
    Color(0xFFE91E63),
    Color(0xFF00ACC1)
)

@Composable
fun OverviewTab(
    projectId: String,
    projectVM: ProjectViewModel = viewModel(),
    taskVM: TaskViewModel = viewModel(),
    taskActivityVM: TaskActivityViewModel = viewModel(),
    authVM: AuthViewModel = viewModel()
) {
    val project by projectVM.selectedProject.collectAsState()
    LaunchedEffect(String) {
        projectVM.getProjectById(projectId)
    }

    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByProjectId(projectId)
    }
    val taskCompleted = tasks.filter { it.status == TaskStatus.COMPLETED }.size
    val taskInProgress = tasks.filter { it.status == TaskStatus.IN_PROGRESS }.size
    val taskPending = tasks.filter { it.status == TaskStatus.PENDING }.size
    val taskTotal = tasks.size

    val taskActivity by taskActivityVM.activities.collectAsState()
    LaunchedEffect(tasks) {
        tasks.forEach { task ->
            taskActivityVM.getTaskActivityByProjectId(projectId)
        }
    }

    var userList by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(taskActivity) {
        val workerIds = taskActivity.map { it.worker }.distinct()

        val users = coroutineScope {
            workerIds.map { workerId ->
                async {
                    authVM.getUserById(workerId)
                }
            }.awaitAll().filterNotNull()
        }
        userList = users
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // Project Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Project Overview",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Project Description
                Text(
                    text = project?.description
                        ?: "This project aims to develop a comprehensive task management system with Kanban methodology for better workflow visualization and team collaboration.",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_regular))
                    ),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Project Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatColumn("Tasks", taskTotal.toString(), Color(0xFF5B5EF4))
                    StatColumn("Completed", taskCompleted.toString(), Color(0xFF43A047))
                    StatColumn("In Progress", taskInProgress.toString(), Color(0xFFFF6D00))
                    StatColumn("Pending", taskPending.toString(), Color(0xFFE91E63))
                }
                Spacer(modifier = Modifier.height(16.dp))
                val progress = (taskCompleted.toFloat() / taskTotal.toFloat()) * 100
                // Progress Bar
                Text(
                    text = "Overall Progress: ${progress}%",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.67f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF5B5EF4),
                    trackColor = Color(0xFFE0E0E0)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Activities
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Recent Activities",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))

                if (taskActivity.isNotEmpty()) {
                    val userMap = userList.filterNotNull().associateBy { it.userId }
                    taskActivity.take(5).forEachIndexed { index, taskAc ->
                        val user = userMap[taskAc.worker]
                        val userIndex = userList.indexOfFirst { it.userId == taskAc.worker }
                        val safeUserIndex = if (userIndex >= 0) userIndex else 0

                        ActivityItem(
                            user?.name ?: "Unknown",
                            taskAc.action,
                            taskAc.note,
                            taskAc.timestamp,
                            kanbanMemberInitials[safeUserIndex % kanbanMemberInitials.size],
                            kanbanMemberColors[safeUserIndex % kanbanMemberColors.size]
                        )

                        // Add divider between items (except for the last one)
                        if (index < taskActivity.take(5).size - 1) {
                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray
                            )
                        }
                    }
                } else {
                    // Activity Items
                    ActivityItem(
                        "John Doe",
                        "added a new task",
                        "Contact client for outline",
                        "2 hours ago",
                        kanbanMemberInitials[0],
                        kanbanMemberColors[0]
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )

                    ActivityItem(
                        "Maria Smith",
                        "completed",
                        "Create wireframes for homepage",
                        "Yesterday",
                        kanbanMemberInitials[1],
                        kanbanMemberColors[1]
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )

                    ActivityItem(
                        "Alex Kim",
                        "commented on",
                        "Implement user authentication",
                        "2 days ago",
                        kanbanMemberInitials[2],
                        kanbanMemberColors[2]
                    )
                }

            }
        }
    }
}

@Composable
fun ListTab(
    projectId: String,
    projectVM: ProjectViewModel = viewModel(),
    taskVM: TaskViewModel = viewModel(),
    taskActivityVM: TaskActivityViewModel = viewModel(),
    authVM: AuthViewModel = viewModel()
) {
//    val project by projectVM.selectedProject.collectAsState()
//    LaunchedEffect(String) {
//        projectVM.getProjectById(projectId)
//    }
    Log.d("Project id in listtab", "$projectId")
    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByProjectId(projectId)
    }
    Log.d("tasks", "$tasks")

    var searchText by remember { mutableStateOf("") }

    val filteredTasks = tasks.filter { it.title.contains(searchText, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // Search and Filter
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Search tasks...",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            textStyle = MaterialTheme.typography.displayMedium.copy(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF5B5EF4)
            ),

            )

        Spacer(modifier = Modifier.height(16.dp))

        // Task List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTasks) { task ->
                ListTaskItem(task)
            }
        }
    }
}

fun getMiddleMonthTimeline(timeLines: List<TimeLine>): TimeLine? {
    val sorted = timeLines.sortedBy { it.startDate }
    val middleIndex = sorted.size / 2
    return sorted.getOrNull(middleIndex)
}

fun formatMonth(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateString, formatter)
    return "Tháng ${date.monthValue}"
}

@Composable
fun TimelineTab(
    projectId: String,
    timeLineVM: TimeLineViewModel = viewModel()
) {
    Log.d("Project id", "$projectId")

    val timeLines by timeLineVM.timeLines.collectAsState()
    LaunchedEffect(projectId) {
        timeLineVM.getTimeLinesByProjectId(projectId.trim())
    }
//    LaunchedEffect(Unit) {
//        timeLineVM.loadAllTimeLines()
//    }

    Log.d("timeLine", "$timeLines")
    Log.d("timeline size", "${timeLines.size}")

    val sortedTimeLines = timeLines.sortedBy { it.startDate }
    Log.d("sortedTimeLines", "$sortedTimeLines")

// Tìm timeline ở giữa để hiển thị mặc định
    val defaultTimeLine = remember(timeLines) {
        sortedTimeLines.getOrNull(sortedTimeLines.size / 2)
    }
    Log.d("defaultTimeLine", "$defaultTimeLine")

    val selectedTimeLine = remember { mutableStateOf<TimeLine?>(null) }

    LaunchedEffect(defaultTimeLine) {
        if (defaultTimeLine != null && selectedTimeLine.value == null) {
            selectedTimeLine.value = defaultTimeLine
        }
    }
    Log.d("Selected time line", "$selectedTimeLine")
// Lọc timeline theo tháng được chọn
    val displayTimeLine = timeLines.filter {
        it.startDate == selectedTimeLine.value?.startDate &&
                it.endDate == selectedTimeLine.value?.endDate
    }
    Log.d("display time line", "$displayTimeLine")
    val currentIndex = remember(timeLines) {
        mutableIntStateOf(sortedTimeLines.indexOf(defaultTimeLine))
    }
    Log.d("Current index", "$currentIndex")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // Timeline Header with Date Range
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedTimeLine.value?.startDate?.let {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val monthFormatter =
                            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
                        LocalDate.parse(it, formatter).format(monthFormatter)
                    } ?: "",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )

                Row {
                    IconButton(
                        onClick = {
                            if (currentIndex.intValue > 0) {
                                currentIndex.intValue--
                                selectedTimeLine.value = sortedTimeLines[currentIndex.intValue]
                            }
                        },
                        enabled = currentIndex.intValue > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Month"
                        )
                    }

                    IconButton(
                        onClick = {
                            if (currentIndex.intValue < sortedTimeLines.size - 1) {
                                currentIndex.intValue++
                                selectedTimeLine.value = sortedTimeLines[currentIndex.intValue]
                            }
                        },
                        enabled = currentIndex.intValue < sortedTimeLines.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Month"
                        )
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timeline Content
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(start = 16.dp)
        ) {
            items(displayTimeLine) { timeline ->
                TimelineWeek(projectId,timeline )
            }
        }
    }
}

// Helper Composables
@Composable
fun StatColumn(title: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            color = Color.Gray
        )
    }
}

@Composable
fun ActivityItem(
    name: String,
    action: String,
    taskName: String,
    time: String,
    initial: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            // Activity Description
            Row {
                Text(
                    text = name,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = action,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_regular))
                    ),
                    color = Color.DarkGray
                )
            }

            // Task Name
            Text(
                text = "\"$taskName\"",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                color = Color(0xFF5B5EF4)
            )

            // Time
            Text(
                text = time,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ListTaskItem(
    task: Task,
    authVM: AuthViewModel = viewModel()
) {
    val user by authVM.user.collectAsState()
    LaunchedEffect(task.assignee) {  // Đảm bảo theo dõi thay đổi của task.assignee
        if (task.assignee != null && task.assignee.isNotEmpty()) {
            authVM.getUserById(task.assignee[0])  // Lấy người dùng theo ID đầu tiên trong assignee
        } else {
            authVM.fetchAndSetCurrentUser()  // Nếu không có assignee, lấy người dùng hiện tại
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = if (task.status == TaskStatus.COMPLETED) true else false,
                onCheckedChange = { /* TODO */ },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF5B5EF4),
                    uncheckedColor = Color.Gray
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
//                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                if (task.dateDue != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Due: ${formatToMMMdd(task.dateDue)}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        color = Color.Gray
                    )
                }
            }

            // Priority and Assigned Person
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.priority != null) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (task.priority) {
                                    TaskPriority.HIGH -> Color(0xFFE57373)
                                    TaskPriority.MEDIUM -> Color(0xFFFFB74D)
                                    TaskPriority.LOW -> Color(0xFF81C784)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (task.assignee != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(kanbanMemberColors[task.assignee.size % kanbanMemberColors.size])
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kanbanMemberInitials[task.assignee.size % kanbanMemberInitials.size],
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )
                    }
                }else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(kanbanMemberColors[1 % kanbanMemberColors.size])
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kanbanMemberInitials[1 % kanbanMemberInitials.size],
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )
                    }
                }
            }
        }
    }
}


fun formatTimelineTitle(startDateStr: String, endDateStr: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(startDateStr, formatter)
    val endDate = LocalDate.parse(endDateStr, formatter)

    // Tuần thứ mấy trong tháng theo locale (vi hoặc mặc định là en-US)
    val weekFields = WeekFields.of(Locale.getDefault())
    val weekOfMonth = startDate.get(weekFields.weekOfMonth())

    // Định dạng ngày dạng "Jul 1" và "Jul 7"
    val displayFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
    val startDisplay = startDate.format(displayFormatter)
    val endDisplay = endDate.format(displayFormatter)

    return "Week$weekOfMonth ($startDisplay-$endDisplay)"
}


fun Int.toColor(): Color = when (this) {
    1 -> Color(0xFFE0F7FA)
    2 -> Color(0xFFFFF3E0)
    3 -> Color(0xFFE8F5E9)
    else -> Color(0xFFF5F5F5)
}


@Composable
fun TimelineWeek(
    projectId : String ,
    timeline: TimeLine,
    timeLineVM: TimeLineViewModel = viewModel(),
    authVM: AuthViewModel = viewModel(),
    taskActivityViewModel: TaskActivityViewModel = viewModel()
) {
    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }

    fun updateEventAt(index: Int, updateFunction: (TimeEvent) -> TimeEvent) {
        val updatedEvent = updateFunction(timeline.events[index])
        val updatedEvents = timeline.events.toMutableList().also {
            it[index] = updatedEvent
        }
        timeLineVM.updateTimeLine(timeline.copy(events = updatedEvents))
    }
    Column {
        Text(
            text = formatTimelineTitle(timeline.startDate, timeline.endDate),
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Timeline items
        Row {
            // Timeline line with dots
            val baseHeightPerEvent = 250.dp / 3
            val lineHeight = baseHeightPerEvent * timeline.events.size

            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(lineHeight)
                    .background(Color.Transparent),
                contentAlignment = Alignment.TopCenter
            ) {
                // Vertical line
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.LightGray)
                )
                // Timeline dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5B5EF4))
                )
            }

            if (timeline.events.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(start = 8.dp, top = 0.dp)
                ) {
                    timeline.events.forEachIndexed { index, event ->
                        TimelineItemEditable(
                            date = event.startTime,
                            title = event.title,
                            description = event.description,
                            backgroundColor = event.type.toColor(),
                            onDateChange = { newDate ->
                                updateEventAt(index) { it.copy(startTime = newDate) }
                            },
                            onTitleChange = { newTitle ->
                                updateEventAt(index) { it.copy(title = newTitle) }
                            },
                            onDescriptionChange = { newDesc ->
                                updateEventAt(index) { it.copy(description = newDesc) }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    OutlinedButton(
                        onClick = {
                            val newEvent = TimeEvent(
                                dayOfWeek = "",
                                startTime = "",
                                endTime = "",
                                title = "",
                                description = "",
                                type = Random.nextInt(1, 5)
                            )
                            val updatedTimeLine = timeline.copy(
                                events = timeline.events + newEvent
                            )
                            timeLineVM.updateTimeLine(updatedTimeLine)

                            val newActivityTask = TaskActivity(
                                id = "task_${UUID.randomUUID()}",
                                taskId = "",
                                notifiedUserIds = emptyList(),
                                action = "${currentUser?.name ?:""} has add new event for time line",
                                timestamp = formatDate2(LocalDateTime.now()),
                                note = "${currentUser?.name ?:""}  has add new event for time line",
                                worker = currentUser?.userId ?: "",
                                projectId = projectId
                            )
                            taskActivityViewModel.addActivity(newActivityTask)

                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(start = 8.dp, top = 0.dp)
                ) {
                    TimelineItem(
                        "Jul 10",
                        "Design team meeting",
                        "Discuss wireframes and UI components",
                        Color(0xFFE0F7FA)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TimelineItem(
                        "Jul 12",
                        "Client presentation",
                        "Present project progress and gather feedback",
                        Color(0xFFFFF8E1)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TimelineItem(
                        "Jul 14",
                        "Development sprint planning",
                        "Plan tasks for the next two weeks",
                        Color(0xFFE8F5E9)
                    )
                }
            }
        }
    }
}


//val backgroundColor = when (event.type) {
//    EventType.MEETING -> Color(0xFFE0F7FA)        // Light blue
//    EventType.PRESENTATION -> Color(0xFFFFF3E0)   // Light orange
//    EventType.PLANNING -> Color(0xFFE8F5E9)       // Light green
//    EventType.OTHER -> Color(0xFFF5F5F5)          // Neutral grey
//}


@Composable
fun TimelineItem(date: String, title: String, description: String, backgroundColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun TimelineItemEditable(
    date: String,
    title: String,
    description: String,
    backgroundColor: Color,
    onDateChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    // Local state để lưu giá trị đang nhập
    var localDate by remember { mutableStateOf(date) }
    var localTitle by remember { mutableStateOf(title) }
    var localDescription by remember { mutableStateOf(description) }

    val focusManager = LocalFocusManager.current

    // Update local state khi props thay đổi từ bên ngoài
    LaunchedEffect(date) { localDate = date }
    LaunchedEffect(title) { localTitle = title }
    LaunchedEffect(description) { localDescription = description }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Editable Date
            BasicTextField(
                value = localDate,
                onValueChange = { localDate = it },
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular)),
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .onFocusChanged { focusState ->
                        // Update khi mất focus
                        if (!focusState.isFocused && localDate != date) {
                            onDateChange(localDate)
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        // Update khi nhấn Next
                        if (localDate != date) {
                            onDateChange(localDate)
                        }
                    }
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (localDate.isEmpty()) {
                        Text(
                            text = "Add Date",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_regular)),
                                color = Color.Gray
                            )
                        )
                    }
                    innerTextField()
                }
            )

            // Editable Title
            BasicTextField(
                value = localTitle,
                onValueChange = { localTitle = it },
                textStyle = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb)),
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .onFocusChanged { focusState ->
                        // Update khi mất focus
                        if (!focusState.isFocused && localTitle != title) {
                            onTitleChange(localTitle)
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        // Update khi nhấn Next
                        if (localTitle != title) {
                            onTitleChange(localTitle)
                        }
                    }
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (localTitle.isEmpty()) {
                        Text(
                            text = "Add Title",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb)),
                                color = Color.Gray
                            )
                        )
                    }
                    innerTextField()
                }
            )

            // Editable Description
            BasicTextField(
                value = localDescription,
                onValueChange = { localDescription = it },
                textStyle = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular)),
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .onFocusChanged { focusState ->
                        // Update khi mất focus
                        if (!focusState.isFocused && localDescription != description) {
                            onDescriptionChange(localDescription)
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Update khi nhấn Done
                        if (localDescription != description) {
                            onDescriptionChange(localDescription)
                        }
                        focusManager.clearFocus()
                    }
                ),
                decorationBox = { innerTextField ->
                    if (localDescription.isEmpty()) {
                        Text(
                            text = "Add Description",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_regular)),
                                color = Color.Gray
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

// Placeholder for other tabs
@Composable
fun CalendarTab() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Calendar View Coming Soon")
    }
}

@Composable
fun DashboardTab() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Dashboard View Coming Soon")
    }
}

@Preview(showBackground = true)
@Composable
fun KanbanBoardPagePreview() {
    To_do_appTheme {
//        KanbanBoardPage()
    }
}



