package com.asap.mindfulness.Setup

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import com.asap.mindfulness.ParentActivity
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.activity_setup.*
import kotlinx.android.synthetic.main.fragment_setup_patient.*
import kotlinx.android.synthetic.main.fragment_setup_user.*

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
 * The fragments are currently SetupUserFragment and SetupPatientIdFragment.
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

    var patientName : String? = null
    var patientPassword : String? = null
    var patientId : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        // Initialize ViewPager and give it an instance of SetupPagerAdapter
        setup_pager.adapter = SetupPagerAdapter(supportFragmentManager)
        setup_pager.setOnTouchListener { _, _ ->
            setup_pager.currentItem = setup_pager.currentItem
            true
        }

        // Add the view pager to the progress bubbles
        setup_progress.setViewPager(setup_pager)

        // Set up the button to scroll to the next page and grab the information from the last page
        setup_button.setOnClickListener {
            // Checking the current page
            val current = setup_pager.currentItem
            if (current == 0) {
                if (scrollUser()) {
                    setup_pager.currentItem = 1
                    setup_button.text = getString(R.string.setup_finish)
                }
            } else {
                if (scrollPatient()) {
                    val main = Intent(this, ParentActivity::class.java)
                    startActivity(main)
                }
            }
        }
    }

    private fun scrollUser() : Boolean {
        patientName = patient_name.text.toString()
        patientPassword = patient_password.toString()

        if (patientName == "") {
            Snackbar.make(activity_setup, "Please enter a name for yourself",
                    Snackbar.LENGTH_SHORT).show()
            return false
        } else if (patientPassword == "" && patient_password_switch.isChecked) {
            Snackbar.make(activity_setup, "Please enter a password or uncheck the box",
                    Snackbar.LENGTH_SHORT).show()
            return false
        }

        // TODO: Store patientName and patientPassword asynchronously

        return true
    }

    private fun scrollPatient() : Boolean {
        patientId = patient_id.text.toString()

        if (patientId == "") {
            Snackbar.make(activity_setup, "Please enter a patient ID",
                    Snackbar.LENGTH_SHORT).show()
            return false
        }

        // TODO: Store patientId asynchronously

        return true
    }

    inner class SetupPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SetupUserFragment.newInstance()
                1 -> SetupPatientIdFragment.newInstance()
                else -> SetupUserFragment.newInstance()
            }
        }

        override fun getCount() = 2
    }
}
