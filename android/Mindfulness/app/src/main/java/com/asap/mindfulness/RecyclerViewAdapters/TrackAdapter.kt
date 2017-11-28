package com.asap.mindfulness.RecyclerViewAdapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.asap.mindfulness.Containers.FeedItem
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.card_feed.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * An adapter to be used with the RecyclerView for the Feed fragment.
 *
 */

class TrackAdapter(private val items : List<FeedItem>) : RecyclerView.Adapter<TrackAdapter.FeedHolder>() {
    class FeedHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val title : TextView

        init {
            title = itemView.feed_title
        }
    }

    init {

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedHolder {
        return FeedHolder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_feed, parent, false))
    }

    override fun onBindViewHolder(holder: FeedHolder?, position: Int) {
        holder?.title?.text =  items[position].title
    }

    override fun getItemCount(): Int {
        return items.size
    }
}