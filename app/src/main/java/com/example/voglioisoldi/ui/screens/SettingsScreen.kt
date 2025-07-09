package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel


data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val logoutDone by viewModel.logoutDone.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(logoutDone) {
        if (logoutDone) {
            navController.navigate(SoldiRoute.Login) {
                popUpTo(0)
                launchSingleTop = true
            }
            viewModel.resetLogoutFlag()
        }
    }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetError()
        }
    }
    SettingsContent(
        navController = navController,
        onLogout = { viewModel.logout() },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun SettingsContent(
    navController: NavController,
    onLogout: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val settingsItems = listOf(
        SettingItem(Icons.Default.Settings, "Modifiche generali", "Modifiche nel tuo account") {},
        SettingItem(Icons.Default.AccountBox, "Account", "Aggiungi o modifica il tuo account") {
            navController.navigate(SoldiRoute.AccountManagement)
        },
        SettingItem(Icons.Default.Notifications, "Notifiche", "Gestisci le tue notifiche") {},
        SettingItem(Icons.Default.Info, "App Info", "Visualizza le informazioni generali") {
            navController.navigate(SoldiRoute.AppInfo)
        },
        SettingItem(Icons.Default.Clear, "Logout", "Esci dal tuo account", onLogout)
    )

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
            settingsItems.forEach { item ->
                SettingRow(item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SettingRow(item: SettingItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}