package com.asap.mindfulness.RecyclerViewAdapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.asap.mindfulness.Containers.Track
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.card_feed.view.*
import kotlinx.android.synthetic.main.card_track.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * An adapter to be used with the RecyclerView for the Feed fragment.
 *
 */

class TrackAdapter(private val items: List<Track>): RecyclerView.Adapter<TrackAdapter.TrackHolder>() {
    class TrackHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val desc: TextView
        val time: TextView

        init {
            title = itemView.track_title
            desc = itemView.track_desc
            time = itemView.track_time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrackHolder {
        return TrackHolder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_feed, parent, false))
    }

    override fun onBindViewHolder(holder: TrackHolder?, position: Int) {
        holder?.title?.text =  items[position].title
        holder?.desc?.text = items[position].desc
        holder?.desc?.text = (items[position].length / 60).toString()
        holder?.itemView?.setOnClickListener { view ->
            // TODO: Logic for playing tracks
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}