package com.example.to_do_app.domain

import android.icu.text.CaseMap.Title
import com.google.firebase.Timestamp

data class Notification(
    var id: String = "",
    var recipientId: String = "",      // userId của người nhận
    var type: String = "",
    var title: String ="",// "TASK_ASSIGNED", "TASK_UPDATED", ...
    var message: String = "",
    var time: Timestamp? = null,             // set từ serverTimestamp ở Cloud Functions
    var seen: Boolean = false,
    var projectId: String = "",
    var taskId: String = "",
    var activityId: String = "",
    var deeplink: String = "",               // ví dụ: todoapp://projects/{p}/tasks/{t}
    var expireAt: Timestamp? = null          // để bật TTL auto cleanup
    // var recipient: String = "" // KHÔNG cần nếu lưu dưới /users/{uid}/notifications
)
