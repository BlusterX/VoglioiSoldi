package com.example.voglioisoldi.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute

@Composable
fun BottomBar(navController: NavController) {
    BottomAppBar(
        containerColor = Color.White,
        modifier = Modifier.height(120.dp)
    ) {
        IconButton(
            onClick = { /* Account */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.size(50.dp)
            )
        }
        IconButton(
            onClick = { /* Transaction */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                //TODO: Change Shopping cart icons
                Icons.Filled.ShoppingCart,
                contentDescription = "Transactions",
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(
            onClick = { navController.navigate(SoldiRoute.Settings) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(50.dp)
            )
        }
        IconButton(
            onClick = { /* Graphs */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                //TODO: Change Shopping cart icons
                Icons.Filled.Star,
                contentDescription = "Graphs",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}