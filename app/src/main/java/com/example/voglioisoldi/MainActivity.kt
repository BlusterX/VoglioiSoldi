package com.example.voglioisoldi

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.voglioisoldi.data.models.ThemeMode
import com.example.voglioisoldi.data.repositories.SettingsRepository
import com.example.voglioisoldi.data.session.SessionManager
import com.example.voglioisoldi.ui.Navigation
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.screens.SplashScreen
import com.example.voglioisoldi.ui.theme.VoglioiSoldiTheme
import org.koin.android.ext.android.get

//Al posto di Text("Screen 2") -> Text(stringResource(R.string.screen2_name))
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager: SessionManager = get()
        val settingsRepository: SettingsRepository = get()
        setContent {
            val themeMode by settingsRepository.getThemeMode().collectAsState(initial = ThemeMode.SYSTEM)
            val loggedUserState = sessionManager.getLoggedInUser().collectAsState(initial = "LOADING")
            val loggedUser = loggedUserState.value

            VoglioiSoldiTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()

                if (loggedUser == "LOADING") {
                    SplashScreen()
                } else {
                    val initialRoute = if (loggedUser.isNullOrBlank()) {
                        SoldiRoute.Login
                    } else {
                        SoldiRoute.Home
                    }
                    Navigation(
                        navController = navController,
                        startDestination = initialRoute
                    )
                }
            }
        }
    }
}


