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

        user_name.populate("Your Name", mPrefs.getString(getString(R.string.sp_name), "None"))
        user_id.populate("Your Study ID", mPrefs.getInt(getString(R.string.sp_study_id), 0).toString())
//        device_id.populate("Device ID", Secure.getString(contentResolver, Secure.ANDROID_ID))
    }
}
