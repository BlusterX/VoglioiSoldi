package com.example.voglioisoldi.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.voglioisoldi.data.database.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUser(userId: Int): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM `transaction` WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("SELECT * FROM `transaction`")
    suspend fun getAllTransactions(): List<Transaction>
}