package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.data.database.entities.User
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.util.rememberCurrentUserId
import com.example.voglioisoldi.ui.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileHeader(name: String, surname: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${name.firstOrNull() ?: ""}${surname.firstOrNull() ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.padding(horizontal = 10.dp))

            Column {
                Text(
                    text = "$name $surname",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Profilo Utente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PersonalInfoCard(user: User?) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Informazioni Personali",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ProfileInfoItem(
                icon = Icons.Filled.Person,
                label = "Nome",
                value = user?.name ?: ""
            )

            ProfileInfoItem(
                icon = Icons.Filled.Person,
                label = "Cognome",
                value = user?.surname ?: ""
            )

            ProfileInfoItem(
                icon = Icons.Filled.Person,
                label = "Username",
                value = user?.username ?: ""
            )

            ProfileInfoItem(
                icon = Icons.Filled.Email,
                label = "Email",
                value = user?.email ?: ""
            )
        }
    }
}

@Composable
fun AccountActionsCard(
    onChangeEmail: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Impostazioni Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedButton(
                onClick = onChangeEmail,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Cambia Email",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Cambia Email")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onChangePassword,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Cambia Password",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Cambia Password")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Elimina Account",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Elimina Account")
            }
        }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val actions = viewModel.actions
    val userId = rememberCurrentUserId()

    LaunchedEffect(userId) {
        if (userId != null) {
            actions.loadUser(userId)
        }
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
                .background(Color.LightGray)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileHeader(
                name = state.user?.name ?: "",
                surname = state.user?.surname ?: ""
            )

            PersonalInfoCard(state.user)

            AccountActionsCard(
                onChangeEmail = { navController.navigate(SoldiRoute.ChangeEmail) },
                onChangePassword = { navController.navigate(SoldiRoute.ChangePassword) },
                onDeleteAccount = { actions.showDeleteDialog() }
            )

            if (state.showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { actions.hideDeleteDialog() },
                    title = { Text("Elimina Account") },
                    text = {
                        Text(
                            "Sei sicuro di voler eliminare il tuo account? Questa azione è irreversibile e tutti i tuoi dati verranno persi definitivamente.",
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (userId != null) {
                                    actions.deleteAccount(userId) {
                                        // Dopo l'eliminazione dell'account, torna alla schermata di login
                                        navController.navigate(SoldiRoute.Login)
                                    }
                                }
                            }
                        ) {
                            Text("Elimina", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { actions.hideDeleteDialog() }
                        ) {
                            Text("Annulla")
                        }
                    }
                )
            }

            if (state.errorMessage.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Errore") },
                    text = { Text(state.errorMessage) },
                    confirmButton = {
                        TextButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
