package com.asap.mindfulness.Notifications

import android.app.job.JobScheduler
import android.content.Context

/**
 * Created by SWK on 1/30/2018.
 */

fun scheduleNotifications(context: Context) {
    // Scheduling notifications
    val jobs = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
}