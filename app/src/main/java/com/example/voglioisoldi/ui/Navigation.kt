package com.example.voglioisoldi.ui

import AddTransactionScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.voglioisoldi.ui.screens.AccountManagementScreen
import com.example.voglioisoldi.ui.screens.AddAccountScreen
import com.example.voglioisoldi.ui.screens.AppInfoScreen
import com.example.voglioisoldi.ui.screens.ChangeEmailScreen
import com.example.voglioisoldi.ui.screens.ChangePasswordScreen
import com.example.voglioisoldi.ui.screens.DetailsScreen
import com.example.voglioisoldi.ui.screens.GeneralSettingsScreen
import com.example.voglioisoldi.ui.screens.GraphsScreen
import com.example.voglioisoldi.ui.screens.HomeScreen
import com.example.voglioisoldi.ui.screens.LoginScreen
import com.example.voglioisoldi.ui.screens.ProfileScreen
import com.example.voglioisoldi.ui.screens.RegistrationScreen
import com.example.voglioisoldi.ui.screens.SettingsScreen
import com.example.voglioisoldi.ui.screens.TransactionsScreen
import kotlinx.serialization.Serializable

sealed interface SoldiRoute {
    @Serializable data object Login : SoldiRoute
    @Serializable data object Registration : SoldiRoute
    @Serializable data object Home : SoldiRoute
    @Serializable data class Details(val soldiId: Int) : SoldiRoute
    @Serializable data object Profile : SoldiRoute
    @Serializable data object ChangeEmail : SoldiRoute
    @Serializable data object ChangePassword : SoldiRoute
    @Serializable data object Settings : SoldiRoute
    @Serializable data object GeneralSettings : SoldiRoute
    @Serializable data object AccountManagement : SoldiRoute
    @Serializable data object AppInfo : SoldiRoute
    @Serializable data object Transactions : SoldiRoute
    @Serializable data object Graphs : SoldiRoute
    @Serializable data object AddTransaction : SoldiRoute
    @Serializable data object AddAccount : SoldiRoute
}

@Composable
fun Navigation(navController: NavHostController, startDestination: SoldiRoute) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<SoldiRoute.Login> {
            LoginScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.Registration> {
            RegistrationScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.Home> {
            HomeScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.Details> { backStackEntry ->
            val route = backStackEntry.toRoute<SoldiRoute.Details>()
            DetailsScreen(
                navController = navController,
                soldiId = route.soldiId
            )
        }
        composable<SoldiRoute.Profile> {
            ProfileScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.ChangeEmail> {
            ChangeEmailScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.ChangePassword> {
            ChangePasswordScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.Settings> {
            SettingsScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.GeneralSettings> {
            GeneralSettingsScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.AccountManagement> {
            AccountManagementScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.AppInfo> {
            AppInfoScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.Transactions> {
            TransactionsScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.Graphs> {
            GraphsScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.AddTransaction> {
            AddTransactionScreen(
                navController = navController
            )
        }
        composable<SoldiRoute.AddAccount> {
            AddAccountScreen(
                navController = navController
            )
        }
    }
}