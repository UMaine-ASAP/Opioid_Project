package com.asap.mindfulness

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Secure
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

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
    }

    /**
     * A recursive helper function to convert a number to its word counterpart
     * @param number The number to be converted
     * @return The string describing the number in english
     */
    fun convertNum(number: Int): String {
        if (number < 20) {
            return when (number) {
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

        if (number < 1000) {
            // Separate the number into a hundreds place and a remainder
            val hund = number / 100     // 99 < hund < 1000
            val rem = number % 100      // 0 < rem < 100

            return if (rem > 0) {
                convertNum(hund) + " hundred " + convertNum(rem)
            } else {
                convertNum(hund) + " hundred"
            }
        }

        else return "Hopefully this will never show up"
    }
}
