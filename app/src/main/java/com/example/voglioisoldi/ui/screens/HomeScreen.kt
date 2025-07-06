package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.data.database.entities.Transaction
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun HomeScreen(
    navController: NavController
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(SoldiRoute.AddTransaction) },
                modifier = Modifier.size(60.dp).offset(y = 45.dp),
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            RecentTransactionsList(uiState.transactions)
        }
    }
}

//Da spostare in un util home??
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

//Da spostare in un util home??
@Composable
fun TransactionCard(transaction: Transaction) {
    val isNegative = transaction.amount < 0
    val amountColor = if (isNegative) Color(0xFFE57373) else Color(0xFF81C784)
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "€%.2f".format(transaction.amount),
                color = amountColor,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.5f)
            )
            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Categoria: ${transaction.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatTransactionDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

//Da spostare in un util home??
fun formatTransactionDate(timestamp: Long?): String {
    if (timestamp == null) return ""
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
