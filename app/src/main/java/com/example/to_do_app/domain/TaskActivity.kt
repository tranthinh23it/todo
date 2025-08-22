package com.example.to_do_app.domain

data class TaskActivity(
    var id: String = "",
    var taskId: String = "",
    val notifiedUserIds: List<String> = emptyList(),
    var action: String = "",
    var timestamp: String = "",
    var note: String = "",
    var worker :String ="",
    var projectId : String ="",
) 