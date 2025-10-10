package com.example.to_do_app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.to_do_app.MainActivity
import com.example.to_do_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "task_notifications"
        private const val CHANNEL_NAME = "Task Notifications"
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        updateFcmToken(token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received: ${remoteMessage.data}")
        
        val data = remoteMessage.data
        val notification = remoteMessage.notification
        
        // Create notification channel
        createNotificationChannel()
        
        // Generate deep link
        val deepLink = generateDeepLink(data)
        
        // Show notification
        showNotification(notification?.title, notification?.body, deepLink)

        // Post event for in-app notification handling
        CoroutineScope(Dispatchers.Main).launch {
            NotificationBus.post(NotificationEvent(data, deepLink))
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task updates"
                enableVibration(true)
                setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification(title: String?, body: String?, deepLink: Uri?) {
        val notificationManager = NotificationManagerCompat.from(this)

        // Intent để mở ứng dụng khi nhấn vào thông báo
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data = deepLink
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Xây dựng thông báo
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title ?: "New Notification")
            .setContentText(body ?: "You have a new message.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Hiển thị thông báo
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        } else {
            Log.w(TAG, "Notification permission not granted")
        }
    }

    private fun generateDeepLink(data: Map<String, String>): Uri? {
        val deepLinkStr = data["deep_link"]?.takeIf { it.isNotBlank() } ?: return null
        return try {
            Uri.parse(deepLinkStr)
        } catch (e: Exception) {
            Log.w(TAG, "Invalid deep link: $deepLinkStr", e)
            null
        }
    }

    private fun updateFcmToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { Log.d(TAG, "FCM token updated") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to update token", e) }
    }
}

// Notification Event Bus
object NotificationBus {
    private val _events = kotlinx.coroutines.flow.MutableSharedFlow<NotificationEvent>()
    val events = _events.asSharedFlow()

    suspend fun post(event: NotificationEvent) {
        _events.emit(event)
    }
}

data class NotificationEvent(
    val data: Map<String, String>,
    val deepLink: Uri?,                 // <-- Uri? thay vì String
    val timestamp: Long = System.currentTimeMillis()
)
