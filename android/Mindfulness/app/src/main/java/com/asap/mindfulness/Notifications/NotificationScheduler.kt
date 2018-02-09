package com.asap.mindfulness.Notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.asap.mindfulness.QuoteActivity
import com.asap.mindfulness.R
import java.util.*

/**
 * Created by SWK on 1/31/2018.
 */

class NotificationScheduler : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }

        // Grab a random quote
        val quotesList = context.resources.getStringArray(R.array.quotes_array)
        val quotesListCredits = context.resources.getStringArray(R.array.quotes_credits)
        val quoteNum = Random().nextInt(quotesList.size)

        // The id of the channel.
        val CHANNEL_ID = CHANNEL_ID
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notify_reminder_title))
                .setContentText(quotesList[quoteNum] + quotesListCredits[quoteNum])
        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(context, QuoteActivity::class.java)

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        val stackBuilder = TaskStackBuilder.create(context)
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(QuoteActivity::class.java)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    companion object {
        private const val CHANNEL_ID = "mindfulness_notifications"
        private const val NOTIFICATION_ID = 91231923

        @SuppressLint("NewApi")
        fun scheduleNotifications(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Build notification channel
                val name = context.getString(R.string.notify_channel_name)
                val notifChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
                notifChannel.description = context.getString(R.string.notify_channel_desc)
                notifChannel.enableLights(false)
                notifChannel.enableVibration(true)
                notifChannel.vibrationPattern = arrayOf(100L).toLongArray()
            }

            // Scheduling notifications
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = PendingIntent.getBroadcast(context, 0,
                    Intent(context, NotificationScheduler::class.java), 0)
            val prefs = context.getSharedPreferences(context.getString(R.string.sp_file_key), Context.MODE_PRIVATE)

            val cal = Calendar.getInstance()
            with (cal) {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY,
                        prefs.getInt(context.getString(R.string.sp_notification_hour), 2))
                set(Calendar.MINUTE,
                        prefs.getInt(context.getString(R.string.sp_notification_minute), 35))
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC, cal.timeInMillis,
                    AlarmManager.INTERVAL_DAY, alarmIntent)


        }
    }
}