import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.R
import com.example.to_do_app.ui.theme.To_do_appTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.isActive
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_app.domain.Task
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.TaskViewModel
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import kotlin.math.roundToInt
import kotlin.math.max
import kotlin.math.min
import java.time.LocalDateTime


// Định dạng
private val ISO_MINUTE: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
private val SPACE_MINUTE: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

private fun parseFlexible(dt: String): LocalDateTime =
    try {
        LocalDateTime.parse(dt, SPACE_MINUTE)
    }   // "2025-08-01 09:00"
    catch (_: Exception) {
        LocalDateTime.parse(dt, ISO_MINUTE)
    } // "2025-08-01T09:00"

private fun toIsoMinute(dt: String): String =
    parseFlexible(dt).format(ISO_MINUTE)

/** Chuẩn hóa 1 task về chuỗi thời gian dạng ISO_MINUTE */
private fun normalizeTask(t: Task): Task =
    t.copy(
        dateStart = toIsoMinute(t.dateStart),
        dateDue = toIsoMinute(t.dateDue)
    )

private fun Task.startLdt(): LocalDateTime = LocalDateTime.parse(dateStart, ISO_MINUTE)
private fun Task.endLdt(): LocalDateTime = LocalDateTime.parse(dateDue, ISO_MINUTE)


private val DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

fun extractHour(datetime: String): Int? = try {
    LocalDateTime.parse(datetime, DATE_TIME_FMT).hour
} catch (_: Exception) {
    null
}

fun extractMinute(datetime: String): Int? = try {
    LocalDateTime.parse(datetime, DATE_TIME_FMT).minute
} catch (_: Exception) {
    null
}

@Composable
fun CalendarPage(
    taskVM : TaskViewModel = viewModel(),
    authVM : AuthViewModel = viewModel(),
    navController: NavController,
) {

    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }

    val tasks by taskVM.tasks.collectAsState()
    LaunchedEffect(String) {
        taskVM.getTasksByUserId(currentUser?.userId ?: "")
    }

    var isExpanded by remember { mutableStateOf(false) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    var isLayoutGrid by remember { mutableStateOf(false) }

    val monthHeader = currentDate.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH))

    // === NEW: pager state để đổi tuần khi vuốt ở TimeSlots ===
    val initialPage = 1000
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })
    val baseMonday = remember {
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
    val scope = rememberCoroutineScope()

    // Đồng bộ currentDate với trang pager
    LaunchedEffect(pagerState.currentPage) {
        val offset = pagerState.currentPage - initialPage
        currentDate = baseMonday.plusWeeks(offset.toLong())
        // Nếu đang chọn ngày không thuộc tuần mới, bạn có thể reset selectedDate về thứ 2 của tuần:
        // selectedDate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showSheet = true
                },
                containerColor = Color(0xFF333333),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event"
                )
            }
        }


    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
//                .padding(16.dp)
        ) {
            CreateNewTaskPersonal(
                navController = navController,
                showSheet = showSheet,
                projectId = "",
                taskType = when ("To Do") {
                    "To Do" -> 1
                    "In Progress" -> 2
                    "Done" -> 3
                    else -> 0
                },
                onDismiss = { showSheet = false },
            )
            // Header with month and navigation
            CalendarHeader(
                currentDate = monthHeader,
                isExpanded = isExpanded,
                onExpandToggle = { isExpanded = !isExpanded },
                isGridLayout = isLayoutGrid,
                onGridLayout = { isLayoutGrid = !isLayoutGrid }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Show week view when collapsed, full month when expanded
            if (isExpanded) {
                MonthView(
                    currentDate = currentDate,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    onExpandToggle = {

                    },
                    onPreviousClick = {
                        currentDate = if (isExpanded) {
                            currentDate.minusMonths(1)
                        } else {
                            currentDate.minusWeeks(1)
                        }
                    },
                    onNextClick = {
                        currentDate = if (isExpanded) {
                            currentDate.plusMonths(1)
                        } else {
                            currentDate.plusWeeks(1)
                        }
                    }
                )
            } else {
                WeekView(
                    currentDate = currentDate,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === NEW: TimeSlots được đặt trong HorizontalPager ===
            // ... bên trong CalendarPage, dưới WeekView
            val weekStart = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            val today = LocalDate.now()
//            val sampleTasks = remember {
//                listOf(
//                    Task(
//                        id = "t1",
//                        title = "Design review",
//                        dateStart = today.atTime(7, 30).format(ISO_MINUTE),   // 07:00
//                        dateDue   = today.atTime(10, 0).format(ISO_MINUTE),  // 10:00
//                        tags = listOf("meeting")
//                    )
//                )
//            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {
                TimeSlots(weekStart = weekStart, tasks = tasks)
            }


        }
    }
}

@Composable
fun CalendarHeader(
    currentDate: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    isGridLayout: Boolean,
    onGridLayout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDate,
//                text = currentDate.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            IconButton(onClick = onExpandToggle) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle calendar",
                    tint = Color.Gray
                )
            }
        }

        IconButton(onClick = {
            onGridLayout()
        }) {
            Icon(
                imageVector = if (isGridLayout) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Previous",
                tint = Color(0xFFFF4444)
            )
        }
    }
}

@Composable
fun WeekView(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(7) { dayIndex ->
            val date = startOfWeek.plusDays(dayIndex.toLong())
            val isSelected = date == selectedDate
            val isToday = date == LocalDate.now()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isToday) Color(0xFFF2F3F5) else Color.Transparent) // nền xám cho hôm nay
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("E",Locale.ENGLISH)),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFFF4444), CircleShape)
                            )
                        }
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MonthView(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onExpandToggle: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDate.format(
                        DateTimeFormatter.ofPattern(
                            "MMMM yyyy",
                            Locale.ENGLISH
                        )
                    ),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                IconButton(onClick = {
                    onExpandToggle()
                }) {
                    Icon(
                        imageVector = if (true) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle calendar",
                        tint = Color.Gray
                    )
                }
            }

            Row {
                IconButton(onClick = {
                    onPreviousClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous",
                        tint = Color(0xFFFF4444)
                    )
                }
                IconButton(onClick = {
                    onNextClick()
                }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = Color(0xFFFF4444)
                    )
                }
            }
        }

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar grid
        val firstDayOfMonth = currentDate.withDayOfMonth(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = currentDate.lengthOfMonth()

        var dayCounter = 1 - firstDayOfWeek

        repeat(6) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { day ->
                    val currentDay = dayCounter
                    val isValidDay = currentDay in 1..daysInMonth
                    val date = if (isValidDay) currentDate.withDayOfMonth(currentDay) else null
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(enabled = isValidDay) {
                                date?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = Color(0xFFFF4444),
                                        shape = CircleShape
                                    )
                            )
                        }

                        if (isValidDay) {
                            Text(
                                text = currentDay.toString(),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(R.font.monasan_sb)),
                                    lineHeight = 18.sp
                                ),
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = if (isSelected || isToday) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                    dayCounter++
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun TimeSlots(weekStart: LocalDate, tasks: List<Task>) {
    val rowHeight = 60.dp
    val timeLabelWidth = 30.dp
    val contentStartPad = 4.dp

    val listState: LazyListState = rememberLazyListState()
    val density = LocalDensity.current
    var viewportHeightPx by remember { mutableStateOf(0) }
    var now by remember { mutableStateOf(LocalTime.now()) }


    val today = LocalDate.now()
    val isTodayInThisWeek = !today.isBefore(weekStart) && !today.isAfter(weekStart.plusDays(6))
    val dayIndex = if (isTodayInThisWeek) ((today.dayOfWeek.value + 6) % 7) else null

    val tasksByDay: Map<Int, List<Pair<Task, Pair<LocalDateTime, LocalDateTime>>>> =
        remember(tasks, weekStart) {
            val weekEnd = weekStart.plusDays(6)
            val days = (0..6).map { d -> weekStart.plusDays(d.toLong()) }

            buildMap<Int, MutableList<Pair<Task, Pair<LocalDateTime, LocalDateTime>>>> {
                for (task in tasks.map(::normalizeTask)) {
                    var s = task.startLdt()
                    var e = task.endLdt()
                    // Sửa data sai: nếu end < start, hoán đổi (hoặc bỏ qua)
                    if (e.isBefore(s)) e = s

                    // Bỏ task hoàn toàn ngoài tuần
                    if (e.toLocalDate().isBefore(weekStart) || s.toLocalDate()
                            .isAfter(weekEnd)
                    ) continue

                    // CẮT theo từng ngày trong tuần để vẽ mỗi ngày một đoạn (segment)
                    for ((dayIndex0, dayDate) in days.withIndex()) {
                        val dayStart = dayDate.atTime(0, 0)
                        val dayEnd = dayDate.atTime(23, 59)

                        val segStart = maxOf(s, dayStart)
                        val segEnd = minOf(e, dayEnd)

                        if (!segEnd.isBefore(segStart)) {
                            val list = getOrPut(dayIndex0) { mutableListOf() }
                            list += task to (segStart to segEnd)
                        }
                    }
                }
            }
        }


    // Tổng scroll hiện tại theo px (để trừ ra vị trí trong viewport)
    val scrollYPx by remember {
        derivedStateOf {
            val rowPx = with(density) { rowHeight.toPx() }
            listState.firstVisibleItemIndex * rowPx + listState.firstVisibleItemScrollOffset
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { viewportHeightPx = it.height }
    ) {
        // 1) Phần nền: LazyColumn (label giờ + line + cột hôm nay + vạch giờ hiện tại)
        LazyColumn(
            state = listState,
            modifier = Modifier.matchParentSize()
        ) {
            items((0..23).toList()) { hour ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                ) {
                    val contentWidth = maxWidth - timeLabelWidth - contentStartPad
                    val dayWidth = contentWidth / 7

                    // Cột xám hôm nay
                    if (dayIndex != null) {
                        val left = timeLabelWidth + contentStartPad + dayWidth * dayIndex
                        Box(
                            modifier = Modifier
                                .offset(x = left)
                                .width(dayWidth)
                                .fillMaxHeight()
                                .background(Color(0xFFF2F3F5))
                        )
                    }

                    // Label giờ + line mờ
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp)
                            .padding(start = contentStartPad),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%02dh", hour),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb)),
                                lineHeight = 18.sp
                            ),
                            color = Color.Gray,
                            modifier = Modifier.width(timeLabelWidth)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.Gray.copy(alpha = 0.2f))
                        )
                    }

                    // Vạch giờ hiện tại (vẫn vẽ trong row tương ứng)
                    if (dayIndex != null && hour == now.hour) {
                        val y = rowHeight * (now.minute / 60f)
                        val left = timeLabelWidth + contentStartPad + dayWidth * dayIndex
                        Box(
                            modifier = Modifier
                                .offset(x = left, y = y)
                                .width(dayWidth)
                                .height(1.dp)
                                .background(Color.Black)
                        )
                        Box(
                            modifier = Modifier
                                .offset(x = left - 3.dp, y = y - 3.dp)
                                .size(6.dp)
                                .background(Color.Black, CircleShape)
                        )
                    }
                }
            }
        }

        // 2) Overlay: vẽ task LIỀN MẠCH theo các segment/ngày
        BoxWithConstraints(Modifier.matchParentSize()) {
            val maxW = maxWidth
            val contentWidth = maxW - timeLabelWidth - contentStartPad
            val dayWidth = contentWidth / 7
            val rowPx = with(density) { rowHeight.toPx() }

            tasksByDay.forEach { (idx, segments) ->
                val columnLeft = timeLabelWidth + contentStartPad + dayWidth * idx

                segments.forEach { (task, seg) ->
                    val (segStart, segEnd) = seg

                    val startYpx = (segStart.hour + segStart.minute / 60f) * rowPx
                    val endYpx = (segEnd.hour + segEnd.minute / 60f) * rowPx
                    val hPx = (endYpx - startYpx).coerceAtLeast(1f)

                    // Cắt theo viewport để tối ưu
                    val viewTop = 0f
                    val viewBot = viewportHeightPx.toFloat()
                    val yInViewTop = startYpx - scrollYPx
                    val yInViewBot = endYpx - scrollYPx
                    if (yInViewBot < viewTop || yInViewTop > viewBot) return@forEach

                    val color = when (task.priority) {
                        TaskPriority.HIGH -> Color(0xFFFFCDD2)
                        TaskPriority.MEDIUM -> Color(0xFFFFF59D)
                        TaskPriority.LOW -> Color(0xFFC8E6C9)
                    }

                    Box(
                        modifier = Modifier
                            .offset(
                                x = columnLeft + 4.dp,
                                y = with(density) { (startYpx - scrollYPx).toDp() }
                            )
                            .width(dayWidth - 8.dp)
                            .height(with(density) { hPx.toDp() })
                            .background(color, RoundedCornerShape(8.dp))
                            .padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            ) // padding mỏng để còn chỗ cho chữ
                    ) {
                        // 1 dòng + marquee để hiển thị hết tiêu đề
                        // ... bên trong Box() của mỗi task segment:
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 12.sp),
                            color = Color.Black,                 // hoặc White nếu nền đậm
                            maxLines = Int.MAX_VALUE,            // cho phép xuống nhiều dòng
                            overflow = TextOverflow.Clip,        // không ellipsis, không marquee
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


/**
 * Scroll list sao cho vạch phút hiện tại nằm giữa viewport.
 */
private suspend fun centerNow(
    listState: LazyListState,
    viewportHeightPx: Int,
    rowHeight: Dp,
    now: LocalTime,
    density: androidx.compose.ui.unit.Density
) {
    val rowPx = with(density) { rowHeight.toPx() }
    val minutePx = rowPx * (now.minute / 60f)

    // Muốn: đỉnh của item "giờ hiện tại" ở vị trí sao cho (minutePx) trùng giữa viewport
    var targetIndex = now.hour
    var offsetPx = (viewportHeightPx / 2f - minutePx - rowPx / 2f).roundToInt()

    // Nếu offset âm, dịch lên các item trước để offset >= 0
    if (offsetPx < 0) {
        val need = -offsetPx
        val shift = ceil(need / rowPx).toInt()
        targetIndex = max(0, targetIndex - shift)
        offsetPx += (rowPx * shift).roundToInt()
    }

    listState.animateScrollToItem(targetIndex, offsetPx)
}


@Composable
@Preview
fun CalendarPagePreview() {
    To_do_appTheme {
//        CalendarPage()
    }
}

val sampleTasks = listOf(
    Task(
        id = "task_001",
        title = "Thiết kế giao diện trang chủ",
        description = "Thiết kế UI/UX cho trang chủ ứng dụng quản lý công việc",
        project = "project_123",
        assignee = listOf("user_001", "user_002"),
        creator = "user_admin",
        status = TaskStatus.IN_PROGRESS,
        priority = TaskPriority.HIGH,
        dateStart = "2025-08-13 09:00",
        dateDue = "2025-08-13 18:00",
        tags = listOf("UI", "Frontend", "Quan trọng"),
        subTask = listOf(
            Task(
                id = "task_001_1",
                title = "Thiết kế Header",
                description = "Tạo phần header với logo và thanh điều hướng",
                project = "project_123",
                assignee = listOf("user_001"),
                creator = "user_admin",
                status = TaskStatus.PENDING,
                priority = TaskPriority.MEDIUM,
                dateStart = "2025-08-13 09:00",
                dateDue = "2025-08-13 12:00",
                tags = listOf("UI", "Header"),
                subTask = emptyList()
            )
        )
    ),
    Task(
        id = "task_002",
        title = "Phát triển API đăng nhập",
        description = "Xây dựng API REST cho tính năng đăng nhập và đăng ký",
        project = "project_123",
        assignee = listOf("user_003"),
        creator = "user_admin",
        status = TaskStatus.PENDING,
        priority = TaskPriority.HIGH,
        dateStart = "2025-08-02 08:30",
        dateDue = "2025-08-06 17:30",
        tags = listOf("Backend", "Auth"),
        subTask = emptyList()
    ),
    Task(
        id = "task_003",
        title = "Viết tài liệu hướng dẫn sử dụng",
        description = "Tạo file PDF hướng dẫn cho người dùng mới",
        project = "project_123",
        assignee = listOf("user_004"),
        creator = "user_admin",
        status = TaskStatus.COMPLETED,
        priority = TaskPriority.LOW,
        dateStart = "2025-08-15 10:00",
        dateDue = "2025-08-15 14:00",
        tags = listOf("Document", "UserGuide"),
        subTask = emptyList()
    ),
    Task(
        id = "task_004",
        title = "Kiểm thử tính năng thanh toán",
        description = "Test tính năng thanh toán qua ZaloPay và MoMo",
        project = "project_123",
        assignee = listOf("user_005", "user_006"),
        creator = "user_admin",
        status = TaskStatus.IN_PROGRESS,
        priority = TaskPriority.MEDIUM,
        dateStart = "2025-08-05 09:30",
        dateDue = "2025-08-09 15:00",
        tags = listOf("Testing", "Payment"),
        subTask = listOf(
            Task(
                id = "task_004_1",
                title = "Test ZaloPay",
                description = "Kiểm thử thanh toán qua ZaloPay",
                project = "project_123",
                assignee = listOf("user_005"),
                creator = "user_admin",
                status = TaskStatus.PENDING,
                priority = TaskPriority.MEDIUM,
                dateStart = "2025-08-05 09:30",
                dateDue = "2025-08-07 12:00",
                tags = listOf("Payment", "ZaloPay"),
                subTask = emptyList()
            )
        )
    )
)
