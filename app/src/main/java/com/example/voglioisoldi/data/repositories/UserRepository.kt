package com.example.voglioisoldi.data.repositories

import com.example.voglioisoldi.data.database.dao.UserDao
import com.example.voglioisoldi.data.database.entities.User
import java.security.MessageDigest

class UserRepository(
    private val dao: UserDao
) {
    suspend fun insertUser(user: User) = dao.insertUser(user)

    suspend fun userExists(username: String, email: String): Boolean {
        return dao.existsUser(username, email) > 0
    }

    suspend fun getUser(username: String, password: String): User? {
        val passwordHash = hashPassword(password)
        return dao.getUser(username, passwordHash)
    }

    suspend fun getUserByUsername(username: String): User? {
        return dao.getUserByUsername(username)
    }

    suspend fun getUserById(userId: Int): User? {
        return dao.getUserById(userId)
    }

    suspend fun updateUserEmail(userId: Int, newEmail: String): Result<Unit> {
        return try {
            // Verifica se l'email è già utilizzata da un altro utente
            if (dao.isEmailTaken(newEmail, userId) > 0) {
                Result.failure(Exception("Email già presente nel sistema."))
            } else {
                dao.updateUserEmail(userId, newEmail)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPassword(userId: Int, currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            // Verifica la password corrente
            val user = dao.getUserById(userId)
            if (user == null) {
                Result.failure(Exception("Utente non trovato."))
            } else {
                val currentPasswordHash = hashPassword(currentPassword)
                if (user.passwordHash != currentPasswordHash) {
                    Result.failure(Exception("Password attuale non corretta."))
                } else {
                    val newPasswordHash = hashPassword(newPassword)
                    dao.updateUserPassword(userId, newPasswordHash)
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfilePicture(userId: Int, imageUri: String?) =
        dao.updateUserProfilePicture(userId, imageUri)

    suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            dao.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

//TODO: Da spostare in una util??
fun hashPassword(password: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray())
        .joinToString("") { "%02x".format(it) }
}