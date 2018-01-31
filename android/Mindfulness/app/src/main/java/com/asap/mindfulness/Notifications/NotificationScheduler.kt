package com.asap.mindfulness.Notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.asap.mindfulness.R
import java.util.*

/**
 * Created by SWK on 1/31/2018.
 */

class NotificationScheduler : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

    }

    companion object {
        private const val NOTIFICATION_ID = "mindfulness_notifications"

        fun scheduleNotifications(context: Context) {
            // Scheduling notifications
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = PendingIntent.getBroadcast(context, 0,
                    Intent(context, NotificationScheduler::class.java), 0)
            val prefs = context.getSharedPreferences(context.getString(R.string.sp_file_key), Context.MODE_PRIVATE)

            val cal = Calendar.getInstance()
            with (cal) {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY,
                        prefs.getInt(context.getString(R.string.sp_notification_hour), 9))
                set(Calendar.MINUTE,
                        prefs.getInt(context.getString(R.string.sp_notification_minute), 0))
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC, cal.timeInMillis,
                    AlarmManager.INTERVAL_DAY, alarmIntent)

            // Build notification channel
//            val name = context.getString(R.string.notify_channel_name)
//            val desc = context.getString(R.string.notify_channel_desc)
//            val notChannel = NotificationChannel(NOTIFICATION_ID, name, NotificationManager.IMPORTANCE_LOW)
        }
    }
}