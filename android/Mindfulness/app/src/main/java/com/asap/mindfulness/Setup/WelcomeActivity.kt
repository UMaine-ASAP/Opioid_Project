package com.asap.mindfulness.Setup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.activity_welcome.*

/**
 * @author Spencer Ward
 * @created November 21, 2017
 *
 * The launcher activity, used to welcome a new user to the app and direct them to the setup steps
 * (held in SetupActivity) or redirect them to the daily feed if they've already been set up.
 */

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if user is already set up and skip to ParentActivity

        setContentView(R.layout.activity_welcome)

        welcome_button.setOnClickListener { _ ->
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }
    }
}