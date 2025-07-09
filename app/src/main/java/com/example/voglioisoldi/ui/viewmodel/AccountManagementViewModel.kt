package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.data.repositories.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountManagementState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

interface AccountListActions {
    fun loadAccounts(userId: Int)
    fun clearError()
}

class AccountManagementViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AccountManagementState())
    val state: StateFlow<AccountManagementState> = _state.asStateFlow()

    val actions = object : AccountListActions {
        override fun loadAccounts(userId: Int) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                try {
                    accountRepository.getAccountsByUser(userId).collect { accounts ->
                        _state.update {
                            it.copy(
                                accounts = accounts,
                                isLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = "Errore nel caricamento dei metodi di pagamento: ${e.message}.",
                            isLoading = false
                        )
                    }
                }
            }
        }

        override fun clearError() {
            _state.update { it.copy(errorMessage = "") }
        }
    }
}