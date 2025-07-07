package com.example.voglioisoldi.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voglioisoldi.data.database.entities.Transaction

@Composable
fun RecentTransactionsList(transactions: List<Transaction>) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            "Ultime transazioni",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        if (transactions.isEmpty()) {
            Text("Nessuna transazione trovata.", color = Color.Gray)
        } else {
            //Fa  vedere le ultime 5 transazioni, DA CAMBIARE???
            transactions.take(5).forEach { transaction ->
                TransactionCard(transaction)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}