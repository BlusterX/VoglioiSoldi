package com.example.voglioisoldi

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.voglioisoldi.data.models.ThemeMode
import com.example.voglioisoldi.data.repositories.SettingsRepository
import com.example.voglioisoldi.ui.Navigation
import com.example.voglioisoldi.ui.SoldiRoute
import com.example.voglioisoldi.ui.theme.VoglioiSoldiTheme
import com.example.voglioisoldi.workers.RecurringTransactionWorker
import org.koin.android.ext.android.get

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsRepository: SettingsRepository = get()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
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
        WorkManager.getInstance(this).enqueue(
            OneTimeWorkRequestBuilder<RecurringTransactionWorker>().build()
        )
    }
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                this,
                "Senza permesso non riceverai notifiche automatiche.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}



