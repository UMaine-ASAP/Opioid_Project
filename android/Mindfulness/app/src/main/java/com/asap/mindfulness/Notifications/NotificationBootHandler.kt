package com.asap.mindfulness.Notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Helper class for scheduling notifications on device boot.
 *
 * (notification alarm PendingIntents are cancelled on reboot)
 */

class NotificationBootHandler : BroadcastReceiver() {

    /**
     * Schedule a notification using the custom NotificationHandler scheduler.
     *
     * We don't need to check if notifications are enabled, since this receiver is disabled if the
     * user turns of notifications.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        // We can't do anything if the context is null
        if (context == null) return

        // Only trigger if the broadcast was received on boot
        if (intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationHandler.scheduleNotifications(context)
        }
    }

}