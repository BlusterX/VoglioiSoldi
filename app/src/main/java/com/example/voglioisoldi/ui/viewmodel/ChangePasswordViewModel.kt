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

data class ChangePasswordState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val showCurrentPassword: Boolean = false,
    val showNewPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val currentPasswordError: Boolean = false,
    val newPasswordError: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val errorMessage: String = ""
)

interface ChangePasswordActions {
    fun setCurrentPassword(password: String)
    fun setNewPassword(password: String)
    fun setConfirmPassword(password: String)
    fun updatePassword(userId: Int, onSuccess: () -> Unit)
    fun hideSuccessDialog()
    fun clearError()
}

class ChangePasswordViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    val actions = object : ChangePasswordActions {
        override fun setCurrentPassword(password: String) {
            _state.update { it.copy(currentPassword = password) }
        }

        override fun setNewPassword(password: String) {
            _state.update { it.copy(newPassword = password) }
        }

        override fun setConfirmPassword(password: String) {
            _state.update { it.copy(confirmPassword = password) }
        }

        override fun updatePassword(userId: Int, onSuccess: () -> Unit) {
            val currentState = _state.value

            if (currentState.currentPassword.isBlank()) {
                _state.update { it.copy(
                    errorMessage = "Inserisci la password attuale.",
                    currentPasswordError = true,
                    newPasswordError = false,
                    confirmPasswordError = false
                ) }
                return
            }

            val (isNewPasswordValid, newPasswordError) = ValidationUtils.validatePassword(currentState.newPassword)
            if (!isNewPasswordValid) {
                _state.update { it.copy(
                    errorMessage = newPasswordError ?: "Password non valida",
                    currentPasswordError = false,
                    newPasswordError = true,
                    confirmPasswordError = false
                ) }
                return
            }

            val (isConfirmPasswordValid, confirmPasswordError) = ValidationUtils.validateConfirmPassword(
                currentState.confirmPassword,
                currentState.newPassword
            )
            if (!isConfirmPasswordValid) {
                _state.update { it.copy(
                    errorMessage = confirmPasswordError ?: "Le password non corrispondono",
                    currentPasswordError = false,
                    newPasswordError = false,
                    confirmPasswordError = true
                ) }
                return
            }

            if (currentState.newPassword == currentState.currentPassword) {
                _state.update {
                    it.copy(
                        errorMessage = "La nuova password inserita è uguale a quella attuale.",
                        currentPasswordError = false,
                        newPasswordError = true,
                        confirmPasswordError = false
                    )
                }
                return
            }

            viewModelScope.launch {
                try {
                    val result = userRepository.updateUserPassword(
                        userId,
                        currentState.currentPassword,
                        currentState.newPassword
                    )
                    if (result.isSuccess) {
                        _state.update {
                            it.copy(
                                showSuccessDialog = true
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                errorMessage = result.exceptionOrNull()?.message ?: "Errore nell'aggiornamento della password."
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = "Errore nell'aggiornamento della password."
                        )
                    }
                }
            }
        }

        override fun hideSuccessDialog() {
            _state.update { it.copy(showSuccessDialog = false) }
        }

        override fun clearError() {
            _state.update { it.copy(
                errorMessage = "",
                currentPasswordError = false,
                newPasswordError = false,
                confirmPasswordError = false
            ) }
        }
    }
}