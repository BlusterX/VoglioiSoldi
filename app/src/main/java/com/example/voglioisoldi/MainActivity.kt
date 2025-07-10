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
import com.example.voglioisoldi.ui.Navigation
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.theme.VoglioiSoldiTheme
import org.koin.android.ext.android.get

//Al posto di Text("Screen 2") -> Text(stringResource(R.string.screen2_name))
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsRepository: SettingsRepository = get()
        setContent {
            val themeMode by settingsRepository.getThemeMode().collectAsState(initial = ThemeMode.SYSTEM)
            VoglioiSoldiTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()
                Navigation(
                    navController = navController,
                    startDestination = SoldiRoute.Login
                )
            }
        }
        //UTILE: Per far partire il worker subito, per testare la ricorrenza delle transazioni
//        WorkManager.getInstance(this).enqueue(
//            OneTimeWorkRequestBuilder<RecurringTransactionWorker>().build()
//        )
    }
}



