package com.asap.mindfulness.Notifications

import android.annotation.TargetApi
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.asap.mindfulness.QuoteActivity

import com.asap.mindfulness.R
import java.util.*

/**
 * Helper class for showing and canceling quote
 * notifications.
 *
 *
 * This class makes heavy use of the [NotificationCompat.Builder] helper
 * class to create notifications in a backward-compatible way.
 */
class NotificationHandler : BroadcastReceiver() {

    /**
     * Pick a random quote and display it as a notification when the broadcast is received
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Notifications", "Broadcast received")
        // We can't pull quotes if the context is null
        if (context == null) return

        val quotesList = context.resources.getStringArray(R.array.quotes_array)
        val quotesListCredits = context.resources.getStringArray(R.array.quotes_credits)
        val chosenQuote = Random().nextInt(quotesList.size)

        notify(context, quotesList[chosenQuote], quotesListCredits[chosenQuote])
    }

    companion object {
        /**
         * The unique identifier for this type of notification.
         */
        private const val NOTIFICATION_CHANNEL = "mindfulness_notifications"
        /**
         * The request code for the pending intent
         */
        private const val REQUEST_CODE = 28173891

        /**
         * Schedules notifications for the times held in SharedPreferences
         */
        fun scheduleNotifications(context: Context) {
            // Build the notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buildChannel(context)
            }

            // Schedule notifications
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                    Intent(context, NotificationHandler::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            val prefs = context.getSharedPreferences(context.getString(R.string.sp_file_key), Context.MODE_PRIVATE)

            val cal = Calendar.getInstance()
            with(cal) {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY,
                        prefs.getInt(context.getString(R.string.sp_notification_hour), 8))
                set(Calendar.MINUTE,
                        prefs.getInt(context.getString(R.string.sp_notification_minute), 0))
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC, cal.timeInMillis,
                    AlarmManager.INTERVAL_DAY, alarmIntent)
        }

        @TargetApi(26)
        private fun buildChannel(context: Context) {
            // Build notification channel
            val name = context.getString(R.string.notify_channel_name)
            val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL, name, NotificationManager.IMPORTANCE_LOW)
            notifChannel.description = context.getString(R.string.notify_channel_desc)
            notifChannel.enableLights(false)
            notifChannel.enableVibration(true)
            notifChannel.vibrationPattern = arrayOf(100L).toLongArray()

            // Register the notification channel
            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(notifChannel)
        }

        /**
         * Shows the notification, or updates a previously shown notification of
         * this type, with the given parameters.
         *
         *
         * TODO: Customize this method's arguments to present relevant content in
         * the notification.
         *
         *
         * TODO: Customize the contents of this method to tweak the behavior and
         * presentation of quote notifications. Make
         * sure to follow the
         * [Notification design guidelines](https://developer.android.com/design/patterns/notifications.html) when doing so.
         *
         * @see .cancel
         */
        fun notify(context: Context,
                   quote: String, author: String) {

            Log.d("Notifications", "Notifying now!")

            val res = context.resources

            val title = res.getString(R.string.notify_quote_summary, author)
            val summary = title

            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)

                    // Set appropriate defaults for the notification light, sound,
                    // and vibration.
                    .setDefaults(Notification.DEFAULT_ALL)

                    // Set required fields, including the small icon, the
                    // notification title, and text.
                    .setSmallIcon(R.drawable.ic_stat_quote)
                    .setContentTitle(title)
                    .setContentText(quote)

                    // All fields below this line are optional.

                    // Use a default priority (recognized on devices running Android
                    // 4.1 or later)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                    // Set ticker text (preview) information for this notification.
                    .setTicker(summary)

                    // Set the pending intent to be initiated when the user touches
                    // the notification.
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    Intent(context, QuoteActivity::class.java),
                                    PendingIntent.FLAG_UPDATE_CURRENT))

                    // Show expanded text content on devices running Android 4.1 or
                    // later.
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(quote)
                            .setBigContentTitle(title))

                    // Automatically dismiss the notification when it is touched.
                    .setAutoCancel(true)

            notify(context, builder.build())
        }

        private fun notify(context: Context, notification: Notification) {
            val nm = context
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_CHANNEL.hashCode(), notification)
        }

        /**
         * Cancels any notifications of this type previously shown using
         * [.notify].
         */
        fun cancel(context: Context) {
            val nm = context
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(NOTIFICATION_CHANNEL.hashCode())
        }

        /**
         * Cancels future scheduled notifications
         */
        fun cancelScheduled(context: Context) {
            // Get the system AlarmManager
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // Rebuild the PendingIntent for the notification's alarm
            val alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                    Intent(context, NotificationHandler::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            // Cancel the scheduled alarm
            alarmManager.cancel(alarmIntent)
        }
    }
}
