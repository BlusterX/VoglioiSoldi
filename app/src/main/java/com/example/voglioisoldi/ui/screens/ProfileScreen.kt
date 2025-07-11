package com.example.voglioisoldi.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.user.PersonalInfoCard
import com.example.voglioisoldi.ui.composables.user.ProfileHeader
import com.example.voglioisoldi.ui.composables.user.UserActionsCard
import com.example.voglioisoldi.ui.composables.util.BottomBar
import com.example.voglioisoldi.ui.composables.util.TopBar
import com.example.voglioisoldi.ui.util.rememberCurrentUserId
import com.example.voglioisoldi.ui.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val actions = viewModel.actions
    val userId = rememberCurrentUserId()
    val context = LocalContext.current

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileHeader(
                name = state.user?.name ?: "",
                surname = state.user?.surname ?: "",
                profilePictureUri = state.user?.profilePictureUri?.let { Uri.parse(it) },
                onProfilePictureSelected = { uri ->
                    if (userId != null) {
                        actions.updateProfilePicture(userId, uri, context.contentResolver)
                    }
                }
            )

            PersonalInfoCard(state.user)

            UserActionsCard(
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
