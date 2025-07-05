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
fun LoginScreen(
    navController: NavController
) {
    val viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Navigazione automatica se loginSuccess
    if (uiState.loginSuccess) {
        LaunchedEffect(Unit) {
            viewModel.resetLoginFlag()
            navController.navigate(SoldiRoute.Home)
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
        TextField(
            value = uiState.username,
            onValueChange = { viewModel.onUsernameChanged(it) },
            label = { Text("Username") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate(SoldiRoute.Registration) }) {
            Text("Non hai un account? Registrati")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.login() },
            enabled = !uiState.isLoading
        ) {
            Text("Login")
        }
        uiState.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}
