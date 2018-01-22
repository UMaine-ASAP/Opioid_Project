package com.asap.mindfulness

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.asap.mindfulness.Fragments.FeedFragment
import com.asap.mindfulness.Fragments.OnNavigationRequestListener
import com.asap.mindfulness.Fragments.ResourceFragment
import com.asap.mindfulness.Fragments.TracksFragment

import kotlinx.android.synthetic.main.activity_parent.*
import kotlinx.android.synthetic.main.fragment_parent.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * The primary activity for the app, handling navigation and paging through each of the three
 * fragments. The hierarchical parent of all activities except WelcomeActivity and SetupActivity.
 *
 * Consists of a Bottom Navigation Bar and a ViewPager. The pager's fragments can be scrolled using
 * the bar or through swiping.
 */

class ParentActivity : AppCompatActivity(), OnNavigationRequestListener {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent)

//        setSupportActionBar(toolbar)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // Don't care
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Don't care
            }

            override fun onPageSelected(position: Int) {
                bottom_nav.selectedItemId = when (position) {
                    0 -> R.id.bottom_nav_feed
                    1 -> R.id.bottom_nav_tracks
                    2 -> R.id.bottom_nav_resources
                    else -> 0
                }
            }
        })

        bottom_nav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_feed -> {
                    container.currentItem = 0
                    true
                }
                R.id.bottom_nav_tracks -> {
                    container.currentItem = 1
                    true
                }
                R.id.bottom_nav_resources -> {
                    container.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_parent, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return false
    }

    /**
     * A callback to allow child fragments to request page changes.
     * @param page: The page to move to, from 0 to 2
     */
    override fun onPageRequested(page: Int) : Boolean {
        if (page > -1 && page < 3) {
            container.currentItem = page
            return true
        }

        return false
    }

    override fun onWebViewRequested(url: String): Boolean {
        if (url != "") {
            val webView = Intent(baseContext, WebViewActivity::class.java)
            webView.putExtra("url", url)
            Log.d("ParentActivity", "Got this far!!!!!!!!!!!!!!!!!!!!!!!!!")
            startActivity(webView)
            return true
        }

        return false
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> FeedFragment.newInstance()
                1 -> TracksFragment.newInstance()
                2 -> ResourceFragment.newInstance()
                else -> FeedFragment.newInstance()
            }
        }

        override fun getCount() = 3
    }
}
