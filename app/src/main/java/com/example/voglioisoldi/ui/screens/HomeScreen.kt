package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    navController: NavController
) {
    val viewModel: AuthViewModel = koinViewModel()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(SoldiRoute.AddTransaction) },
                modifier = Modifier.size(60.dp).offset(y = 45.dp),
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.LightGray)
        )
    }
}