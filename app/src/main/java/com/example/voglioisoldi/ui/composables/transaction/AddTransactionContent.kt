package com.example.voglioisoldi.ui.composables.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.ui.composables.account.AccountDropdown
import com.example.voglioisoldi.ui.composables.util.DropdownMenuBox
import com.example.voglioisoldi.ui.viewmodel.AddTransactionActions
import com.example.voglioisoldi.ui.viewmodel.AddTransactionState

@Composable
fun AddTransactionContent(
    padding: PaddingValues,
    state: AddTransactionState,
    actions: AddTransactionActions,
    accountBalances: List<Pair<Account, Double>>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(22.dp),
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Aggiungi Transazione",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        AccountDropdown(
            accountBalances = accountBalances,
            selectedAccountId = state.accountId,
            onAccountSelected = actions::setAccountId
        )
        TransactionAmountField(
            amount = state.amount,
            onAmountChange = actions::setAmount
        )
        TransactionDescriptionField(
            description = state.description,
            onDescriptionChange = actions::setDescription
        )
        TransactionTypeSelector(
            selectedType = state.type,
            onTypeSelected = actions::setType
        )
        DropdownMenuBox(
            selected = state.selectedCategory,
            items = state.availableCategories,
            onItemSelected = actions::setCategory
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.isRecurring,
                onCheckedChange = actions::setRecurring
            )
            Text("Transazione ricorrente")
        }
        if (state.isRecurring) {
            OutlinedTextField(
                value = state.recurringPeriodMinutes,
                onValueChange = actions::setRecurringPeriod,
                label = { Text("Ripeti ogni X minuti") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(
            onClick = actions::showConfirmDialog,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                "Salva",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
        }
    }
}