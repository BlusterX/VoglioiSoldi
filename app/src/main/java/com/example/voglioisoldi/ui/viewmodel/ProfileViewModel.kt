package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.User
import com.example.voglioisoldi.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val showDeleteDialog: Boolean = false,
    val errorMessage: String = ""
)

interface ProfileActions {
    fun loadUser(userId: Int)
    fun showDeleteDialog()
    fun hideDeleteDialog()
    fun deleteAccount(userId: Int, onSuccess: () -> Unit)
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    val actions = object : ProfileActions {
        override fun loadUser(userId: Int) {
            viewModelScope.launch {
                try {
                    val user = userRepository.getUserById(userId)
                    _state.update {
                        it.copy(user = user)
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = "Errore nel caricamento del profilo."
                        )
                    }
                }
            }
        }

        override fun showDeleteDialog() {
            _state.update { it.copy(showDeleteDialog = true) }
        }

        override fun hideDeleteDialog() {
            _state.update { it.copy(showDeleteDialog = false) }
        }

        override fun deleteAccount(userId: Int, onSuccess: () -> Unit) {
            viewModelScope.launch {
                try {
                    val result = userRepository.deleteUser(userId)
                    if (result.isSuccess) {
                        _state.update { it.copy(showDeleteDialog = false) }
                        onSuccess()
                    } else {
                        _state.update {
                            it.copy(
                                showDeleteDialog = false,
                                errorMessage = result.exceptionOrNull()?.message ?: "Errore nell'eliminazione dell'account."
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            showDeleteDialog = false,
                            errorMessage = "Errore nell'eliminazione dell'account."
                        )
                    }
                }
            }
        }
    }
}