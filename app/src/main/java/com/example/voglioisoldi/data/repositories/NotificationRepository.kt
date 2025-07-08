package com.example.voglioisoldi.data.repositories

import com.example.voglioisoldi.data.database.dao.NotificationDao
import com.example.voglioisoldi.data.database.entities.Notification

class NotificationRepository(
    private val dao: NotificationDao
) {
    suspend fun insertNotification(notification: Notification) = dao.insertNotification(notification)

    suspend fun getNotificationsByUser(userId: Int): List<Notification> = dao.getNotificationsByUser(userId)

    suspend fun getUnreadNotifications(userId: Int): List<Notification> = dao.getUnreadNotifications(userId)

    suspend fun markAsRead(notificationId: Int) = dao.markAsRead(notificationId)

    suspend fun markAllAsRead(userId: Int) = dao.markAllAsRead(userId)

    suspend fun deleteNotification(notificationId: Int) = dao.deleteNotification(notificationId)

    suspend fun getUnreadCount(userId: Int): Int = dao.getUnreadCount(userId)
}