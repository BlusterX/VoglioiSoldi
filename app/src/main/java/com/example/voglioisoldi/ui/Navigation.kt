package com.example.voglioisoldi.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.voglioisoldi.ui.screens.AccountScreen
import com.example.voglioisoldi.ui.screens.DetailsScreen
import com.example.voglioisoldi.ui.screens.GraphsScreen
import com.example.voglioisoldi.ui.screens.HomeScreen
import com.example.voglioisoldi.ui.screens.SettingsScreen
import com.example.voglioisoldi.ui.screens.TransactionsScreen
import kotlinx.serialization.Serializable

sealed interface SoldiRoute {
    @Serializable data object Home : SoldiRoute
    @Serializable data class Details(val soldiId: String) : SoldiRoute
    @Serializable data object Account : SoldiRoute
    @Serializable data object Settings : SoldiRoute
    @Serializable data object Transactions : SoldiRoute
    @Serializable data object Graphs : SoldiRoute
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SoldiRoute.Home
    ) {
        composable<SoldiRoute.Home> {
            HomeScreen(navController)
        }
        composable<SoldiRoute.Details> { backStackEntry ->
            val route = backStackEntry.toRoute<SoldiRoute.Details>()
            DetailsScreen(navController, route.soldiId)
        }
        composable<SoldiRoute.Account> {
            AccountScreen(navController)
        }
        composable<SoldiRoute.Settings> {
            SettingsScreen(navController)
        }
        composable<SoldiRoute.Transactions> {
            TransactionsScreen(navController)
        }
        composable<SoldiRoute.Graphs> {
            GraphsScreen(navController)
        }
    }
}