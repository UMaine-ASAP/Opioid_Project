package com.asap.mindfulness.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asap.mindfulness.Containers.FeedItem
import com.asap.mindfulness.Containers.Resource
import com.asap.mindfulness.Containers.Track

import com.asap.mindfulness.R
import com.asap.mindfulness.RecyclerViewAdapters.FeedAdapter
import com.asap.mindfulness.SQLite.DatabaseClass
import kotlinx.android.synthetic.main.content_scrolling.view.*

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
    private val feedItems = ArrayList<FeedItem>()
    private val resources = ArrayList<Resource>()
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load in resources from SQLite
        val db = DatabaseClass(context, "Updatables").readableDatabase
        val cursor = db.query(true, "Resources", arrayOf("Title", "Extra", "Type", "Image"),
                null, null, "Type", null, null, null)

        while (!cursor.isLast) {
            cursor.moveToNext()
            resources.add(Resource(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    when(cursor.getInt(2)) {
//                        Resource.WEBSITE -> getResources().getIdentifier(cursor.getString(4), "drawable", "com.asap.mindfulness.Fragments")
                        Resource.WEBSITE -> cursor.getInt(3)
//                        VIDEO -> R.drawable.icon_video
//                        AUDIO -> R.drawable.icon_audio
//                        SURVEY -> R.drawable.icon_survey
//                        INTRODUCTION -> R.drawable.icon_intro
                        else -> R.drawable.ic_dashboard_black_24dp
                    }))
        }

        cursor.close()

        feedItems.add(FeedItem("First", "Thing", FeedItem.PROGRESS))
        feedItems.add(FeedItem("Second", "Card", FeedItem.SURVEY))

        val trackTitles = getResources().getStringArray(R.array.track_titles)
        val trackDescriptions = getResources().getStringArray(R.array.track_descs)
        val trackCredits = getResources().getStringArray(R.array.track_credits)
        val trackLengths = getResources().getStringArray(R.array.track_lengths)

        val trackNumber = context.getSharedPreferences(getString(R.string.sp_file_key), android.content.Context.MODE_PRIVATE).
                getInt(getString(R.string.sp_current_track), 0)
        track = Track(trackTitles[trackNumber], trackDescriptions[trackNumber],
                trackCredits[trackNumber], trackLengths[trackNumber])
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_feed, container, false)

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
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
