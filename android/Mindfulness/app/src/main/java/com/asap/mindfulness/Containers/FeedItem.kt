package com.asap.mindfulness.Containers

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.card_feed.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * A simple data class for holding cards in the feed
 *
 */

class FeedItem(val title : String) {

    class Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val title : TextView

        init {
            title = itemView.feed_title
        }
    }
}
