package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.data.repositories.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AccountUiState(
    val type: String = "",
    val balance: String = "",
    val isLoading: Boolean = false,
    val hasExistingAccounts: Boolean = false,
    val errorMessage: String = "",
    val success: Boolean = false
)

interface AccountActions {
    fun setType(type: String)
    fun setBalance(balance: String)
    fun loadUserAccounts(userId: Int)
    fun createAccount(userId: Int)
    fun resetSuccess()
    fun clearError()
}

class AccountViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    val actions = object : AccountActions {
        override fun setType(type: String) {
            _uiState.value = _uiState.value.copy(type = type)
        }

        override fun setBalance(balance: String) {
            _uiState.value = _uiState.value.copy(balance = balance)
        }

        override fun loadUserAccounts(userId: Int) {
            viewModelScope.launch {
                accountRepository.getAccountsByUser(userId).collect { accounts ->
                    _uiState.value =
                        _uiState.value.copy(hasExistingAccounts = accounts.isNotEmpty())
                }
            }
        }

        override fun createAccount(userId: Int) {
            val type = _uiState.value.type
            val balanceString = _uiState.value.balance.replace(",", ".").trim()
            val balance = balanceString.toDoubleOrNull()
            if (type.isBlank() || balance == null) {
                _uiState.value = _uiState.value.copy(errorMessage = "Compila tutti i campi!")
                return
            }
            _uiState.value = _uiState.value.copy(isLoading = true)
            viewModelScope.launch {
                try {
                    accountRepository.insertAccount(
                        Account(
                            type = type,
                            balance = balance,
                            userId = userId
                        )
                    )
                    _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(isLoading = false, errorMessage = "Errore creazione account")
                }
            }
        }

        override fun resetSuccess() {
            _uiState.value = _uiState.value.copy(success = false)
        }

        override fun clearError() {
            _uiState.value = _uiState.value.copy(errorMessage = "")
        }
    }
}