package com.example.voglioisoldi.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.data.session.SessionManager
import org.koin.compose.getKoin

@Composable
fun rememberCurrentUserId(): Int? {
    val sessionManager: SessionManager = getKoin().get()
    val userRepository: UserRepository = getKoin().get()
    val username by sessionManager.getLoggedInUser().collectAsState(initial = null)
    val userIdState = produceState<Int?>(initialValue = null, username) {
        value = username?.let { userRepository.getUserByUsername(it)?.id }
    }
    return userIdState.value
}