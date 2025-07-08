package com.example.voglioisoldi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.voglioisoldi.data.database.entities.Notification

@Dao
interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: Notification)

    @Query("SELECT * FROM notification WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getNotificationsByUser(userId: Int): List<Notification>

    @Query("SELECT * FROM notification WHERE userId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    suspend fun getUnreadNotifications(userId: Int): List<Notification>

    @Query("UPDATE notification SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: Int)

    @Query("UPDATE notification SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Int)

    @Query("DELETE FROM notification WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Int)

    @Query("SELECT COUNT(*) FROM notification WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadCount(userId: Int): Int
}