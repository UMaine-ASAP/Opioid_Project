package com.asap.mindfulness.Setup

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import com.asap.mindfulness.ParentActivity
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.activity_setup.*

/**
 * @author Spencer Ward
 * @created November 20, 2017
 *
 * An activity for paging through the setup steps (stored as fragments).
 */

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        setup_pager.adapter = SetupPagerAdapter(supportFragmentManager)
        setup_pager.setOnTouchListener { _, _ ->
            setup_pager.currentItem = setup_pager.currentItem
            true
        }
        setup_progress.setViewPager(setup_pager)

        setup_button.setOnClickListener {
            // Grab Patient Name
            // Grab Patient ID
            var current = setup_pager.currentItem
            if (current == 0) {
                setup_pager.currentItem = 1
                setup_button.text = getString(R.string.setup_finish)
            } else {
                val main = Intent(this, ParentActivity::class.java)
                startActivity(main)
            }
        }
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
