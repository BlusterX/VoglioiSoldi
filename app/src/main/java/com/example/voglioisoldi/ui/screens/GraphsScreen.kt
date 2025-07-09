package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.util.BottomBar
import com.example.voglioisoldi.ui.composables.chart.LineChartComposable
import com.example.voglioisoldi.ui.composables.chart.PeriodSelector
import com.example.voglioisoldi.ui.composables.util.TopBar
import com.example.voglioisoldi.ui.viewmodel.GraphsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun GraphsScreen(
    navController: NavController
) {
    val viewModel: GraphsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() })
                 },
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text("Andamento Conto", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            PeriodSelector(
                selected = uiState.selectedPeriod,
                onSelect = viewModel::setPeriod
            )
            Spacer(Modifier.height(18.dp))

            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.points.isEmpty() -> Text("Nessun dato per il periodo selezionato.")
                else -> LineChartComposable(uiState.points)
            }
        }
    }
}


