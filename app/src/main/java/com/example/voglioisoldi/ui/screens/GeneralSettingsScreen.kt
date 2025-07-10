package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.data.models.ThemeMode
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.settings.GeneralSettingsCard
import com.example.voglioisoldi.ui.composables.util.BottomBar
import com.example.voglioisoldi.ui.composables.util.ThemeSelectionDialog
import com.example.voglioisoldi.ui.composables.util.TopBar
import com.example.voglioisoldi.ui.util.BiometricAuthUtil
import com.example.voglioisoldi.ui.util.BiometricStatus
import com.example.voglioisoldi.ui.viewmodel.GeneralSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GeneralSettingsScreen(
    navController: NavController,
) {
    val viewModel: GeneralSettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val actions = viewModel.actions
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showThemeDialog by remember { mutableStateOf(false) }
    val biometricManager = remember { BiometricAuthUtil(context) }

    // Carica solo la prima volta che viene avviato il composable
    LaunchedEffect(Unit) {
        actions.loadSettings()
        // Ulteriore controllo per la disponibilità dell'autenticazione biometrica
        actions.setBiometricStatus(biometricManager.getBiometricStatus())
    }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.errorMessage)
            actions.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                showBackButton = true,
                onBackClick = { navController.navigate(SoldiRoute.Home) }
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Impostazioni Generali",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Impostazione tema
            GeneralSettingsCard(
                icon = when (state.themeMode) {
                    ThemeMode.LIGHT -> Icons.Default.LightMode
                    ThemeMode.DARK -> Icons.Default.DarkMode
                    ThemeMode.SYSTEM -> Icons.Default.Settings
                },
                title = "Tema",
                subtitle = when (state.themeMode) {
                    ThemeMode.LIGHT -> "Modalità chiara"
                    ThemeMode.DARK -> "Modalità scura"
                    ThemeMode.SYSTEM -> "Predefinito dal sistema"
                },
                onClick = { showThemeDialog = true }
            )

            // Attiva/disattiva accesso con biometrics
            GeneralSettingsCard(
                icon = Icons.Default.Fingerprint,
                title = "Accesso con impronta digitale",
                subtitle = when (state.biometricStatus) {
                    BiometricStatus.AVAILABLE -> {
                        if (state.biometricEnabled) "Attivo" else "Non Attivo"
                    }
                    BiometricStatus.NOT_ENROLLED -> "Configura impronta digitale"
                    BiometricStatus.NOT_AVAILABLE -> "Non disponibile su questo dispositivo"
                },
                onClick = when (state.biometricStatus) {
                    BiometricStatus.NOT_ENROLLED -> {
                        { biometricManager.openBiometricSettings() }
                    }
                    else -> null
                },
                trailingContent = {
                    Switch(
                        checked = state.biometricEnabled,
                        onCheckedChange = { enabled ->
                            actions.updateBiometricEnabled(enabled)
                        },
                        enabled = state.biometricStatus == BiometricStatus.AVAILABLE
                    )
                }
            )
        }
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.themeMode,
            onThemeSelected = { theme ->
                actions.updateTheme(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}