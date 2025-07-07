package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.Transaction
import com.example.voglioisoldi.data.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddTransactionState(
    val amount: String = "",
    val description: String = "",
    val type: TransactionType = TransactionType.USCITA,
    val selectedCategory: String = "Spesa",
    val showConfirmDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String = ""
) {
    //TODO: complete submit control
    val canSubmit get() = amount.isNotBlank() && amount.toDoubleOrNull() != null

    val availableCategories: List<String>
        get() = when (type) {
            TransactionType.ENTRATA -> listOf("Stipendio", "Bonus", "Vendita", "Altro")
            TransactionType.USCITA -> listOf("Spesa", "Trasporti", "Bollette", "Svago", "Altro")
        }

    private val finalAmount: Double
        get() {
            val baseAmount = amount.toDoubleOrNull() ?: 0.0
            return when (type) {
                TransactionType.ENTRATA -> baseAmount
                TransactionType.USCITA -> -baseAmount
            }
        }

    fun toTransaction(userId: Int) = Transaction(
        amount = finalAmount,
        category = selectedCategory,
        description = description,
        date = System.currentTimeMillis(),
        userId = userId
    )
}

enum class TransactionType {
    ENTRATA, USCITA
}

interface AddTransactionActions {
    fun setAmount(amount: String)
    fun setDescription(description: String)
    fun setType(type: TransactionType)
    fun setCategory(category: String)
    fun showConfirmDialog()
    fun hideConfirmDialog()
    fun showErrorDialog(message: String)
    fun hideErrorDialog()
    fun saveTransaction(userId: Int)
}

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionState())
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    val actions = object : AddTransactionActions {
        override fun setAmount(amount: String) {
            _state.update { it.copy(amount = amount) }
        }

        override fun setDescription(description: String) {
            _state.update { it.copy(description = description) }
        }

        override fun setType(type: TransactionType) {
            val defaultCategory = when (type) {
                TransactionType.ENTRATA -> "Stipendio"
                TransactionType.USCITA -> "Spesa"
            }
            _state.update {
                it.copy(
                    type = type,
                    selectedCategory = defaultCategory,
                )
            }
        }

        override fun setCategory(category: String) {
            _state.update { it.copy(selectedCategory = category) }
        }

        override fun showConfirmDialog() {
            val currentState = _state.value
            if (currentState.canSubmit) {
                _state.update { it.copy(showConfirmDialog = true) }
            } else {
                val errorMessage = "Inserisci un importo valido."
                showErrorDialog(errorMessage)
            }
        }

        override fun hideConfirmDialog() {
            _state.update { it.copy(showConfirmDialog = false) }
        }

        override fun showErrorDialog(message: String) {
            _state.update {
                it.copy(
                    showErrorDialog = true,
                    errorMessage = message
                )
            }
        }

        override fun hideErrorDialog() {
            _state.update {
                it.copy(
                    showErrorDialog = false,
                    errorMessage = ""
                )
            }
        }

        override fun saveTransaction(userId: Int) {
            viewModelScope.launch {
                try {
                    val currentState = _state.value
                    val transaction = currentState.toTransaction(userId)
                    transactionRepository.insertTransaction(transaction)
                    _state.update { it.copy(showConfirmDialog = false) }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            showConfirmDialog = false,
                            showErrorDialog = true,
                            errorMessage = "Errore durante il salvataggio della transazione: ${e.message ?: "Errore sconosciuto"}"
                        )
                    }
                }
            }
        }
    }
}