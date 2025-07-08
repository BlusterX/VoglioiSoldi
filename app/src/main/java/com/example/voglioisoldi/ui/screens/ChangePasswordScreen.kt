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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.util.rememberCurrentUserId
import com.example.voglioisoldi.ui.viewmodel.ChangePasswordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController
) {
    val viewModel: ChangePasswordViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val actions = viewModel.actions
    val userId = rememberCurrentUserId()

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
                text = "Modifica password",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            // Password attuale
            OutlinedTextField(
                value = state.currentPassword,
                onValueChange = actions::setCurrentPassword,
                label = { Text("Password Attuale") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.currentPasswordError
            )

            // Nuova password
            OutlinedTextField(
                value = state.newPassword,
                onValueChange = actions::setNewPassword,
                label = { Text("Nuova Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.newPasswordError
            )

            // Conferma password
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = actions::setConfirmPassword,
                label = { Text("Conferma Nuova Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.confirmPasswordError
            )

            if (state.errorMessage.isEmpty()) {
                Text(
                    text = "La password deve essere lunga almeno 6 caratteri",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (userId != null) {
                        actions.updatePassword(userId) {
                            navController.popBackStack()
                        }
                    }
                },
                enabled = state.currentPassword.isNotBlank() &&
                        state.newPassword.isNotBlank() &&
                        state.confirmPassword.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aggiorna password",
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
            text = { Text("Password aggiornata con successo!") },
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