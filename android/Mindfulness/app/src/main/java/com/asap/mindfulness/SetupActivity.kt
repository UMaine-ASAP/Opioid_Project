package com.asap.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

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

    }

    class SetupUserFragment : Fragment() {
        companion object {
            fun newInstance() : SetupUserFragment {
                return SetupUserFragment()
            }
        }
    }

    class SetupPatientIdFragment : Fragment() {
        companion object {
            fun newInstance() : SetupPatientIdFragment {
                return SetupPatientIdFragment()
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

        override fun getCount() = 3
    }
}
