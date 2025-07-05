package com.example.voglioisoldi.data.repositories

import com.example.voglioisoldi.data.database.dao.UserDao
import com.example.voglioisoldi.data.database.entities.User
import java.security.MessageDigest

class UserRepository(
    private val dao: UserDao
) {
    suspend fun insertUser(user: User) {
        dao.insertUser(user)
    }

    suspend fun userExists(username: String, email: String): Boolean {
        return dao.existsUser(username, email) > 0
    }

    suspend fun getUser(username: String, password: String): User? {
        val passwordHash = hashPassword(password)
        return dao.getUser(username, passwordHash)
    }
}
//TODO: Da spostare in una util??
fun hashPassword(password: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(password.toByteArray())
        .joinToString("") { "%02x".format(it) }
}