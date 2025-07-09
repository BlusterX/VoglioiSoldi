package com.example.voglioisoldi.ui.composables.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.ui.composables.DropdownMenuBox
import com.example.voglioisoldi.ui.composables.account.AccountDropdown
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
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
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
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = actions::showConfirmDialog,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                "Salva",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
        }
    }
}