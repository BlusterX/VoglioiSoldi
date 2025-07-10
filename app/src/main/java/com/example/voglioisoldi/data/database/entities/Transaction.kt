package com.example.voglioisoldi.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val category: String,
    val description: String? = null,
    val date: Long = System.currentTimeMillis(),
    val userId: Int,
    val accountId: Int,
    val isRecurring: Boolean = false,
    val recurringPeriodMinutes: Int? = null,
    val isRecurringActive: Boolean = true,
    val lastExecutionDate: Long? = null
)