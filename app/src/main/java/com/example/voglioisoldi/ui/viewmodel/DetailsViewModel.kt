package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.Transaction
import com.example.voglioisoldi.data.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction

    fun loadTransactionById(id: Int) {
        viewModelScope.launch {
            _transaction.value = transactionRepository.getTransactionById(id)
        }
    }

    fun deleteTransaction(transaction: Transaction, onDeleted: () -> Unit) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            onDeleted()
        }
    }

    fun stopRecurring(transaction: Transaction, onComplete: () -> Unit) {
        viewModelScope.launch {
            val updated = transaction.copy(isRecurringActive = false)
            transactionRepository.updateTransaction(updated)
            onComplete()
        }
    }
}