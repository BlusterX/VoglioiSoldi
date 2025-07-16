package com.example.voglioisoldi.workers

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.voglioisoldi.R
import com.example.voglioisoldi.data.database.entities.Notification
import com.example.voglioisoldi.data.repositories.NotificationRepository
import com.example.voglioisoldi.data.repositories.TransactionRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

class RecurringTransactionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val transactionRepository: TransactionRepository by inject()
    private val notificationRepository: NotificationRepository by inject()

    @SuppressLint("DefaultLocale")
    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val recurringTx = transactionRepository.getActiveRecurringTransactions()

        for (tx in recurringTx) {
            val lastExec = tx.lastExecutionDate ?: tx.date
            val periodMillis = (tx.recurringPeriodMinutes ?: 0) * 60 * 1000L
            //Fatto per test
//            if (periodMillis > 0 && now - lastExec >= periodMillis) {
                val newTx = tx.copy(
                    id = 0,
                    date = now,
                    isRecurring = false,
                    isRecurringActive = false,
                    lastExecutionDate = null
                )
                transactionRepository.insertTransaction(newTx)
                transactionRepository.updateTransaction(tx.copy(lastExecutionDate = now))

                val notifTitle = "Transazione automatica eseguita"
                val notifMessage = "Transazione automatica di €${String.format("%.2f", newTx.amount)} eseguita."
                val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notif = NotificationCompat.Builder(applicationContext, "recurring_tx_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(notifTitle)
                    .setContentText(notifMessage)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()
                notificationManager.notify((now + Random.nextInt(1000)).toInt(), notif)

                //Salva la notifica anche nel DB
                notificationRepository.insertNotification(
                    Notification(
                        title = notifTitle,
                        message = notifMessage,
                        timestamp = now,
                        userId = newTx.userId,  // attenzione a prendere il giusto userId!
                        isRead = false
                    )
                )
            }
        //}
        return Result.success()
    }
}
