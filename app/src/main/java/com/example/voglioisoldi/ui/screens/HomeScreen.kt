package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.data.models.ThemeMode
import com.example.voglioisoldi.data.repositories.SettingsRepository
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.composables.account.AccountInfoCard
import com.example.voglioisoldi.ui.composables.chart.LineChartComposable
import com.example.voglioisoldi.ui.composables.transaction.BalanceSummary
import com.example.voglioisoldi.ui.composables.transaction.RecentTransactionsList
import com.example.voglioisoldi.ui.composables.util.BottomBar
import com.example.voglioisoldi.ui.composables.util.TopBar
import com.example.voglioisoldi.ui.viewmodel.ChartPeriod
import com.example.voglioisoldi.ui.viewmodel.GraphsViewModel
import com.example.voglioisoldi.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin


@Composable
fun HomeScreen(
    navController: NavController
) {
    val viewModel: HomeViewModel = koinViewModel()
    val viewModel2: GraphsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiState2 by viewModel2.uiState.collectAsState()

    val accountsWithBalance = uiState.accountsWithBalance

    val settingsRepository: SettingsRepository = getKoin().get()
    val themeMode by settingsRepository.getThemeMode().collectAsState(initial = ThemeMode.SYSTEM)
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    LaunchedEffect(Unit) {
        viewModel2.setPeriod(ChartPeriod.DAILY)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(SoldiRoute.AddTransaction) },
                modifier = Modifier.size(60.dp).offset(y = 45.dp),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(36.dp),
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            BalanceSummary(uiState.transactions)
            Spacer(Modifier.height(8.dp))
            when {
                uiState2.isLoading -> CircularProgressIndicator()
                uiState2.points.isEmpty() -> Text("Nessun dato per il periodo selezionato.")
                else -> LineChartComposable(uiState2.points, isDark)
            }
            Spacer(Modifier.height(24.dp))
            if (accountsWithBalance.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accountsWithBalance) { item  ->
                        AccountInfoCard(
                            accountType = item.account.type,
                            balance = item.actualBalance.toFloat(),
                            modifier = Modifier
                                .width(180.dp)
                                .height(85.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            RecentTransactionsList(uiState.transactions, navController)
        }
    }
}



