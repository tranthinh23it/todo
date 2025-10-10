package com.example.to_do_app.domain

import com.google.firebase.Timestamp

data class TaskActivity(
    var id: String = "",
    var projectId: String = "",
    var taskId: String = "",
    var action: String = "",                 // "ASSIGNED", "STATUS_CHANGED", "COMMENTED", "UPDATED"
    var note: String = "",
    var worker: String = "",                 // uid người thao tác
    var timestamp: Timestamp? = null,        // ưu tiên Timestamp
    var notifiedUserIds: List<String> = emptyList()
) 