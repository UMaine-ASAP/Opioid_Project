package com.asap.mindfulness.RecyclerViewAdapters

import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.asap.mindfulness.Containers.Track
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.card_feed.view.*
import kotlinx.android.synthetic.main.card_track.view.*
import android.os.Bundle
import com.asap.mindfulness.MediaActivity

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * An adapter to be used with the RecyclerView for the Feed fragment.
 *
 * @property items: The list of Tracks displayed by the RecyclerView
 *
 */

class TrackAdapter(private val items: List<Track>): RecyclerView.Adapter<TrackAdapter.TrackHolder>() {
    /**
     * A simple ViewHolder for Track cards
     * @property title: The TextView holding the title of th\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
     */
    class TrackHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val desc: TextView
        val time: TextView
        val credits: TextView

        init {
            title = itemView.track_title
            desc = itemView.track_desc
            time = itemView.track_time
            credits = itemView.track_credits
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrackHolder {
        // Create a new TrackHolder bound to a new Card view
        return TrackHolder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_track, parent, false))
    }

    override fun onBindViewHolder(holder: TrackHolder?, position: Int) {
        // Initialize card components
        holder?.title?.text =  items[position].title
        holder?.desc?.text = items[position].desc
        holder?.credits?.text = items[position].credits
        holder?.time?.text = items[position].length

        holder?.time?.setOnClickListener { view ->
            val bundle = Bundle()
            val intent = Intent(view.context, MediaActivity::class.java)

            bundle.putString("title", items[position].title)
            bundle.putString("desc", items[position].desc)
            bundle.putInt("path", items[position].path)
            bundle.putInt("index", position)

            intent.putExtras(bundle)

            view.context.startActivity(intent)
        }

        // Set the card to float (backwards compatible)
        holder?.itemView?.card_track?.cardElevation = 8f
    }

    override fun getItemCount(): Int {
        return items.size
    }
}