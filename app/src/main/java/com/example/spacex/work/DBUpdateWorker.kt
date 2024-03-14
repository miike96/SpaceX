package com.example.spacex.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.spacex.R
import com.example.spacex.data.mediator.ContentMediator
import com.example.spacex.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltWorker
class DBUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val contentMediator: ContentMediator
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // example of getting data from params
        val data = inputData.getInt(EXTRA_VALUE, DEFAULT_VALUE)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        println("Did work at, current time: $formatted")

        checkIfNewLaunchesAvailable()

        return Result.success(workDataOf(OUTPUT_EXTRA to "Done"))
    }

    private suspend fun checkIfNewLaunchesAvailable() {
        contentMediator.checkIfNewLaunchesAvailable().collectLatest { newLaunchesAvailable ->
            if (newLaunchesAvailable) {
                sendNotification(NOTIFICATION_TITLE, NOTIFICATION_CONTENT)
            }
        }
    }

    private fun sendNotification(notificationTitle: String, notificationContent: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(CLICKED_EXTRA_NAME, CLICKED_EXTRA_VALUE)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.launch)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    companion object {
        const val EXTRA_VALUE = "extra_name"
        const val DEFAULT_VALUE = 220
        const val OUTPUT_EXTRA = "result_extra"
        const val CHANNEL_ID = "channel001"
        const val CHANNEL_NAME = "channel_name"
        const val CHANNEL_DESCRIPTION = "channel_description"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_TITLE = "New Launches Available."
        const val NOTIFICATION_CONTENT = "Check out the new launches."
        const val CLICKED_EXTRA_NAME = "FragmentToOpen"
        const val CLICKED_EXTRA_VALUE = "LaunchesFragment"
    }
}
