package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _logoutDone = MutableStateFlow(false)
    val logoutDone: StateFlow<Boolean> = _logoutDone

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun logout() {
        viewModelScope.launch {
            try {
                sessionManager.clearSession()
                _logoutDone.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Errore durante il logout"
            }
        }
    }

    fun resetLogoutFlag() {
        _logoutDone.value = false
    }

    fun resetError() {
        _errorMessage.value = null
    }
}