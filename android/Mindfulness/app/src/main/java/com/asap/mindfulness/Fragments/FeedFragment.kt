package com.asap.mindfulness.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asap.mindfulness.Containers.FeedItem
import com.asap.mindfulness.Containers.Resource
import com.asap.mindfulness.Containers.Track

import com.asap.mindfulness.R
import com.asap.mindfulness.RecyclerViewAdapters.FeedAdapter
import com.asap.mindfulness.SQLite.DatabaseClass
import com.asap.mindfulness.UserActivity
import kotlinx.android.synthetic.main.fragment_feed.view.*
import java.util.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnNavigationRequestListener] interface
 * to handle interaction events.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment() {

    private var mListener: OnNavigationRequestListener? = null
    private lateinit var mPrefs: SharedPreferences
    private val feedItems = ArrayList<FeedItem>()
    private val resources = ArrayList<Resource>()
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No need to reload resources on a fragment refresh
        if (!resources.isEmpty()) return

        mPrefs = context.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        // Load in resources from SQLite
        val db = DatabaseClass(context, "Updatables").readableDatabase
        val cursor = db.query(true, "Resources", arrayOf("Title", "Extra", "Type", "Image"),
                null, null, null, null, "Type", null)

        while (!cursor.isLast) {
            cursor.moveToNext()
            resources.add(Resource(
                    context,
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2)))
        }

        cursor.close()

        val trackTitles = getResources().getStringArray(R.array.track_titles)
        val trackDescriptions = getResources().getStringArray(R.array.track_descs)
        val trackCredits = getResources().getStringArray(R.array.track_credits)
        val trackLengths = getResources().getStringArray(R.array.track_lengths)

        val trackNumber = mPrefs.getInt(getString(R.string.sp_tracks_current), 0)
        track = Track(trackTitles[trackNumber], trackDescriptions[trackNumber],
                trackCredits[trackNumber], trackLengths[trackNumber], trackNumber)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_feed, container, false)

        // Listener to launch the user fragment
        val openUserFragment = object: View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(context, UserActivity::class.java)
                startActivity(intent)
            }
        }

        // Get days passed since start date
        val daysPassed = mPrefs.getInt(getString(R.string.sp_days_passed), 0)
        var daysText = UserActivity.convertNum(daysPassed)
        daysText = daysText.substring(0, 1).toUpperCase() + daysText.substring(1)

        rootView.feed_recycler.adapter = FeedAdapter(track, feedItems, resources)
                .attachOnNavigationRequestListener(mListener)
        // Create grid layout for Cards
        val gridLayout = GridLayoutManager(context, 2)
        // Create a SpanSizeLookup to set the sizes for each column
        gridLayout.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> 2
                    1 -> 1
                    2 -> 1
                    3 -> 2
                    else -> 2
                }
            }
        }
        rootView.feed_recycler.layoutManager = gridLayout

        /*
         * Don't reload the FeedItems on Fragment refresh
         * This code needs to be in onCreateView instead of onCreate in order to bind the views for
         * SnackBars
         */
        if (feedItems.isEmpty()) {
            feedItems.add(FeedItem(getString(R.string.feed_progress_top),
                    getString(R.string.feed_progress_bottom, daysText), FeedItem.PROGRESS, openUserFragment))

            // Get date of the most recent survey
            val lastSurveyDate = mPrefs.getLong(getString(R.string.sp_last_survey_date), -1L)
            if (lastSurveyDate != -1L) {
                val surveyDate = Date(lastSurveyDate)
                val surveyMonth = DateFormat.format("MMMM", surveyDate).toString()
                val surveyDay = Integer.parseInt(DateFormat.format("dd", surveyDate).toString())
                // Get the link for the most recent survey
                val surveyLink = mPrefs.getString(getString(R.string.sp_last_survey_link), "")

                feedItems.add(FeedItem(getString(R.string.feed_survey_top),
                        getString(R.string.feed_survey_bottom, surveyMonth, surveyDay), FeedItem.SURVEY, object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        mListener?.onWebViewRequested(surveyLink)
                    }
                }))
            } else {
                feedItems.add(FeedItem(
                        getString(R.string.feed_no_survey_top),
                        getString(R.string.feed_no_survey_bottom),
                        FeedItem.SURVEY,
                        object : View.OnClickListener {
                            override fun onClick(p0: View?) {
                                Snackbar.make(rootView, "No surveys to take today", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                ))
            }
        }

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnNavigationRequestListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnNavigationRequestListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {
//        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//        private val ARG_PARAM1 = "param1"
//        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new FeedFragment instance
         *
         * @return A new instance of fragment FeedFragment.
         */
        fun newInstance(): FeedFragment {
            return FeedFragment()
        }
    }
}
