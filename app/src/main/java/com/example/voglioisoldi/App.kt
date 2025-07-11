package com.example.voglioisoldi

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.voglioisoldi.workers.RecurringTransactionWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        val manager = getSystemService(NotificationManager::class.java)

        val recurringTxChannel = NotificationChannel(
            "recurring_tx_channel",
            "Transazioni Ricorrenti",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(recurringTxChannel)

        val registrationChannel = NotificationChannel(
            "registration_channel",
            "Notifica di Registrazione",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(registrationChannel)

        val workRequest = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
            15, TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RecurringTransactionWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}