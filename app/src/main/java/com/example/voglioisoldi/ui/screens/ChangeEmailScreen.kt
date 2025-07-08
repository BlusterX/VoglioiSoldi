package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.util.rememberCurrentUserId
import com.example.voglioisoldi.ui.viewmodel.ChangeEmailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangeEmailScreen(
    navController: NavController
) {
    val viewModel: ChangeEmailViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val actions = viewModel.actions
    val userId = rememberCurrentUserId()

    LaunchedEffect(userId) {
        if (userId != null) {
            actions.loadCurrentUser(userId)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Modifica email",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            if (state.currentEmail.isNotEmpty()) {
                Text(
                    text = "Email attuale: ${state.currentEmail}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = state.newEmail,
                onValueChange = actions::setNewEmail,
                label = { Text("Nuova Email") },
                placeholder = { Text("esempio@mail.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.errorMessage.isNotEmpty()
            )

            Button(
                onClick = {
                    if (userId != null) {
                        actions.updateEmail(userId) {
                            navController.popBackStack()
                        }
                    }
                },
                enabled = state.newEmail.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aggiorna Email",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    // Success dialog
    if (state.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                actions.hideSuccessDialog()
                navController.popBackStack()
            },
            title = { Text("Successo") },
            text = { Text("Email aggiornata con successo!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        actions.hideSuccessDialog()
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Error dialog
    if (state.errorMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { actions.clearError() },
            title = { Text("Errore") },
            text = { Text(state.errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = { actions.clearError() }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
