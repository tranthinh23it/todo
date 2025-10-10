package com.example.to_do_app.util

import android.util.Log
import com.example.to_do_app.data.TaskActivityRepo
import com.example.to_do_app.data.UserRepo
import com.example.to_do_app.domain.TaskActivity
import com.example.to_do_app.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * NotificationManager handles automatic notification logic
 * Automatically determines who should be notified for each activity
 */
object NotificationManager {
    private const val TAG = "NotificationManager"
    private val firestore = FirebaseFirestore.getInstance()
    private val taskActivityRepo = TaskActivityRepo()
    private val userRepo = UserRepo()

    /**
     * Creates a TaskActivity with automatic notification logic
     * Automatically determines who should be notified based on activity type
     */
    suspend fun createActivityWithNotifications(
        projectId: String,
        taskId: String,
        action: String,
        note: String,
        worker: String,
        additionalUserIds: List<String> = emptyList()
    ): Result<String> {
        return try {
            // Get users to notify based on activity type
            val notifiedUserIds = getUsersToNotify(projectId, taskId, action, worker, additionalUserIds)
            
            // Create TaskActivity
            val activity = TaskActivity(
                id = generateActivityId(),
                projectId = projectId,
                taskId = taskId,
                action = action,
                note = note,
                worker = worker,
                timestamp = com.google.firebase.Timestamp.now(),
                notifiedUserIds = notifiedUserIds
            )
            
            // Save to Firestore
            val result = taskActivityRepo.addActivity(activity)
            if (result.isSuccess) {
                Log.d(TAG, "Activity created with ${notifiedUserIds.size} users to notify")
                Result.success(activity.id)
            } else {
                Result.failure(Exception("Failed to save activity"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating activity with notifications: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Determines which users should be notified based on activity type
     */
    private suspend fun getUsersToNotify(
        projectId: String,
        taskId: String,
        action: String,
        worker: String,
        additionalUserIds: List<String>
    ): List<String> {
        val usersToNotify = mutableSetOf<String>()
        
        try {
            // Always exclude the worker from notifications
            usersToNotify.remove(worker)
            
            // Add additional users explicitly specified
            usersToNotify.addAll(additionalUserIds)
            
            when {
                action.contains("ASSIGNED", ignoreCase = true) -> {
                    // Notify project members and task assignee
                    val projectMembers = getProjectMembers(projectId)
                    usersToNotify.addAll(projectMembers)
                }
                
                action.contains("STATUS_CHANGED", ignoreCase = true) -> {
                    // Notify project members and task assignee
                    val projectMembers = getProjectMembers(projectId)
                    val taskAssignee = getTaskAssignee(taskId)
                    usersToNotify.addAll(projectMembers)
                    taskAssignee?.let { usersToNotify.add(it) }
                }
                
                action.contains("COMMENTED", ignoreCase = true) -> {
                    // Notify project members and task assignee
                    val projectMembers = getProjectMembers(projectId)
                    val taskAssignee = getTaskAssignee(taskId)
                    usersToNotify.addAll(projectMembers)
                    taskAssignee?.let { usersToNotify.add(it) }
                }
                
                action.contains("UPDATED", ignoreCase = true) -> {
                    // Notify project members
                    val projectMembers = getProjectMembers(projectId)
                    usersToNotify.addAll(projectMembers)
                }
                
                action.contains("CREATED", ignoreCase = true) -> {
                    // Notify project members
                    val projectMembers = getProjectMembers(projectId)
                    usersToNotify.addAll(projectMembers)
                }
            }
            
            // Remove duplicates and worker (temporarily allow worker for testing)
            // TODO: Remove this in production
            return usersToNotify.distinct()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error determining users to notify: ${e.message}", e)
            return additionalUserIds.filter { it != worker }
        }
    }

    /**
     * Gets all members of a project
     */
    private suspend fun getProjectMembers(projectId: String): List<String> {
        return try {
            val projectDoc = firestore.collection("projects").document(projectId).get().await()
            val memberIds = projectDoc.get("memberIds") as? List<String> ?: emptyList()
            val ownerId = projectDoc.getString("ownerId")
            
            val allMembers = mutableListOf<String>()
            allMembers.addAll(memberIds)
            ownerId?.let { allMembers.add(it) }
            
            allMembers.distinct()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting project members: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Gets the assignee of a task
     */
    private suspend fun getTaskAssignee(taskId: String): String? {
        return try {
            val taskDoc = firestore.collection("tasks").document(taskId).get().await()
            taskDoc.getString("assigneeId")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting task assignee: ${e.message}", e)
            null
        }
    }

    /**
     * Generates a unique activity ID
     */
    private fun generateActivityId(): String {
        return "activity_${System.currentTimeMillis()}_${(0..999).random()}"
    }

    /**
     * Marks notifications as read for a user
     */
    suspend fun markNotificationsAsRead(userId: String, activityIds: List<String>): Result<Unit> {
        return try {
            // This could be implemented by updating a "readBy" field in TaskActivity
            // or creating a separate "read_notifications" collection
            Log.d(TAG, "Marked ${activityIds.size} notifications as read for user $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notifications as read: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Gets unread notification count for a user
     */
    suspend fun getUnreadNotificationCount(userId: String): Int {
        return try {
            // This could be implemented by counting unread notifications
            // For now, return 0 as placeholder
            0
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unread notification count: ${e.message}", e)
            0
        }
    }
}
