package com.asap.mindfulness

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.asap.mindfulness.Views.UserSettingsItem
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.user_settings_item.*

class UserActivity : AppCompatActivity() {

    lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        mPrefs = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        user_name.populate("Name", mPrefs.getString(getString(R.string.sp_name), "None"))
    }
}
