package com.asap.mindfulness.Setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.asap.mindfulness.ParentActivity
import com.asap.mindfulness.QuoteActivity
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.activity_welcome.*

/**
 * @author Spencer Ward
 * @created November 21, 2017
 *
 * The launcher activity, used to welcome a new user to the app and direct them to the setup steps
 * (held in SetupActivity) or redirect them to the daily feed if they've already been set up.
 *
 * Consists of some welcome text and a button. Doesn't load at all if the user data already exists.
 *
 * TODO: Add logic to check if the user's been setup (SQLite tools from Jason should make that
 * TODO... a lot easier than that sounds).
 * TODO: Tie in background login and info pull with Retrofit, if necessary.
 */


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Check if user is already set up and skip to ParentActivity
        if (!getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)
                        .getString(getString(R.string.sp_name), "").equals("", false)) {
            Log.d("WelcomeActivity", getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)
                    .getString(getString(R.string.sp_name), ""))
            val intent = Intent(this, QuoteActivity::class.java)
            startActivity(intent)
        }
        // Otherwise, start creating the activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // TODO: Create background activity to refresh login - can be setup with retrofit and SQLite
        // TODO... to make into a fire-and-forget runnable

        // Set the Welcome Button to launch the SetupActivity
        welcome_button.setOnClickListener { _ ->
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }
    }
}