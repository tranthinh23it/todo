package com.example.to_do_app.presentation.screens.task

import BottomNavigation
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
//import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.ThreeDRotation
//import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Business
//import androidx.compose.material.icons.filled.Chat
//import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.ui.theme.To_do_appTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.to_do_app.R
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.TaskViewModel
import kotlin.math.roundToInt
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.to_do_app.presentation.screens.getPriorityColor
import com.example.to_do_app.util.Screens
import com.example.to_do_app.util.TaskStatus
import formatDate1
import java.time.LocalDateTime
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTaskPage(
    navController: NavController = rememberNavController(),
) {
    // Cho phép kéo thả FAB
    var fabOffset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {

        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
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
        // snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // quan trọng: tránh đè TopBar/FAB
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Top Bar
            TopBar()
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your progress task",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Section
                    ProgressSection()
                    Spacer(modifier = Modifier.height(24.dp))

                    // Task List Section
                    TaskListSection()
                }
            }
        }
    }
}

@Composable
fun TopBar(
    navController: NavController = rememberNavController(),
    authVM: AuthViewModel = viewModel()
) {
    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5B5EF4))
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
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = currentUser?.name ?: "Full Name",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb)),
//                        fontWeight = FontWeight.Bold[[[[]]]]
                    ),
                )
            }

            Row {
                IconButton(onClick = {
                    navController.navigate(Screens.NotificationsPage.route)
                }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
                IconButton(onClick = {
                    navController.navigate(Screens.SearchPage.route)
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressSection(
    taskVM: TaskViewModel = viewModel(),
    userVM: AuthViewModel = viewModel()
) {
    val currentUser by userVM.currentUser.observeAsState()
    val tasks by taskVM.tasks.collectAsState(initial = emptyList())

// 1) Load current user đúng 1 lần
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }
// 2) Khi userId có giá trị thì mới fetch tasks
    LaunchedEffect(currentUser?.userId) {
        val uid = currentUser?.userId?.trim()
        if (!uid.isNullOrBlank()) {
            taskVM.getTasksByUserId(uid)
        }
    }

    Log.d("CurrentUser", "userId=${currentUser?.userId}")
    Log.d("ProgressSection", "tasks=${tasks.size}")

    val todayString = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    val todayTasks = tasks.filter { formatDate1(it.dateStart) == todayString }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Task List Card
        Card(
            modifier = Modifier
                .weight(1f)
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEE6FF))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "List",
                        tint = Color.Gray
                    )
                    Text(
                        text = "6",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val todayTask = todayTasks.shuffled().take(4)
                todayTask.forEach { task ->
                    TaskItemWithIcon(
                        task.title,
                        getPriorityColor(task.priority.toString()),
                        Icons.Default.Phone
                    )
                }
                // Task items with colored indicators
                TaskItemWithIcon("Read a Book", Color.Red, Icons.Default.Phone)
                TaskItemWithIcon("Weekly Meet", Color.Gray, Icons.Default.Person)
                TaskItemWithIcon("3D Designing", Color(0xFFFFD700), Icons.Default.Place)
                TaskItemWithIcon("Meeting With...", Color.Black, Icons.Default.PlayArrow)
            }
        }

        val doneTask =
            todayTasks.filter { it.status == TaskStatus.COMPLETED && it.status == TaskStatus.IN_PROGRESS }
        val percentage = (doneTask.size.toFloat() / todayTasks.size.toFloat()) * 100

        // Progress Circle Card
        Card(
            modifier = Modifier
                .weight(1f)
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Progress Circle

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Background circle (full)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color(0xFF87CEFA),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 20f, cap = StrokeCap.Round)
                        )
                    }

                    // Progress arc (theo percentage)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color(0xFFFF69B4),
                            startAngle = 270f,
                            sweepAngle = (percentage / 100f) * 360f, // Sử dụng percentage tính ở trên
                            useCenter = false,
                            style = Stroke(width = 20f, cap = StrokeCap.Round)
                        )
                    }

                    // Percentage text
                    Text(
                        text = "${percentage.toInt()}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Text below progress circle
                Text(
                    text = "My Progress Task",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = "${doneTask.size} out ${todayTasks.size} task done",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_regular))
                    ),
                )
            }
        }
    }
}

@Composable
fun TaskItemWithIcon(text: String, color: Color, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        // Colored icon background
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
        )
    }
}

@Composable
fun TaskListSection(
    taskVM: TaskViewModel = viewModel(),
    userVM: AuthViewModel = viewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
//    Log.d("selectedDate", "$selectedDate")

    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }

    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByUserId(currentUser?.userId ?: "")
    }
    val filterTask = tasks.filter { formatDate1(it.dateStart) == selectedDate.toString() }
    Log.d("filter task", "$filterTask")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "List task",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
            )
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Week View with horizontal scrolling
        CalendarWeekView(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (filterTask != null) {
            filterTask.forEach { task ->
                EventCard(
                    title = task.title,
                    type = task.description,
                    time = " ${extractTime(task.dateStart)} - ${extractTime(task.dateDue)}",
                    color = Color(0xFFFFF0F0)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            // Event Cards
            EventCard(
                title = "Townhall meeting online",
                type = "Work Event",
                time = "7:00 am - 9:00 am",
                color = Color(0xFFFFF0F0)
            )

            Spacer(modifier = Modifier.height(12.dp))
            EventCard(
                title = "Townhall meeting online",
                type = "Work Event",
                time = "7:00 am - 9:00 am",
                color = Color(0xFFE6F7FF)
            )
            Spacer(modifier = Modifier.height(12.dp))

            EventCard(
                title = "Townhall meeting online",
                type = "Work Event",
                time = "7:00 am - 9:00 am",
                color = Color(0xFFFFF0F0)
            )
        }


    }
}

@Composable
fun CalendarWeekView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    taskVM: TaskViewModel = viewModel(), userVM: AuthViewModel = viewModel()

) {

    // State for current month and year
    // var currentDate by remember { mutableStateOf(LocalDate.now()) } // Removed
    val currentMonth = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val currentYear = selectedDate.year.toString()

    // Today's date for reference
    val today = remember { LocalDate.now() }
    val currentUser by userVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVM.fetchAndSetCurrentUser()
    }
    Log.d("CurrentUser", "userId=${currentUser}")

    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByUserId(currentUser?.userId ?: "")
    }
    Log.d("tasks", "$tasks")
    Log.d("Selected date ", selectedDate.toString())
    val filterTask = tasks.filter { formatDate1(it.dateStart) == selectedDate.toString() }
//    Log.d("filter task", filterTask.toString())

    val yearMonth = YearMonth.from(selectedDate)
    val daysWithTasks = remember(tasks, yearMonth) {
        tasks.mapNotNull { parseDateStart(it.dateStart) }
            .filter { YearMonth.from(it) == yearMonth }
            .map { it.dayOfMonth }
            .distinct()
            .sorted()
    }

//    Log.d("Date with task", daysWithTasks.toString())

    // Generate days for the visible month
    val daysInMonth = remember(selectedDate, daysWithTasks) {
        val firstDayOfMonth = selectedDate.withDayOfMonth(1)
        val lastDayOfMonth =
            selectedDate.withDayOfMonth(selectedDate.month.length(selectedDate.isLeapYear))

        val days = mutableListOf<DayData>()

        // Add days from previous month for padding if needed
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        if (firstDayOfWeek > 1) {
            val prevMonth = firstDayOfMonth.minusMonths(1)
            val daysInPrevMonth = prevMonth.month.length(prevMonth.isLeapYear)
            for (i in (daysInPrevMonth - firstDayOfWeek + 2)..daysInPrevMonth) {
                val date = prevMonth.withDayOfMonth(i)
                days.add(
                    DayData(
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        i.toString(),
                        false,
                        false,
                        true
                    )
                )
            }
        }

        // Add days from current month
        for (i in 1..lastDayOfMonth.dayOfMonth) {
            val date = firstDayOfMonth.plusDays((i - 1).toLong())
            val isSelected =
                date.isEqual(selectedDate)  // Sửa: so sánh với selectedDate thay vì today
//            val hasEvent = listOf(2, 5, 10, 15, 20, 25).contains(i)
            val hasEvent = daysWithTasks.contains(i)
            Log.d("hasEvent", "$hasEvent")

            days.add(
                DayData(
                    date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    i.toString(),
                    isSelected,
                    hasEvent
                )
            )
        }
        days
    }

    // Find index of today in the list
    val todayIndex = daysInMonth.indexOfFirst { it.isSelected }

    // Create a LazyListState to control scrolling
    val listState = rememberLazyListState()

    // Scroll to today's position when the component is first composed
    LaunchedEffect(key1 = selectedDate) {
        if (todayIndex != -1) {
            // Add a small delay to ensure the list is properly laid out
            delay(100)
            // Scroll to position with the current day centered
            listState.animateScrollToItem(
                index = todayIndex,
                scrollOffset = -((listState.layoutInfo.viewportSize.width - 72.dp.value.toInt()) / 2)
            )
        }
    }




    Column {
        // Month and Year header with navigation arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    // Go to previous month
                    val newDate = selectedDate.minusMonths(1)
                    onDateSelected(newDate)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = Color.Gray
                )
            }

            Text(
                text = "$currentMonth $currentYear",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                color = Color.Gray
            )

            IconButton(
                onClick = {
                    // Go to next month
                    val newDate = selectedDate.plusMonths(1)
                    onDateSelected(newDate)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of the month with horizontal scrolling
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(daysInMonth) { day ->
                DayItem(
                    day.name, day.date, day.isSelected, day.hasEvent,
                    onClick = {
                        if (!day.isPadding) {
                            val clickedDate = selectedDate.withDayOfMonth(day.date.toInt())
                            onDateSelected(clickedDate)
                        }
                    }
                )
            }
        }
    }
}

// Updated data class with padding flag
data class DayData(
    val name: String,
    val date: String,
    val isSelected: Boolean,
    val hasEvent: Boolean = false,
    val isPadding: Boolean = false
)

@Composable
fun DayItem(
    day: String,
    date: String,
    isSelected: Boolean = false,
    hasEvent: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF5B5EF4) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date,
                color = if (isSelected) Color.White else Color.Black,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
            )
        }
        if (hasEvent) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5B5EF4))
            )
        }
    }
}

@Composable
fun EventCard(title: String, type: String, time: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (title.contains("Townhall")) Icons.Default.Person else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = type,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
            )

            if (!title.contains("Townhall")) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Participant avatars
                    Row {
                        for (i in 0..3) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .offset(x = (-8 * i).dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Time",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_regular))
                            ),
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun MyTaskPagePreview() {
    To_do_appTheme {
        MyTaskPage()
    }
}

fun parseDateStart(dateStr: String): LocalDate? {
    return try {
        LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            .toLocalDate()
    } catch (e: Exception) {
        null
    }
}
fun extractTime(dateTimeStr: String?): String? {
    if (dateTimeStr.isNullOrBlank()) return null
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(dateTimeStr.trim(), formatter)
        dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        null // hoặc trả về "" nếu muốn
    }
}