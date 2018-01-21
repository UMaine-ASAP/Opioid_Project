package com.asap.mindfulness.Fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asap.mindfulness.Containers.FeedItem
import com.asap.mindfulness.Containers.Track

import com.asap.mindfulness.R
import com.asap.mindfulness.RecyclerViewAdapters.FeedAdapter
import com.asap.mindfulness.RecyclerViewAdapters.TrackAdapter
import kotlinx.android.synthetic.main.fragment_feed.view.*
import kotlinx.android.synthetic.main.fragment_tracks.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnNavigationRequestListener] interface
 * to handle interaction events.
 * Use the [TracksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TracksFragment : Fragment() {

    private var mListener: OnNavigationRequestListener? = null
    // List to hold our tracks as we load them
    private val trackList = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pull String arrays from resources
        val trackTitles = resources.getStringArray(R.array.track_titles)
        val trackDescriptions = resources.getStringArray(R.array.track_descs)
        val trackCredits = resources.getStringArray(R.array.track_credits)
        val trackLengths = resources.getStringArray(R.array.track_lengths)
        for (i in 0..5) {
            // Create track from resources
            trackList.add(
                    Track(trackTitles[i], trackDescriptions[i], trackCredits[i], trackLengths[i]))
            // Link track to the audio file
            trackList[i].path = when (i) {
                0 -> R.raw.track1
                1 -> R.raw.track2
                2 -> R.raw.track3
                3 -> R.raw.track4
                4 -> R.raw.track5
                5 -> R.raw.track6
                else -> R.raw.track1
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_tracks, container, false)

        // Set up our recycler adapter and layout
        rootView.track_recycler.adapter = TrackAdapter(trackList)
        rootView.track_recycler.layoutManager = LinearLayoutManager(context)

        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Grab callback from caller
        if (context is OnNavigationRequestListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnNavigationRequestListener")
        }
    }

    override fun onDetach() {
        super.onDetach()

        // Destroy the player
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
        fun newInstance(): TracksFragment {
//            val fragment = FeedFragment()
//            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
//            args.putString(ARG_PARAM2, param2)
//            fragment.arguments = args
            return TracksFragment()
        }
    }
}// Required empty public constructor
