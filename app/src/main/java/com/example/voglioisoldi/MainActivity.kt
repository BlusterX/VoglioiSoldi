package com.example.voglioisoldi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                var initialRoute by remember { mutableStateOf<SoldiRoute?>(null) }
                val loggedUserState = sessionManager.getLoggedInUser().collectAsState(initial = "LOADING")
                val loggedUser = loggedUserState.value


                LaunchedEffect(loggedUser) {
                    if (initialRoute == null) {
                        initialRoute = if (loggedUser.isNullOrBlank() || loggedUser == "LOADING") {
                            SoldiRoute.Login
                        } else {
                            SoldiRoute.Home
                        }
                    }
                }

                if (initialRoute != null) {
                    Navigation(
                        navController = navController,
                        startDestination = initialRoute!!
                    )
                } else {
                    SplashScreen()
                }
            }
        }
    }
}


