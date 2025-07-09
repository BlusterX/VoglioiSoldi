package com.example.voglioisoldi.ui.composables.account

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.voglioisoldi.data.database.entities.Account

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDropdown(
    accountBalances: List<Pair<Account, Double>>,
    selectedAccountId: Int?,
    onAccountSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val accountNames = accountBalances.map {
        "${it.first.type} (${String.format("%.2f", it.second)} €)"
    }
    val accountIds = accountBalances.map { it.first.id }
    val selectedIndex = accountIds.indexOf(selectedAccountId).takeIf { it >= 0 } ?: 0

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = accountNames.getOrElse(selectedIndex) { "Seleziona account" },
            onValueChange = {},
            readOnly = true,
            label = { Text("Conto:") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            accountNames.forEachIndexed { idx, name ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onAccountSelected(accountIds[idx])
                        expanded = false
                    }
                )
            }
        }
    }
}