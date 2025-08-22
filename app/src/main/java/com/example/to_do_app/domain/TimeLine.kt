package com.example.to_do_app.domain

import androidx.compose.ui.graphics.Color

data class TimeLine(
    val id: String = "",
    val title: String = "",
    val startDate: String ="",
    val endDate: String="",
    val events: List<TimeEvent> = emptyList(),
    val projectId : String="",
)
data class TimeEvent(
    val dayOfWeek: String="",
    val startTime: String="",
    val endTime: String="",
    val title: String="",
    val description: String = "",
    val type: Int = 0
)



