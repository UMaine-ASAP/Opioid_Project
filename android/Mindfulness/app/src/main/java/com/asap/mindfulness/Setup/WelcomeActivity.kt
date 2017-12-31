package com.asap.mindfulness.Setup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.asap.mindfulness.ParentActivity
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
 * TODO... a lot easier than that sounds)
 */


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Check if user is already set up and skip to ParentActivity
        if (/* TODO: Logic for the check */ true) {
            val intent = Intent(this, ParentActivity::class.java)
            startActivity(intent)
        }
        // Otherwise, start creating the activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Set the Welcome Button to launch the SetupActivity
        welcome_button.setOnClickListener { _ ->
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }
    }
}