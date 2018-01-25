package com.asap.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.asap.mindfulness.Views.UserSettingsItem
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.user_settings_item.*

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        user_name.populate("Name", "Jane Doe")
    }
}
