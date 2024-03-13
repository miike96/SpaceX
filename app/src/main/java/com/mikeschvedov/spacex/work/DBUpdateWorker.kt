package com.mikeschvedov.spacex.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mikeschvedov.spacex.R
import com.mikeschvedov.spacex.data.mediator.ContentMediator
import com.mikeschvedov.spacex.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltWorker
class DBUpdateWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    val contentMediator: ContentMediator
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        // example of getting data from params
        // use the data in the worker
        val data = params.inputData.getInt(EXTRA_VALUE, DEFAULT_VALUE)

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        println("Did work at, current time: $formatted")

        checkIfNewLaunchesAvailable()

        // Return a result with output data
        return Result.success(workDataOf(OUTPUT_EXTRA to "Done"))
    }

    companion object {
        // Input - Output Extras and Values
        const val EXTRA_VALUE = "extra_name"
        const val DEFAULT_VALUE = 220
        const val OUTPUT_EXTRA = "result_extra"

        // Notification
        const val CHANNEL_ID = "channel001"
        const val CHANNEL_NAME = "channel_name"
        const val CHANNEL_DESCRIPTION = "channel_description"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_TITLE = "New Launches Available."
        const val NOTIFICATION_CONTENT = "Check out the new launches."
        // Intent
        const val CLICKED_EXTRA_NAME = "FragmentToOpen"
        const val CLICKED_EXTRA_VALUE = "LaunchesFragment"
    }

    private suspend fun checkIfNewLaunchesAvailable() {
        coroutineScope {
            contentMediator.checkIfNewLaunchesAvailable()
                .collectLatest { newLaunchesAvailable: Boolean ->
                    if (newLaunchesAvailable) {
                        sendNotification(NOTIFICATION_TITLE, NOTIFICATION_CONTENT)
                    } else {
                        return@collectLatest
                    }
                }
        }
    }

    private fun sendNotification(notificationTitle: String, notificationContent: String) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                    .apply {
                        description = CHANNEL_DESCRIPTION
                    }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create an explicit intent for an Activity in your app
        // If the user clicks on the notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(CLICKED_EXTRA_NAME, CLICKED_EXTRA_VALUE)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Building the notification
        // And provide the Channel Id that we created
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.launch)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

}