package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.repositories.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AccountUiState(
    val type: String = "",
    val balance: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AccountViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    fun setType(type: String) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun setBalance(balance: String) {
        _uiState.value = _uiState.value.copy(balance = balance)
    }

    fun createAccount(userId: Int) {
        val type = _uiState.value.type
        val balanceString = _uiState.value.balance.replace(",", ".").trim()
        val balance = balanceString.toDoubleOrNull()
        if (type.isBlank() || balance == null) {
            _uiState.value = _uiState.value.copy(error = "Compila tutti i campi!")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                accountRepository.insertAccount(
                    com.example.voglioisoldi.data.database.entities.Account(
                        type = type,
                        balance = balance,
                        userId = userId
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore creazione account")
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }

    fun resetError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}