package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.util.TopBar
import com.example.voglioisoldi.ui.util.rememberCurrentUserId
import com.example.voglioisoldi.ui.viewmodel.AccountViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun AddAccountScreen(
    navController: NavController
) {
    val viewModel: AccountViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val actions = viewModel.actions
    val userId = rememberCurrentUserId()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        userId?.let { actions.loadUserAccounts(it) }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            navController.navigate(SoldiRoute.Home) {
                popUpTo(0)
            }
            actions.resetSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            actions.clearError()
        }
    }

    Scaffold(
        topBar = {
            if (uiState.hasExistingAccounts) {
                TopBar(
                    showBackButton = true,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.95f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Crea un conto",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = uiState.type,
                        onValueChange = actions::setType,
                        label = { Text("Nome del conto (es: Postepay)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.balance,
                        onValueChange = actions::setBalance,
                        label = { Text("Ammontare (€)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { userId?.let { actions.createAccount(it) } },
                        enabled = !uiState.isLoading && userId != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Crea conto!")
                    }
                }
            }
        }
    }
}