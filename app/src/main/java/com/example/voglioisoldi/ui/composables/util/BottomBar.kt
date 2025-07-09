package com.example.voglioisoldi.ui.composables.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.SoldiRoute

@Composable
fun BottomBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier.height(120.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        IconButton(
            onClick = { navController.navigate(SoldiRoute.Profile) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.size(50.dp)
            )
        }
        IconButton(
            onClick = { navController.navigate(SoldiRoute.Transactions) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ReceiptLong,
                contentDescription = "Account",
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
            onClick = { navController.navigate(SoldiRoute.Graphs) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.BarChart,
                contentDescription = "Account",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}