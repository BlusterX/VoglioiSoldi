package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreen(
    navController: NavController
) {
    val viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.registrationSuccess) {
        LaunchedEffect(Unit) {
            viewModel.resetRegistrationFlag()
            navController.navigate(SoldiRoute.Login)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Benvenuto in VOGLIOiSOLDI",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = uiState.name, onValueChange = { viewModel.onNameChanged(it) }, label = { Text("Nome") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = uiState.surname, onValueChange = { viewModel.onSurnameChanged(it) }, label = { Text("Cognome") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = uiState.username, onValueChange = { viewModel.onUsernameChanged(it) }, label = { Text("Username") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = uiState.email, onValueChange = { viewModel.onEmailChanged(it) }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate(SoldiRoute.Login) }) {
            Text("Hai già un account? Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.register() },
            enabled = !uiState.isLoading
        ) {
            Text("Registrati")
        }
        uiState.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}
