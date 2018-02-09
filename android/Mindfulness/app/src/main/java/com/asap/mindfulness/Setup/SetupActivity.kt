package com.asap.mindfulness.Setup

import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import com.asap.mindfulness.Notifications.NotificationScheduler
import com.asap.mindfulness.ParentActivity
import com.asap.mindfulness.QuoteActivity
import com.asap.mindfulness.R
import com.asap.mindfulness.SQLite.SQLManager
import kotlinx.android.synthetic.main.activity_setup.*
import kotlinx.android.synthetic.main.fragment_setup_patient.*
import kotlinx.android.synthetic.main.fragment_setup_user.*
import kotlinx.android.synthetic.main.fragment_setup_user.view.*
import java.util.*

/**
 * @author Spencer Ward
 * @created November 20, 2017
 *
 * This is our app's second activity. It is only launched from the Welcome Button in the
 * WelcomeActivity.
 *
 * This activity consists of two parts: a ViewPager to scroll through the setup fragments, and
 * a button to move to the next step.
 *
 * The fragments are currently SetupUserFragment and SetupPatientFragment.
 * The button starts out as "Move On" and changes to "FinishSetup" when the pager's on the last
 * setup step.
 */

/*
 * TODO: Either completely disable touch scrolling on the pager, or take off the limiter and add a
 * TODO... listener to update the button text and grab the inputs from the last page
 * TODO: Store inputs and make sure everything's filled in
 */


class SetupActivity : AppCompatActivity() {

    // Use patient_password_switch.isChecked for the password checkbox
    lateinit var mPager: ViewPager
    lateinit var mPreferences: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        Log.d("TESTING", R.mipmap.google_favicon.toString())
        Log.d("TESTING", R.mipmap.wikipedia_favicon.toString())

        mPreferences = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE).edit()

        // Initialize ViewPager and give it an instance of SetupPagerAdapter
        mPager = setup_pager
        mPager.adapter = SetupPagerAdapter(supportFragmentManager)
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            // TODO: This is only half finished
            override fun onPageSelected(position: Int) {
                // Checking the current page
                val current = mPager.currentItem
                if (current == 1) {
                    if (scrollUser()) {
                        mPager.currentItem = 1
                        setup_button.text = getString(R.string.setup_finish)
                    } else {
                        mPager.currentItem = 0
                    }
                } else {
                    if (scrollPatient()) {
                        val main = Intent(parent, ParentActivity::class.java)
                        startActivity(main)
                    } else {
                        mPager.currentItem = 0
                    }
                }
            }

        })

        // Add the view pager to the progress bubbles
        setup_progress.setViewPager(mPager)

        // Set up the button to scroll to the next page and grab the information from the last page
        setup_button.setOnClickListener {
            // Checking the current page
            val current = mPager.currentItem
            if (current == 0) {
                if (scrollUser()) {
                    mPager.currentItem = 1
                    setup_button.text = getString(R.string.setup_finish)
                }
            } else {
                if (scrollPatient()) {
                    val main = Intent(this, QuoteActivity::class.java)
                    startActivity(main)
                }
            }
        }

        NotificationScheduler.scheduleNotifications(this)

        //Setting the date the user starts treatment
        val today: Long = Date().time
        mPreferences.putLong(getString(R.string.sp_start_date), today)
        mPreferences.putString(getString(R.string.sp_last_survey_link), getString(R.string.survey_link))
        mPreferences.apply()

        Log.d("TESTING", R.mipmap.google_favicon.toString())
        Log.d("TESTING", R.mipmap.wikipedia_favicon.toString())

        // Load in Resources to the DB
        val resourceTitles = resources.getStringArray(R.array.resource_titles)
        val resourceExtras = resources.getStringArray(R.array.resource_extras)
        val resourceTypes = resources.getIntArray(R.array.resource_types)
        val resourceImages = resources.getStringArray(R.array.resource_images)
        val db = SQLManager(this)
        db.registerDatabase("Updatables")

        // Create audio table
        db.pushColumn("ID", "INTEGER", "PRIMARY KEY, AUTO INCREMENT")
        db.pushColumn("track_number", "INTEGER")
        db.pushColumn("completion_status", "INTEGER")
        db.pushColumn("creation_date", "LONG")
        db.pushColumn("server_pushed", "INTEGER")
        db.createTable("Updatables", "Audio_History")

        // Create survey table
        db.pushColumn("ID", "INTEGER", "PRIMARY KEY, AUTO INCREMENT")
        db.pushColumn("resource_id", "INTEGER")
        db.pushColumn("rating", "INTEGER")
        db.pushColumn("creation_date", "LONG")
        db.pushColumn("server_pushed", "INTEGER")
        db.createTable("Updatables", "Survey_History")


        // Create Resources Table
        db.pushColumn("ID", "INTEGER", "PRIMARY KEY, AUTO INCREMENT")
        db.pushColumn("Title", "TEXT")
        db.pushColumn("Extra", "TEXT")
        db.pushColumn("Type", "INTEGER")
        db.pushColumn("Image", "TEXT")
        db.createTable("Updatables", "Resources")


        for (i in 0 until resourceTitles.size) {
            db.insertRow("Updatables", "Resources",
                    "Title, Extra, Type, Image",
                    String.format("%s,%s,%d,%s",
                            resourceTitles[i],
                            resourceExtras[i],
                            resourceTypes[i],
                            resourceImages[i]
                    )
            )

            Log.d("SetupActivity", R.mipmap.google_favicon.toString())
            Log.d("SetupActivity", R.mipmap.wikipedia_favicon.toString())
        }
    }

    private fun scrollUser() : Boolean {
        val patientName = patient_name.text.toString()
        val patientPasswordEnabled = patient_password_switch.isChecked
        val patientPassword = patient_password.text.toString()

        if (patientName == "") {
            Snackbar.make(activity_setup, "Please enter a name for yourself",
                    Snackbar.LENGTH_SHORT).show()
            return false
        } else if (patientPassword == "" && patient_password_switch.isChecked) {
            Snackbar.make(activity_setup, "Please enter a password or uncheck the box",
                    Snackbar.LENGTH_SHORT).show()
            return false
        }

        mPreferences
                .putString(getString(R.string.sp_name), patientName)
//                .putBoolean(getString(R.string.sp_password_enabled), patientPasswordEnabled)
//                .putString(getString(R.string.sp_password), patientPassword)
                .putBoolean(getString(R.string.sp_password_enabled), false)
                .putString(getString(R.string.sp_password), "")
                .apply()

        return true
    }

    private fun scrollPatient() : Boolean {
        val patientId = patient_id.text.toString()

        if (patientId == "") {
            Snackbar.make(activity_setup, "Please enter a patient ID",
                    Snackbar.LENGTH_SHORT).show()
            return false
        } else {
            mPreferences
                    .putString(getString(R.string.sp_study_id), patientId)
                    .apply()
        }

        return true
    }

    inner class SetupPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SetupUserFragment.newInstance()
                1 -> SetupPatientFragment.newInstance()
                else -> SetupUserFragment.newInstance()
            }
        }

        override fun getCount() = 2
    }
}
