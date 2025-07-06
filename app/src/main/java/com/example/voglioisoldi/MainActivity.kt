package com.example.voglioisoldi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.voglioisoldi.data.session.SessionManager
import com.example.voglioisoldi.ui.Navigation
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.screens.SplashScreen
import com.example.voglioisoldi.ui.theme.VoglioiSoldiTheme
import org.koin.android.ext.android.get

//Al posto di Text("Screen 2") -> Text(stringResource(R.string.screen2_name))
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager: SessionManager = get()
        setContent {
            VoglioiSoldiTheme {
                val navController = rememberNavController()
                val loggedUserState =
                    sessionManager.getLoggedInUser().collectAsState(initial = "LOADING")
                val loggedUser = loggedUserState.value
                when {
                    loggedUser == "LOADING" -> {
                        SplashScreen()
                    }
                    loggedUser.isNullOrBlank() -> {
                        Navigation(
                            navController = navController,
                            startDestination = SoldiRoute.Login
                        )
                    }
                    else -> {
                        Navigation(
                            navController = navController,
                            startDestination = SoldiRoute.Home
                        )
                    }
                }
            }
        }
    }
}


