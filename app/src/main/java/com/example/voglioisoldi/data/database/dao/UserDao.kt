package com.example.voglioisoldi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.voglioisoldi.data.database.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE username = :username AND passwordHash = :passwordHash")
    suspend fun getUser(username: String, passwordHash: String): User?

    //Ci dice se l'utente già esiste in fase di registrazione
    @Query("SELECT COUNT(*) FROM user WHERE username = :username OR email = :email")
    suspend fun existsUser(username: String, email: String): Int

    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("UPDATE user SET email = :newEmail WHERE id = :userId")
    suspend fun updateUserEmail(userId: Int, newEmail: String)

    @Query("UPDATE user SET passwordHash = :newPasswordHash WHERE id = :userId")
    suspend fun updateUserPassword(userId: Int, newPasswordHash: String)

    @Query("UPDATE user SET profilePictureUri = :imageUri WHERE id = :userId")
    suspend fun updateUserProfilePicture(userId: Int, imageUri: String?)

    @Query("DELETE FROM user WHERE id = :userId")
    suspend fun deleteUser(userId: Int)

    @Query("SELECT COUNT(*) FROM user WHERE email = :email AND id != :userId")
    suspend fun isEmailTaken(email: String, userId: Int): Int
}