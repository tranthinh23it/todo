package com.example.to_do_app.presentation.screens.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.R
import com.example.to_do_app.components.CategoryTopAppBar
import com.example.to_do_app.components.shareText
import com.example.to_do_app.ui.theme.To_do_appTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun CalenderTimePage() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<CalendarTask?>(null) }
    val context = LocalContext.current
    
    val onDeleteTask: (CalendarTask) -> Unit = { task ->
        taskToDelete = task
        showDeleteDialog = true
    }
    
    Scaffold(
        topBar = {
            CategoryTopAppBar(
                text = "Calendar & Time", 
                onBackClick = { /* TODO: Handle back navigation */ },
                icon = Icons.Default.Share,
                onClick = {
                    shareText(context, "Check out my calendar and tasks!")
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Calendar section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Month navigation with improved styling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { currentMonth = currentMonth.minusMonths(1) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFFF0F0F0),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Previous month",
                                    tint = Color(0xFF333333)
                                )
                            }
                            
                            Text(
                                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                                ),
                                color = Color(0xFF1A1A1A)
                            )
                            
                            IconButton(
                                onClick = { currentMonth = currentMonth.plusMonths(1) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Color(0xFFF0F0F0),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Next month",
                                    tint = Color(0xFF333333)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Calendar grid
                        CalendarGridContent(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it }
                        )
                    }
                }
            }

            item {
                // Enhanced Stats row
                StatsRow()
            }

            item {
                // Task list with header
                Column {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "Today's Tasks",
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color(0xFF1A1A1A)
//                        )
//                        TextButton(onClick = { /* TODO */ }) {
//                            Text(
//                                text = "View All",
//                                color = Color(0xFFFF4040)
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Task items directly in Column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleTasks.forEach { task ->
                            TaskItem(task, onDeleteTask)
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && taskToDelete != null) {
        DeleteTaskDialog(
            task = taskToDelete!!,
            onConfirm = {
                // TODO: Handle actual deletion
                showDeleteDialog = false
                taskToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                taskToDelete = null
            }
        )
    }
}

@Composable
fun CalendarGridContent(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val days = remember(currentMonth) {
        val firstDay = currentMonth.atDay(1)
        val lastDay = currentMonth.atEndOfMonth()

        // Get the day of week for the first day (1 = Monday, 7 = Sunday)
        val firstDayOfWeek = firstDay.dayOfWeek.value

        // Previous month days to show
        val prevMonthDays = if (firstDayOfWeek > 1) {
            val prevMonth = currentMonth.minusMonths(1)
            val daysInPrevMonth = prevMonth.lengthOfMonth()
            (daysInPrevMonth - firstDayOfWeek + 2..daysInPrevMonth).map {
                CalendarDay(prevMonth.atDay(it), isCurrentMonth = false)
            }
        } else emptyList()

        // Current month days
        val currentMonthDays = (1..currentMonth.lengthOfMonth()).map {
            CalendarDay(currentMonth.atDay(it), isCurrentMonth = true)
        }

        // Next month days to fill the grid
        val totalDaysSoFar = prevMonthDays.size + currentMonthDays.size
        val nextMonthDays = if (totalDaysSoFar < 42) { // 6 rows of 7 days
            val nextMonth = currentMonth.plusMonths(1)
            (1..(42 - totalDaysSoFar)).map {
                CalendarDay(nextMonth.atDay(it), isCurrentMonth = false)
            }
        } else emptyList()

        prevMonthDays + currentMonthDays + nextMonthDays
    }

    // Day of week headers
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                color = Color.Gray
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Calendar days grid using Column and Row
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                week.forEach { day ->
                    Box(modifier = Modifier.weight(1f)) {
                        DayCell(
                            day = day,
                            isSelected = day.date.isEqual(selectedDate),
                            onDateSelected = onDateSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val isToday = day.date.isEqual(today)

    // Determine background color
    val backgroundColor = when {
        isSelected -> Color(0xFFFF4040) // Red for selected
        isToday -> Color(0xFFFFEBEB) // Light red for today
        else -> Color.Transparent
    }

    // Determine text color
    val textColor = when {
        isSelected -> Color.White
        !day.isCurrentMonth -> Color.LightGray
        else -> Color.Black
    }

    // Progress indicator for some days (just for demo)
    val showProgress = day.isCurrentMonth &&
                      (day.date.dayOfMonth % 5 == 0 || day.date.dayOfMonth % 7 == 0)
    val progressPercent = if (showProgress) (day.date.dayOfMonth * 3) % 100 else 0

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onDateSelected(day.date) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showProgress && !isSelected) {
            CircularProgressIndicator(
                progress = { progressPercent / 100f },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFFF4040),
                trackColor = Color(0xFFFFEBEB),
                strokeWidth = 3.dp
            )
        }

        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.monasan_regular))
            ),
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun StatsRow() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp).padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalendarStatItem(title = "Progress", value = "71.4%")
            CalendarVerticalDivider()
            CalendarStatItem(title = "Worked", value = "300 mins")
            CalendarVerticalDivider()
            CalendarStatItem(title = "Sessions", value = "12")
        }
    }
}

@Composable
fun CalendarVerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Color.LightGray)
    )
}

@Composable
fun CalendarStatItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.monasan_regular))
            ),
            color = Color.Gray
        )
    }
}

@Composable
fun TaskList(onDeleteTask: (CalendarTask) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(sampleTasks) { task ->
            TaskItem(task, onDeleteTask)
        }
    }
}

@Composable
fun TaskItem(task: CalendarTask, onDeleteTask: (CalendarTask) -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(300)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        // Delete background (chỉ hiện khi swipe)
        if (offsetX < 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .width(77.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                        .background(Color(0xFFFF4040))
                        .clickable { onDeleteTask(task) }
//                        .shadow(
//                            elevation = 4.dp,
//                            shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
//                        )
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (offsetX < -100f) -200f else 0f
                        }
                    ) { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-200f, 0f)
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Task icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFF4040)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Task details
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )

                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${task.completedMinutes}/${task.totalMinutes} mins",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_regular))
                            ),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${task.timeLeft})",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_regular))
                            ),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${task.minutes} mins",
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
}

// Data classes
data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)

data class CalendarTask(
    val title: String,
    val minutes: Int,
    val progress: Float,
    val completedMinutes: Int,
    val totalMinutes: Int,
    val timeLeft: String,
    val color: Color
)

// Sample data
val sampleTasks = listOf(
    CalendarTask(
        title = "Design User Interface (UI)",
        minutes = 200,
        progress = 0.8f,
        completedMinutes = 160,
        totalMinutes = 200,
        timeLeft = "13h 20m",
        color = Color(0xFFFF4040)
    ),
    CalendarTask(
        title = "Create a Design Wireframe",
        minutes = 50,
        progress = 0.4f,
        completedMinutes = 40,
        totalMinutes = 150,
        timeLeft = "50m",
        color = Color(0xFFFF4040)
    ),
    CalendarTask(
        title = "Designing Brand Logos",
        minutes = 25,
        progress = 0.0f,
        completedMinutes = 0,
        totalMinutes = 100,
        timeLeft = "25m",
        color = Color(0xFFFF4040)
    )
)

@Composable
fun DeleteTaskDialog(
    task: CalendarTask,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Task") },
        text = { Text("Are you sure you want to delete this task?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CalenderTimePagePreview() {
    To_do_appTheme {
        CalenderTimePage()
    }
}
