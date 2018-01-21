package com.asap.mindfulness.RecyclerViewAdapters

import android.content.Intent
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.asap.mindfulness.Containers.Resource
import com.asap.mindfulness.Fragments.OnNavigationRequestListener
import com.asap.mindfulness.R
import com.asap.mindfulness.WebViewActivity
import kotlinx.android.synthetic.main.card_resource.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * An adapter to be used with the RecyclerView for the Feed fragment.
 *
 */

class ResourceAdapter(private val items : List<Resource>) : RecyclerView.Adapter<Resource.Holder>() {


    var navigationListener: OnNavigationRequestListener? = null

    fun attachOnNavigationRequestListener(listener: OnNavigationRequestListener?) : ResourceAdapter {
        navigationListener = listener
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Resource.Holder {
        return Resource.Holder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_resource, parent, false))
    }

    override fun onBindViewHolder(holder: Resource.Holder?, position: Int) {
        holder?.populate(items[position], navigationListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}