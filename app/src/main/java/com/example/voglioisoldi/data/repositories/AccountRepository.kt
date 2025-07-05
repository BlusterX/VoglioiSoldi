package com.example.voglioisoldi.data.repositories

import com.example.voglioisoldi.data.database.dao.AccountDao
import com.example.voglioisoldi.data.database.entities.Account
import kotlinx.coroutines.flow.Flow

class AccountRepository(
    private val dao: AccountDao
) {
    suspend fun insertAccount(account: Account): Long = dao.insertAccount(account)

    suspend fun updateAccount(account: Account) = dao.updateAccount(account)

    suspend fun deleteAccount(account: Account) = dao.deleteAccount(account)

    fun getAccountsByUser(userId: Int): Flow<List<Account>> {
        return dao.getAccountsByUser(userId)
    }

    suspend fun getAccountById(accountId: Int): Account? {
        return dao.getAccountById(accountId)
    }
}