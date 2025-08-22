package com.example.to_do_app.presentation.screens.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.components.CategoryTopAppBar
import com.example.to_do_app.ui.theme.To_do_appTheme

// Data classes for report data
data class FocusTimeData(
    val period: String,
    val totalHours: String,
    val avgHours: String,
    val totalSessions: String,
    val chartData: List<Float>,
    val chartLabels: List<String>
)

data class HeatmapData(
    val period: String,
    val productiveHours: List<Int>,
    val heatmapGrid: Map<String, List<Int>>
)

// Sample data
fun getFocusTimeData(period: String): FocusTimeData {
    return when (period) {
        "Weekly" -> FocusTimeData(
            period = "Weekly",
            totalHours = "46.2 hr",
            avgHours = "6.6 hr",
            totalSessions = "109",
            chartData = listOf(8f, 6f, 4f, 7f, 5f, 3f, 2f),
            chartLabels = listOf("14", "15", "16", "17", "18", "19", "20")
        )
        "Monthly" -> FocusTimeData(
            period = "Monthly",
            totalHours = "184.5 hr",
            avgHours = "6.1 hr",
            totalSessions = "420",
            chartData = listOf(25f, 30f, 28f, 35f, 32f, 29f, 27f),
            chartLabels = listOf("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7")
        )
        "Yearly" -> FocusTimeData(
            period = "Yearly",
            totalHours = "2,208 hr",
            avgHours = "6.0 hr",
            totalSessions = "5,040",
            chartData = listOf(180f, 195f, 210f, 185f, 200f, 175f, 190f),
            chartLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul")
        )
        else -> getFocusTimeData("Weekly")
    }
}

fun getHeatmapData(period: String): HeatmapData {
    return when (period) {
        "Weekly" -> HeatmapData(
            period = "Weekly",
            productiveHours = listOf(1, 2, 4, 4, 4, 4, 0, 3, 4, 4, 4, 4),
            heatmapGrid = mapOf(
                "Mon" to listOf(4, 1, 3, 1, 3, 3, 3, 0, 3, 4, 4, 4),
                "Tue" to listOf(2, 4, 4, 2, 4, 4, 4, 4, 4, 4, 4, 4),
                "Wed" to listOf(4, 2, 3, 2, 3, 3, 3, 0, 3, 4, 4, 2),
                "Thu" to listOf(4, 1, 3, 1, 3, 3, 3, 0, 3, 4, 4, 1)
            )
        )
        "Monthly" -> HeatmapData(
            period = "Monthly",
            productiveHours = listOf(2, 3, 4, 4, 3, 4, 1, 2, 4, 3, 4, 3),
            heatmapGrid = mapOf(
                "Mon" to listOf(3, 2, 4, 3, 2, 4, 4, 1, 4, 3, 3, 4),
                "Tue" to listOf(4, 3, 3, 4, 3, 3, 3, 2, 3, 4, 4, 3),
                "Wed" to listOf(2, 4, 2, 3, 4, 2, 4, 0, 2, 3, 3, 4),
                "Thu" to listOf(3, 2, 4, 2, 3, 4, 2, 1, 4, 3, 4, 2),
                "Fri" to listOf(4, 4, 3, 4, 2, 3, 4, 3, 3, 4, 2, 3),
                "Sat" to listOf(2, 3, 4, 1, 4, 3, 2, 4, 3, 2, 4, 3),
                "Sun" to listOf(1, 2, 3, 3, 2, 4, 3, 2, 4, 3, 3, 4)
            )
        )
        "Yearly" -> HeatmapData(
            period = "Yearly",
            productiveHours = listOf(3, 3, 4, 3, 4, 3, 2, 3, 4, 3, 3, 4),
            heatmapGrid = mapOf(
                "Mon" to listOf(3, 4, 3, 2, 4, 3, 3, 2, 3, 4, 3, 3),
                "Tue" to listOf(4, 3, 4, 3, 3, 4, 2, 3, 4, 3, 4, 3),
                "Wed" to listOf(2, 4, 3, 4, 2, 3, 4, 1, 3, 4, 2, 4),
                "Thu" to listOf(3, 3, 4, 3, 4, 2, 3, 2, 4, 3, 3, 3),
                "Fri" to listOf(4, 2, 3, 4, 3, 4, 2, 4, 3, 2, 4, 3),
                "Sat" to listOf(2, 4, 2, 3, 4, 3, 4, 3, 2, 4, 3, 2),
                "Sun" to listOf(3, 3, 4, 2, 3, 4, 3, 4, 3, 3, 4, 3)
            )
        )
        else -> getHeatmapData("Weekly")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPage() {
    var selectedPeriod by remember { mutableStateOf("Weekly") }
    var selectedHeatmapPeriod by remember { mutableStateOf("Monthly") }
    
    Scaffold(
        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Report",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { /* TODO */ }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* TODO */ }) {
//                        Icon(
//                            imageVector = Icons.Default.MoreVert,
//                            contentDescription = "More"
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White
//                )
//            )
            CategoryTopAppBar(
                text = "Report",
                onBackClick = { /* TODO: Handle back navigation */ },
                icon = Icons.Default.MoreVert,
                onClick = { /* TODO: Handle icon click */ }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues).background(Color(0xFFf5f5f5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                FocusTimeSection(
                    selectedPeriod = selectedPeriod,
                    onPeriodChange = { selectedPeriod = it }
                )
            }
            
            item {
                HeatmapSection(
                    selectedPeriod = selectedHeatmapPeriod,
                    onPeriodChange = { selectedHeatmapPeriod = it }
                )
            }
        }
    }
}

@Composable
fun FocusTimeSection(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Focus Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                
                PeriodDropdown(
                    selectedPeriod = selectedPeriod,
                    onPeriodChange = onPeriodChange
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ReportStatItem("46.2 hr", "Total Focus Time")
                ReportStatItem("6.6 hr", "Avg Focus Time")
                ReportStatItem("109", "Total Sessions")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Chart
            FocusTimeChart()
        }
    }
}

@Composable
fun ReportStatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FocusTimeChart() {
    val chartData = listOf(8f, 6f, 4f, 7f, 5f, 3f, 2f)
    val days = listOf("14", "15", "16", "17", "18", "19", "20")
    val maxValue = chartData.maxOrNull() ?: 1f
    
    Column {
        // Y-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            // Y-axis
            Column(
                modifier = Modifier.width(30.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("8h", fontSize = 10.sp, color = Color(0xFF999999))
                Text("6h", fontSize = 10.sp, color = Color(0xFF999999))
                Text("4h", fontSize = 10.sp, color = Color(0xFF999999))
                Text("2h", fontSize = 10.sp, color = Color(0xFF999999))
                Text("0h", fontSize = 10.sp, color = Color(0xFF999999))
            }
            
            // Chart bars
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEachIndexed { index, value ->
                    val height = (value / maxValue * 140).dp
                    val isHighlighted = index == 0 // Highlight first bar
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(height)
                                .background(
                                    if (isHighlighted) Color(0xFFFF4040) else Color(0xFFFFB3B3),
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        
                        if (isHighlighted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFFF4040), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${value.toInt()}h",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.width(30.dp))
            days.forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HeatmapSection(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit
) {
    val heatmapData = remember(selectedPeriod) { getHeatmapData(selectedPeriod) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Heatmap",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                
                PeriodDropdown(
                    selectedPeriod = selectedPeriod,
                    onPeriodChange = onPeriodChange
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Productive Hours label
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productive Hours",
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
                Spacer(modifier = Modifier.width(8.dp))
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Productive hours row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                heatmapData.productiveHours.forEach { intensity ->
                    val color = when (intensity) {
                        0 -> Color(0xFFFFE5E5)
                        1 -> Color(0xFFFFB3B3)
                        2 -> Color(0xFFFF8080)
                        3 -> Color(0xFFFF6666)
                        else -> Color(0xFFFF4040)
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Time labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val timeLabels = listOf("6 AM", "9 AM", "12 AM", "3 PM", "15 PM", "18 PM")
                timeLabels.forEach { time ->
                    Text(
                        text = time,
                        fontSize = 10.sp,
                        color = Color(0xFF999999),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Heatmap Chart label
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Heatmap Chart",
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
                Spacer(modifier = Modifier.width(8.dp))
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Heatmap grid
            HeatmapGrid(heatmapData.heatmapGrid)
        }
    }
}

@Composable
fun HeatmapGrid(heatmapGrid: Map<String, List<Int>>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        heatmapGrid.forEach { (day, dayData) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = day,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.width(30.dp)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dayData.forEach { intensity ->
                        val color = when (intensity) {
                            0 -> Color(0xFFFFE5E5)
                            1 -> Color(0xFFFFB3B3)
                            2 -> Color(0xFFFF8080)
                            3 -> Color(0xFFFF6666)
                            else -> Color(0xFFFF4040)
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(color, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodDropdown(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val periods = listOf("Weekly", "Monthly", "Yearly")
    
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF666666)
            ),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = selectedPeriod,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                modifier = Modifier.size(16.dp)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            periods.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period) },
                    onClick = {
                        onPeriodChange(period)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportPagePreview() {
    To_do_appTheme {
        ReportPage()
    }
}
