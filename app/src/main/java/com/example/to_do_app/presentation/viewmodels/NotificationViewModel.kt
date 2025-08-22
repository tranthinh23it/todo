package com.example.to_do_app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_app.data.NotificationRepo
import com.example.to_do_app.domain.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val repo = NotificationRepo()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _selectedNotification = MutableStateFlow<Notification?>(null)
    val selectedNotification: StateFlow<Notification?> = _selectedNotification

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllNotifications() {
        viewModelScope.launch {
            val result = repo.getAllNotifications()
            _notifications.value = result
        }
    }

    fun getNotificationById(notificationId: String) {
        viewModelScope.launch {
            val notification = repo.getNotificationById(notificationId)
            _selectedNotification.value = notification
        }
    }

    fun getNotificationsByRecipient(recipient: String) {
        viewModelScope.launch {
            val result = repo.getNotificationsByRecipient(recipient)
            _notifications.value = result
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch {
            val result = repo.addNotification(notification)
            if (result.isSuccess) {
                loadAllNotifications()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun updateNotification(notification: Notification) {
        viewModelScope.launch {
            val result = repo.updateNotification(notification)
            if (result.isSuccess) {
                loadAllNotifications()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val result = repo.deleteNotification(notificationId)
            if (result.isSuccess) {
                loadAllNotifications()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 