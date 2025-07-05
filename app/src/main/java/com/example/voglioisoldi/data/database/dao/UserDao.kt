package com.example.voglioisoldi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.voglioisoldi.data.database.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    //L'implementation di UserDao, da errore perchè non utilizziamo ancora tale funziona
    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE username = :username AND passwordHash = :passwordHash")
    suspend fun getUser(username: String, passwordHash: String): User?

    //Ci dice se l'utente già esiste in fase di registrazione
    @Query("SELECT COUNT(*) FROM user WHERE username = :username OR email = :email")
    suspend fun existsUser(username: String, email: String): Int
}