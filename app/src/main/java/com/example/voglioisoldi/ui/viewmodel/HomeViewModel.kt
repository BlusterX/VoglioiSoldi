package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.data.database.entities.Transaction
import com.example.voglioisoldi.data.repositories.AccountRepository
import com.example.voglioisoldi.data.repositories.TransactionRepository
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val username: String? = null,
    val userId: Int? = null,
    val transactions: List<Transaction> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val accountsWithBalance: List<AccountWithBalance> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

data class AccountWithBalance(
    val account: Account,
    val actualBalance: Double
)

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        observeSessionAndLoadUser()
    }

    private fun observeSessionAndLoadUser() {
        viewModelScope.launch {
            sessionManager.getLoggedInUser().collect { username ->
                if (!username.isNullOrBlank()) {
                    _uiState.update { it.copy(username = username, loading = true) }
                    val user = userRepository.getUserByUsername(username)
                    if (user != null) {
                        val accounts = accountRepository.getAccountsByUser(user.id).firstOrNull() ?: emptyList()
                        val transactions = _uiState.value.transactions
                        _uiState.update { it.copy(userId = user.id, accounts = accounts, loading = false, error = null) }
                        updateAccountsWithBalance(accounts, transactions)
                        observeTransactions(user.id)
                    } else {
                        _uiState.update { it.copy(loading = false, error = "Utente non trovato") }
                    }
                } else {
                    _uiState.update { it.copy(loading = false, error = "Nessun utente loggato") }
                }
            }
        }
    }

    private fun observeTransactions(userId: Int) {
        viewModelScope.launch {
            transactionRepository.getTransactionsByUser(userId).collect { transactions ->
                val accounts = _uiState.value.accounts
                _uiState.update { it.copy(transactions = transactions) }
                updateAccountsWithBalance(accounts, transactions)
            }
        }
    }

    private fun updateAccountsWithBalance(accounts: List<Account>, transactions: List<Transaction>) {
        val list = accounts.map { account ->
            val total = account.balance +
                    transactions.filter { it.accountId == account.id }
                        .sumOf { it.amount }
            AccountWithBalance(account, total)
        }
        _uiState.update { it.copy(accountsWithBalance = list) }
    }
}
