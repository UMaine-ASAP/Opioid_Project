package com.asap.mindfulness

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.activity_user.*
import android.content.pm.PackageManager
import android.content.ComponentName
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.asap.mindfulness.Fragments.NameDialogFragment
import com.asap.mindfulness.Notifications.NotificationBootHandler


class UserActivity : AppCompatActivity(), NameDialogFragment.OnNameChangeListener {

    lateinit var mPrefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        mPrefs = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        val userName = mPrefs.getString(getString(R.string.sp_name), "Tom Jones")
        val userId = mPrefs.getString(getString(R.string.sp_study_id), "0")
        val userDays = mPrefs.getInt(getString(R.string.sp_days_passed), 0)
        val userTracks = mPrefs.getInt(getString(R.string.sp_tracks_completed), 0)

        user_name.text = userName
        user_name.setOnClickListener { _ ->
            val fragTransaction = fragmentManager.beginTransaction()
            val prevFrag = fragmentManager.findFragmentByTag("user_name_dialog")
            if (prevFrag != null) {
                fragTransaction.remove(prevFrag)
            }
            fragTransaction.addToBackStack(null)

            NameDialogFragment.newInstance().show(fragTransaction, "user_name_dialog")
        }


        user_study_id.text = getString(R.string.user_study_id, userId)
        user_progress_days.text = if (userDays > 1) {
            resources.getQuantityString(
                    R.plurals.user_progress_days, userDays, convertNum(userDays))
        } else {
            getString(R.string.user_progress_days_none)
        }
        user_progress_tracks.text = if (userTracks > 0) {
            resources.getQuantityString(
                    R.plurals.user_progress_tracks, userTracks, convertNum(userTracks))
        } else {
            getString(R.string.user_progress_tracks_none)
        }

        // Grab time stuff
        val h = mPrefs.getInt(getString(R.string.sp_notification_hour), 8)
        val m = mPrefs.getInt(getString(R.string.sp_notification_minute), 0)

        val amPM: String
        val h12 = if (h < 12) {
            amPM = "am"
            h
        } else {
            amPM = "pm "
            h - 12
        }

        user_notifications_time_picker.text = String.format("%d:%02d %s", h12, m, amPM)

        user_notifications_switch.isChecked = mPrefs.getBoolean(getString(R.string.sp_notification_enabled), true)
        user_notifications_enabled.text = if (user_notifications_switch.isChecked) {
            getString(R.string.user_notify_enabled)
        } else {
            getString(R.string.user_notify_disabled)
        }

        user_notifications_switch.setOnClickListener { _ ->
            TransitionManager.beginDelayedTransition(user_views)
            if (user_notifications_switch.isChecked) {
                // Update the UI
                user_notifications_enabled.text = getString(R.string.user_notify_enabled)
                // Update settings
                with (mPrefs.edit()) {
                    putBoolean(getString(R.string.sp_notification_enabled), true)
                    apply()
                }
                // Enable the on-boot receiver
                val receiver = ComponentName(this, NotificationBootHandler::class.java)
                packageManager.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP)
            } else {
                // Update the UI
                user_notifications_enabled.text = getString(R.string.user_notify_disabled)
                // Update settings
                with (mPrefs.edit()) {
                    putBoolean(getString(R.string.sp_notification_enabled), false)
                    apply()
                }
                // Disable the on-boot receiver
                val receiver = ComponentName(this, NotificationBootHandler::class.java)
                packageManager.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP)
            }
        }

        val timePickerListener = { _: View, h: Int, m: Int ->
            with (mPrefs.edit()) {
                putInt(getString(R.string.sp_notification_hour), h)
                putInt(getString(R.string.sp_notification_minute), m)
                apply()
            }

            val amPM: String
            val h12 = if (h < 12) {
                amPM = "am"
                h
            } else {
                amPM = "pm "
                h - 12
            }

            user_notifications_time_picker.text = String.format("%d:%02d %s", h12, m, amPM)
        }

        user_notifications_time_picker.setOnClickListener { _ ->
            val hour = mPrefs.getInt(getString(R.string.sp_notification_hour), 8)
            val min = mPrefs.getInt(getString(R.string.sp_notification_minute), 0)
            val tp = TimePickerDialog(this, R.style.TimePickerDialogStyle, timePickerListener, hour, min, false)
            tp.show()
        }
    }

    override fun nameChanged(newName: String) {
        user_name.text = newName
    }

    companion object {
        /**
         * A recursive helper function to convert a number to its word counterpart
         * @param number The number to be converted
         * @return The string describing the number in english
         */
        fun convertNum(number: Int): String {
            if (number < 20) return when (number) {
                0 -> "zero"
                1 -> "one"
                2 -> "two"
                3 -> "three"
                4 -> "four"
                5 -> "five"
                6 -> "six"
                7 -> "seven"
                8 -> "eight"
                9 -> "nine"
                10 -> "ten"
                11 -> "eleven"
                12 -> "twelve"
                13 -> "thirteen"
                14 -> "fourteen"
                15 -> "fifteen"
                16 -> "sixteen"
                17 -> "seventeen"
                18 -> "eighteen"
                19 -> "nineteen"
                else -> "none"
            }

            if (number < 100) {
                // Separate the number into a tens place and a ones place
                val tens = number / 10      // 19 < tens < 100
                val ones = number % 10      // 0 < ones < 10

                val tenString = when (tens) {
                    2 -> "twenty"
                    3 -> "thirty"
                    4 -> "forty"
                    5 -> "fifty"
                    6 -> "sixty"
                    7 -> "seventy"
                    8 -> "eighty"
                    9 -> "ninety"
                    else -> "nonety"
                }

                // As long as the ones place isn't zero, return tenString-oneString
                return if (ones > 0) {
                    tenString + "-" + convertNum(ones)
                } else {
                    tenString
                }
            }

            return if (number < 1000) {
                // Separate the number into a hundreds place and a remainder
                val hund = number / 100     // 99 < hund < 1000
                val rem = number % 100      // 0 < rem < 100

                if (rem > 0) {
                    convertNum(hund) + " hundred " + convertNum(rem)
                } else {
                    convertNum(hund) + " hundred"
                }
            }
            else {
                "Hopefully this will never show up"
            }
        }
    }
}
