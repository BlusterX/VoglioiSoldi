package com.example.voglioisoldi.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.voglioisoldi.data.database.entities.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert
    suspend fun insertAccount(account: Account): Long

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("SELECT * FROM account WHERE userId = :userId")
    fun getAccountsByUser(userId: Int): Flow<List<Account>>

    @Query("SELECT * FROM account WHERE id = :accountId")
    suspend fun getAccountById(accountId: Int): Account?
}