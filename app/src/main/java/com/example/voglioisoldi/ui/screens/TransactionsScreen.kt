package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.composables.TransactionCard
import com.example.voglioisoldi.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionsScreen(
    navController: NavController
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val uiState by homeViewModel.uiState.collectAsState()
    val transactions = uiState.transactions
    val accounts = uiState.accounts

    var selectedAccountId by remember { mutableStateOf<Int?>(null) }
    val filteredTransactions = if (selectedAccountId == null) {
        transactions
    } else {
        transactions.filter { it.accountId == selectedAccountId }
    }

    Scaffold(
        topBar = {
            TopBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (accounts.isNotEmpty()) {
                AccountFilterBar(
                    accounts = accounts,
                    selectedAccountId = selectedAccountId,
                    onSelectAccount = { id -> selectedAccountId = id }
                )
            }
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nessuna transazione trovata.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTransactions) { transaction ->
                        TransactionCard(transaction) {
                            navController.navigate(
                                com.example.voglioisoldi.ui.SoldiRoute.Details(transaction.id)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountFilterBar(
    accounts: List<Account>,
    selectedAccountId: Int?,
    onSelectAccount: (Int?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OutlinedButton(
                onClick = { onSelectAccount(null) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedAccountId == null) Color(0xFF1565C0) else Color.Transparent,
                    contentColor = if (selectedAccountId == null) Color.White else Color(0xFF1565C0)
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text("Tutti")
            }
        }
        items(accounts) { account ->
            OutlinedButton(
                onClick = { onSelectAccount(account.id) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedAccountId == account.id) Color(0xFF1565C0) else Color.Transparent,
                    contentColor = if (selectedAccountId == account.id) Color.White else Color(0xFF1565C0)
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text(account.type)
            }
        }
        // Optional: extra space at end
        item { Spacer(modifier = Modifier.width(4.dp)) }
    }
}