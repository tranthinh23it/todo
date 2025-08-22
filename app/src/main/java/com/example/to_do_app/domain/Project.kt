package com.example.to_do_app.domain

import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus


data class Project(
    val id: String="",
    val name: String="",
    val description: String="",
    val team : String = "",
    val members: List<String> = emptyList(),
    val progress : Float = 0f,
    val dateStart : String = "",
    val dateEnd : String ="",
    val status: TaskStatus = TaskStatus.PENDING,
    val priority: TaskPriority = TaskPriority.LOW,
)

