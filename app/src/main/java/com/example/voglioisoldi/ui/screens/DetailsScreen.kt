package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.transaction.formatTransactionDate
import com.example.voglioisoldi.ui.composables.util.TopBar
import com.example.voglioisoldi.ui.viewmodel.DetailsViewModel
import com.example.voglioisoldi.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun DetailsScreen(
    navController: NavController,
    soldiId: Int
) {
    val viewModel: DetailsViewModel = koinViewModel()
    val transaction by viewModel.transaction.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val homeViewModel: HomeViewModel = koinViewModel()
    val accounts = homeViewModel.uiState.collectAsState().value.accounts
    val accountType = accounts.find { it.id == transaction?.accountId }?.type ?: "Account sconosciuto"

    var showStopRecurringDialog by remember { mutableStateOf(false) }
    LaunchedEffect(soldiId) {
        viewModel.loadTransactionById(soldiId)
    }

    Scaffold(
        topBar = {
            TopBar(
                showBackButton = true,
                onBackClick = { navController.navigate(SoldiRoute.Home) }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F6FA))
        ) {
            if (transaction != null) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 32.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val isNegative = transaction!!.amount < 0
                            val amountColor = if (isNegative) Color(0xFFD32F2F) else Color(0xFF388E3C)
                            Text(
                                text = "€%.2f".format(transaction!!.amount),
                                style = MaterialTheme.typography.displaySmall.copy(
                                    color = amountColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            InfoRow(label = "Categoria:", value = transaction!!.category)
                            InfoRow(label = "Descrizione:", value = transaction!!.description.orEmpty())
                            InfoRow(label = "Data:", value = formatTransactionDate(transaction!!.date))
                            InfoRow(label = "Conto:", value = accountType)


                            if (transaction!!.isRecurring && transaction!!.isRecurringActive) {
                                Button(
                                    onClick = { showStopRecurringDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text("Interrompi ricorrenza")
                                }
                            }

                            if (showStopRecurringDialog) {
                                AlertDialog(
                                    onDismissRequest = { showStopRecurringDialog = false },
                                    title = { Text("Interrompi ricorrenza") },
                                    text = { Text("Vuoi davvero fermare la ripetizione automatica di questa transazione?") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            transaction?.let {
                                                viewModel.stopRecurring(it) {
                                                    showStopRecurringDialog = false
                                                    navController.popBackStack()
                                                }
                                            }
                                        }) { Text("Sì, interrompi") }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showStopRecurringDialog = false }) { Text("Annulla") }
                                    }
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Text("Elimina transazione")
                    }
                }
            } else {
                // Loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Conferma eliminazione") },
                    text = { Text("Sei sicuro di voler eliminare questa transazione?") },
                    confirmButton = {
                        TextButton(onClick = {
                            transaction?.let {
                                viewModel.deleteTransaction(it) {
                                    navController.popBackStack()
                                }
                            }
                        }) {
                            Text("Elimina")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Annulla")
                        }
                    }
                )
            }
        }

    }
}


@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}