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

class TrackAdapter(private val items: List<Track>): RecyclerView.Adapter<Track.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Track.Holder {
        // Create a new Track.Holder bound to a new Card view
        return Track.Holder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_track, parent, false))
    }

    override fun onBindViewHolder(holder: Track.Holder?, position: Int) {
        // Initialize card components
        holder?.populate(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}