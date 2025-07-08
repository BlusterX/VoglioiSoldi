package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel

import com.example.voglioisoldi.data.database.entities.Notification
import com.example.voglioisoldi.data.repositories.NotificationRepository

data class NotificationState(
    val notifications: List<Notification> = emptyList(),
    val errorMessage: String = ""
)

interface NotificationActions {
    fun loadNotifications(userId: Int)
    fun markAsRead(notificationId: Int)
    fun markAllAsRead(userId: Int)
    fun deleteNotification(notificationId: Int)
    fun clearError()
}

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {


}