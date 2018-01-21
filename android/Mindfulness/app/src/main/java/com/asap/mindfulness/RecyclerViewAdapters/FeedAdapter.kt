package com.asap.mindfulness.RecyclerViewAdapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.asap.mindfulness.Containers.FeedItem
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

class FeedAdapter(private val items : List<FeedItem>) : RecyclerView.Adapter<FeedItem.Holder>() {

    var navigationListener: OnNavigationRequestListener? = null

    fun attachOnNavigationRequestListener(listener: OnNavigationRequestListener?) : FeedAdapter {
        navigationListener = listener
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedItem.Holder {
        return FeedItem.Holder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_feed, parent, false))
    }

    override fun onBindViewHolder(holder: FeedItem.Holder?, position: Int) {
        holder?.populate(items[position], navigationListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}