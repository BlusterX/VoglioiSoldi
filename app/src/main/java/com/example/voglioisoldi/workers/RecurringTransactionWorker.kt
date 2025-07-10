package com.example.voglioisoldi.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.voglioisoldi.R
import com.example.voglioisoldi.data.repositories.TransactionRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

class RecurringTransactionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val transactionRepository: TransactionRepository by inject()

    @SuppressLint("DefaultLocale")
    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val recurringTx = transactionRepository.getActiveRecurringTransactions()

        for (tx in recurringTx) {
            val lastExec = tx.lastExecutionDate ?: tx.date
            val periodMillis = (tx.recurringPeriodMinutes ?: 0) * 60 * 1000L
            if (periodMillis > 0 && now - lastExec >= periodMillis) {
                val newTx = tx.copy(
                    id = 0,
                    date = now,
                    isRecurring = false,
                    isRecurringActive = false,
                    lastExecutionDate = null
                )
                transactionRepository.insertTransaction(newTx)
                transactionRepository.updateTransaction(tx.copy(lastExecutionDate = now))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "recurring_tx_channel",
                        "Transazioni Ricorrenti",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    val manager = applicationContext.getSystemService(NotificationManager::class.java)
                    manager.createNotificationChannel(channel)
                }
                Log.d("WORKER", "Creo la notifica automatica di ${newTx.amount}")
                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notif = NotificationCompat.Builder(applicationContext, "recurring_tx_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Transazione automatica eseguita")
                    .setContentText("Transazione automatica di €${String.format("%.2f", newTx.amount)} eseguita.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()
                notificationManager.notify((now + Random.nextInt(1000)).toInt(), notif)
            }
        }
        return Result.success()
    }
}