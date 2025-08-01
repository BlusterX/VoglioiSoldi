package com.example.voglioisoldi.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.voglioisoldi.data.database.dao.AccountDao
import com.example.voglioisoldi.data.database.dao.NotificationDao
import com.example.voglioisoldi.data.database.dao.TransactionDao
import com.example.voglioisoldi.data.database.dao.UserDao
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.data.database.entities.Notification
import com.example.voglioisoldi.data.database.entities.Transaction
import com.example.voglioisoldi.data.database.entities.User

@Database(
    entities = [
        User::class,
        Transaction::class,
        Account::class,
        Notification::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDAO() : UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun notificationDao(): NotificationDao
}