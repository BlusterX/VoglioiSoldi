package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountScreen(
    navController: NavController
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.LightGray)
        )
    }
}