package com.example.voglioisoldi.ui.composables.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.voglioisoldi.ui.viewmodel.AddTransactionState


@Composable
fun TransactionConfirmDialog(
    show: Boolean,
    state: AddTransactionState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    accountLabel: String
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Conferma Transazione") },
        text = {
            Column {
                Text("Sei sicuro di voler salvare la seguente transazione?\n")
                Text("Tipo: ${state.type.name.lowercase().replaceFirstChar { it.uppercase() }}")
                Text("Importo: €${state.amount}")
                Text("Categoria: ${state.selectedCategory}")
                Text("Descrizione: ${state.description}")
                Text("Conto: $accountLabel")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Sì") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("No") }
        }
    )
}