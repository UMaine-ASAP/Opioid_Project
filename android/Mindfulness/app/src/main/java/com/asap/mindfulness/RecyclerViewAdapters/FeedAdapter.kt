package com.asap.mindfulness.RecyclerViewAdapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.asap.mindfulness.Containers.FeedItem
import com.asap.mindfulness.Containers.Resource
import com.asap.mindfulness.Containers.Track
import com.asap.mindfulness.Fragments.OnNavigationRequestListener
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.card_feed.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * An adapter to be used with the RecyclerView for the Feed fragment.
 *
 */

class FeedAdapter(private val track: Track,
                  private val feedItems: List<FeedItem>,
                  private val resources: List<Resource>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var navigationListener: OnNavigationRequestListener? = null

    fun attachOnNavigationRequestListener(listener: OnNavigationRequestListener?) : FeedAdapter {
        navigationListener = listener
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TRACK -> Track.Holder(LayoutInflater.from(parent?.context)
                    .inflate(R.layout.card_track, parent, false))
            FEED -> FeedItem.Holder(LayoutInflater.from(parent?.context)
                    .inflate(R.layout.card_feed, parent, false))
            RESOURCE -> Resource.Holder(LayoutInflater.from(parent?.context)
                    .inflate(R.layout.card_resource, parent, false))
            else -> FeedItem.Holder(LayoutInflater.from(parent?.context)
                    .inflate(R.layout.card_feed, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder?.itemViewType) {
            TRACK -> (holder as Track.Holder).populate(track)
            FEED -> (holder as FeedItem.Holder).populate(feedItems[position - 1], navigationListener)
            RESOURCE -> (holder as Resource.Holder).populate(resources[position - 3], navigationListener)
        }
    }

    override fun getItemCount(): Int {
        return feedItems.size + resources.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TRACK
            1 -> FEED
            2 -> FEED
            3 -> RESOURCE
            else -> RESOURCE
        }
    }

    companion object {
        const val TRACK = 0
        const val FEED = 1
        const val RESOURCE = 2
    }
}