package com.asap.mindfulness.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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

        val trackNumber = mPrefs.getInt(getString(R.string.sp_current_track), 0)
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

        // Get date of the most recent survey
        val lastSurveyDate = mPrefs.getLong(getString(R.string.sp_last_survey_date), 0)
        val surveyDate = Date(lastSurveyDate)
        val surveyMonth = DateFormat.format("MMMM", surveyDate).toString()
        val surveyDay = Integer.parseInt(DateFormat.format("dd", surveyDate).toString())
        // Get the link for the most recent survey
        val surveyLink = mPrefs.getString(getString(R.string.sp_last_survey_link), "")

        feedItems.add(FeedItem(getString(R.string.feed_progress_top),
                getString(R.string.feed_progress_bottom, daysPassed + 1), FeedItem.PROGRESS, openUserFragment))
        feedItems.add(FeedItem(getString(R.string.feed_survey_top),
                getString(R.string.feed_survey_bottom, surveyMonth, surveyDay), FeedItem.SURVEY, object: View.OnClickListener {
                    override fun onClick(p0: View?) {
                        mListener?.onWebViewRequested(surveyLink)
                    }
        }))


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
//            val fragment = FeedFragment()
//            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
//            args.putString(ARG_PARAM2, param2)
//            fragment.arguments = args
            return FeedFragment()
        }
    }
}
