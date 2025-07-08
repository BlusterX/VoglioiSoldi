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
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val balance: Double,
    val userId: Int,
    val createdAt: Long = System.currentTimeMillis()
)