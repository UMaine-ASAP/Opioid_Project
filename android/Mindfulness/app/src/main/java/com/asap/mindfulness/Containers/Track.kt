package com.asap.mindfulness.Containers

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.asap.mindfulness.MediaActivity
import com.asap.mindfulness.R
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

class Track(val title: String, val desc: String, val credits: String, val length: String, index: Int) : Parcelable {
    var path : Int = 0

    init {
        // Link track to the audio file
        path = when (index) {
            0 -> R.raw.track1
            1 -> R.raw.track2
            2 -> R.raw.track3
            3 -> R.raw.track4
            4 -> R.raw.track5
            5 -> R.raw.track6
            else -> R.raw.track1
        }
    }

    constructor(parcel: Parcel): this(parcel.readString(), parcel.readString(), parcel.readString(),
            parcel.readString(), parcel.readInt())

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(title)
        out.writeString(desc)
        out.writeString(credits)
        out.writeString(length)
        out.writeInt(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<Track> {
        override fun createFromParcel(source: Parcel?): Track {
            return if (source != null) {
                Track(source)
            } else {
                Track("Blank", "Track", "Contact", "Developer", -1)
            }
        }

        override fun newArray(size: Int): Array<Track?> {
            return Array(size, { _ -> null})
        }
    }

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

                with(bundle) {
                    putParcelable(MediaActivity.TRACK_INTENT, track)
                    putInt(MediaActivity.INDEX_INTENT, position)
                }

                intent.putExtras(bundle)
                view.context.startActivity(intent)
            }
        }
    }
}