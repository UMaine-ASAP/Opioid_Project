package com.asap.mindfulness.Fragments

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
import com.asap.mindfulness.Containers.Resource
import com.asap.mindfulness.Containers.Resource.Companion.AUDIO
import com.asap.mindfulness.Containers.Resource.Companion.INTRODUCTION
import com.asap.mindfulness.Containers.Resource.Companion.SURVEY
import com.asap.mindfulness.Containers.Resource.Companion.VIDEO

import com.asap.mindfulness.R
import com.asap.mindfulness.RecyclerViewAdapters.FeedAdapter
import com.asap.mindfulness.RecyclerViewAdapters.ResourceAdapter
import com.asap.mindfulness.SQLite.DatabaseClass
import kotlinx.android.synthetic.main.fragment_resource.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnNavigationRequestListener] interface
 * to handle interaction events.
 * Use the [ResourceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResourceFragment : Fragment() {

    private var mListener: OnNavigationRequestListener? = null
    private val resources = ArrayList<Resource>()

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
                        Resource.WEBSITE -> R.mipmap.wikipedia_favicon_round
//                        VIDEO -> R.drawable.icon_video
//                        AUDIO -> R.drawable.icon_audio
//                        SURVEY -> R.drawable.icon_survey
//                        INTRODUCTION -> R.drawable.icon_intro
                        else -> R.drawable.ic_dashboard_black_24dp
                    }))
        }

        cursor.close()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_resource, container, false)

        rootView.resource_recycler.adapter = ResourceAdapter(resources)
                .attachOnNavigationRequestListener(mListener)
        rootView.resource_recycler.layoutManager = LinearLayoutManager(context)

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
        fun newInstance(): ResourceFragment {
//            val fragment = FeedFragment()
//            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
//            args.putString(ARG_PARAM2, param2)
//            fragment.arguments = args
            return ResourceFragment()
        }
    }
}// Required empty public constructor
