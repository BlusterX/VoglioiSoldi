package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun DetailsScreen(
    navController: NavController,
    soldiId: String
) {
    val viewModel: AuthViewModel = koinViewModel()
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
    ) { paddingValues ->
        Text(
            text = "Dettagli transazione",
            modifier = Modifier.padding(paddingValues)
        )
    }
}
