package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.data.models.ThemeMode
import com.example.voglioisoldi.ui.composables.util.BottomBar
import com.example.voglioisoldi.ui.composables.settings.GeneralSettingsCard
import com.example.voglioisoldi.ui.composables.util.ThemeSelectionDialog
import com.example.voglioisoldi.ui.composables.util.TopBar
import com.example.voglioisoldi.ui.viewmodel.GeneralSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GeneralSettingsScreen(
    navController: NavController,
) {
    val viewModel: GeneralSettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val actions = viewModel.actions
    val snackbarHostState = remember { SnackbarHostState() }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Carica solo la prima volta che viene avviato il composable
    LaunchedEffect(Unit) {
        actions.loadSettings()
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
                onBackClick = { navController.popBackStack() }
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
}