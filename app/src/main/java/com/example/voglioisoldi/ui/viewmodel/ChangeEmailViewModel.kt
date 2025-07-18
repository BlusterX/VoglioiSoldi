package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.ui.util.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangeEmailState(
    val currentEmail: String = "",
    val newEmail: String = "",
    val showSuccessDialog: Boolean = false,
    val isEmailValid: Boolean = true,
    val errorMessage: String = ""
)

interface ChangeEmailActions {
    fun loadCurrentUser(userId: Int)
    fun setNewEmail(email: String)
    fun updateEmail(userId: Int, onSuccess: () -> Unit)
    fun hideSuccessDialog()
    fun clearError()
}

class ChangeEmailViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChangeEmailState())
    val state: StateFlow<ChangeEmailState> = _state.asStateFlow()

    val actions = object : ChangeEmailActions {
        override fun loadCurrentUser(userId: Int) {
            viewModelScope.launch {
                try {
                    val user = userRepository.getUserById(userId)
                    _state.update { it.copy(currentEmail = user?.email ?: "") }
                } catch (e: Exception) {
                    _state.update { it.copy(errorMessage = "Errore nel caricamento dei dati") }
                }
            }
        }

        override fun setNewEmail(email: String) {
            _state.update { it.copy(newEmail = email) }
        }

        override fun updateEmail(userId: Int, onSuccess: () -> Unit) {
            val currentState = _state.value

            val (isValid, validationError) = ValidationUtils.validateEmail(currentState.newEmail)
            if (!isValid) {
                _state.update {
                    it.copy(
                        isEmailValid = false,
                        errorMessage = validationError ?: ""
                    )
                }
                return
            }

            if (currentState.newEmail == currentState.currentEmail) {
                _state.update { it.copy(errorMessage = "La nuova email inserita è uguale a quella attuale") }
                return
            }

            viewModelScope.launch {
                try {
                    val result = userRepository.updateUserEmail(userId, currentState.newEmail)
                    if (result.isSuccess) {
                        _state.update {
                            it.copy(
                                showSuccessDialog = true
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                errorMessage = result.exceptionOrNull()?.message ?: "Errore nell'aggiornamento dell'email"
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = "Errore nell'aggiornamento dell'email: " + e.message
                        )
                    }
                }
            }
        }

        override fun hideSuccessDialog() {
            _state.update { it.copy(showSuccessDialog = false) }
        }

        override fun clearError() {
            _state.update {
                it.copy(
                    isEmailValid = true,
                    errorMessage = ""
                )
            }
        }
    }
}