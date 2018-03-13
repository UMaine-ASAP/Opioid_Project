package com.asap.mindfulness.Containers

import android.support.v7.widget.RecyclerView
import android.view.View
import com.asap.mindfulness.Fragments.OnNavigationRequestListener
import kotlinx.android.synthetic.main.card_feed.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * A simple data class for holding cards in the feed
 *
 */

class FeedItem(val line1: String, val line2: String, val type: Int, val onClickListener: View.OnClickListener) {

    companion object {
        const val PROGRESS = 0
        const val SURVEY = 1
    }

    class Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val top = itemView.feed_top
        private val bottom = itemView.feed_bottom

        fun populate(item: FeedItem, listener: OnNavigationRequestListener?) {
            top.text = item.line1
            if (item.line2 == "") {
                bottom.visibility = View.GONE
            } else {
                bottom.text = item.line2
            }
            itemView.setOnClickListener(item.onClickListener)

            // TODO: Set up listener to navigate the parent to a specific page
        }
    }
}
