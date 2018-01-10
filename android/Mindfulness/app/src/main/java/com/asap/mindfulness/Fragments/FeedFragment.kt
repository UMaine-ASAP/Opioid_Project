package com.asap.mindfulness.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asap.mindfulness.Containers.FeedItem

import com.asap.mindfulness.R
import com.asap.mindfulness.RecyclerViewAdapters.FeedAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (arguments != null) {
//            mParam1 = arguments.getString(ARG_PARAM1)
//            mParam2 = arguments.getString(ARG_PARAM2)
//        }

        // TODO: Put database and network requests here
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_feed, container, false)

        val feedItems = ArrayList<FeedItem>()
        feedItems.add(FeedItem("First"))
        feedItems.add(FeedItem("Second"))

        rootView.feed_recycler.adapter = FeedAdapter(feedItems)
        rootView.feed_recycler.layoutManager = LinearLayoutManager(context)

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
}// Required empty public constructor
