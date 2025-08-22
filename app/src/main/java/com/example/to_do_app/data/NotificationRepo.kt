package com.example.to_do_app.data

import android.util.Log
import com.example.to_do_app.domain.Notification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NotificationRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("notifications")

    suspend fun addNotification(notification: Notification): Result<Unit> {
        return try {
            collection.document(notification.id).set(notification).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Failed to add notification: ", e)
            Result.failure(e)
        }
    }

    suspend fun updateNotification(notification: Notification): Result<Unit> {
        return try {
            collection.document(notification.id).set(notification).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Failed to update notification: ", e)
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            collection.document(notificationId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Failed to delete notification: ", e)
            Result.failure(e)
        }
    }

    suspend fun getNotificationById(notificationId: String): Notification? {
        return try {
            val doc = collection.document(notificationId).get().await()
            doc.toObject(Notification::class.java)
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Failed to get notification by id: ", e)
            null
        }
    }

    suspend fun getAllNotifications(): List<Notification> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Failed to get all notifications: ", e)
            emptyList()
        }
    }

    suspend fun getNotificationsByRecipient(recipient: String): List<Notification> {
        return try {
            val snapshot = collection.whereEqualTo("recipient", recipient).get().await()
            snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Failed to get notifications by recipient: ", e)
            emptyList()
        }
    }
} 