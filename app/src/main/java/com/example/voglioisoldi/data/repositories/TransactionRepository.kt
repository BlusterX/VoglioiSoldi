package com.example.voglioisoldi.data.repositories

import com.example.voglioisoldi.data.database.dao.TransactionDao
import com.example.voglioisoldi.data.database.entities.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val dao: TransactionDao
) {
    suspend fun insertTransaction(transaction: Transaction) = dao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) = dao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) = dao.deleteTransaction(transaction)

    fun getTransactionsByUser(userId: Int): Flow<List<Transaction>> {
        return dao.getTransactionsByUser(userId)
    }
}