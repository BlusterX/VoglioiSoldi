package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.repositories.AccountRepository
import com.example.voglioisoldi.data.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountManagementState(
    val accountsWithBalance: List<AccountWithBalance> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

interface AccountListActions {
    fun loadAccounts(userId: Int)
    fun clearError()
}

class AccountManagementViewModel(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AccountManagementState())
    val state: StateFlow<AccountManagementState> = _state.asStateFlow()

    val actions = object : AccountListActions {
        override fun loadAccounts(userId: Int) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                try {
                    val accounts = accountRepository.getAccountsByUser(userId).first()
                    val transactions = transactionRepository.getTransactionsByUser(userId).first()

                    val withBalance = accounts.map { account ->
                        val total = account.balance +
                                transactions.filter { it.accountId == account.id }
                                    .sumOf { it.amount }
                        AccountWithBalance(account, total)
                    }

                    _state.update {
                        it.copy(
                            accountsWithBalance = withBalance,
                            isLoading = false
                        )
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