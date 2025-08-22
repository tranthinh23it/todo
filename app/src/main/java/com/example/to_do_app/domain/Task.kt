package com.example.to_do_app.domain

import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus

data class Task(
    var id : String="",
    var title : String ="",
    var description: String ="",
    var project: String ="",
    var assignee: List<String> = emptyList(), // Sửa ở đây
    var creator : String ="",
    var status : TaskStatus = TaskStatus.IN_PROGRESS,
    var priority : TaskPriority = TaskPriority.HIGH,
    var dateStart : String = "",
    var dateDue : String ="",
    var tags : List<String> = emptyList(),
    var subTask: List<Task> = emptyList(),
    var type : String ="",
)