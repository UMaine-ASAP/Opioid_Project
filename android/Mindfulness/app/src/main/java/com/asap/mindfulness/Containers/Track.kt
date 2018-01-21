package com.asap.mindfulness.Containers

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.asap.mindfulness.MediaActivity
import kotlinx.android.synthetic.main.card_track.view.*

/**
 * @author Spencer Ward
 * @created November 31, 2017
 *
 * A simple data class for holding track info.
 * @property title: The title of the track
 * @property desc: The description for the track, usually multiline
 * @property credits: Credits for the track
 * @property length: The length of the track in seconds
 */

class Track(val title: String, val desc: String, val credits: String, val length: String) {
    var path : Int = 0

    /**
     * A simple ViewHolder for Track cards
     * @property title: The TextView holding the title of th\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
     */
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
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
        
        fun populate(track: Track, position: Int) {
            title.text =  track.title
            desc.text = track.desc
            credits.text = track.credits
            time.text = track.length

            time.setOnClickListener { view ->
                val bundle = Bundle()
                val intent = Intent(view.context, MediaActivity::class.java)

                bundle.putString("title", track.title)
                bundle.putString("desc", track.desc)
                bundle.putInt("path", track.path)
                bundle.putInt("index", position)

                intent.putExtras(bundle)

                view.context.startActivity(intent)
            }

            // Set the card to float (backwards compatible)
            itemView?.card_track?.cardElevation = 8f
        }
    }
}