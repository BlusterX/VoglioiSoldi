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
    val accountId: Int? = null,
    val showConfirmDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String = ""
) {
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

    fun toTransaction(userId: Int) = accountId?.let {
        Transaction(
        amount = finalAmount,
        category = selectedCategory,
        description = description,
        date = System.currentTimeMillis(),
        userId = userId,
        accountId = it
    )
    }
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
    fun setAccountId(accountId: Int)
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

        override fun setAccountId(accountId: Int) {
            _state.update { it.copy(accountId = accountId) }
        }

        override fun showConfirmDialog() {
            val currentState = _state.value
            val isAmountValid = currentState.amount.isNotBlank() && currentState.amount.toDoubleOrNull() != null
            val isDescriptionValid = currentState.description.isNotBlank()

            if (!isAmountValid) {
                showErrorDialog("Inserisci un importo valido.")
            } else if (!isDescriptionValid) {
                showErrorDialog("La descrizione è obbligatoria.")
            } else {
                _state.update { it.copy(showConfirmDialog = true) }
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
                val currentState = _state.value
                val transaction = currentState.toTransaction(userId)
                if (transaction == null) {
                    _state.update {
                        it.copy(
                            showConfirmDialog = false,
                            showErrorDialog = true,
                            errorMessage = "Seleziona un account."
                        )
                    }
                    return@launch
                }
                try {
                    transactionRepository.insertTransaction(transaction)
                    _state.update { it.copy(showConfirmDialog = false) }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            showConfirmDialog = false,
                            showErrorDialog = true,
                            errorMessage = "Errore nel salvataggio: ${e.message ?: "Unknown error"}"
                        )
                    }
                }
            }
        }

    }
}