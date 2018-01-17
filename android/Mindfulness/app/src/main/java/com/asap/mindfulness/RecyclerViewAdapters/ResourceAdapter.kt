package com.asap.mindfulness.RecyclerViewAdapters

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.asap.mindfulness.Containers.Resource
import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.card_resource.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * An adapter to be used with the RecyclerView for the Feed fragment.
 *
 */

class ResourceAdapter(private val items : List<Resource>) : RecyclerView.Adapter<ResourceAdapter.ResourceHolder>() {
    class ResourceHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var extra: TextView
        var image: ImageView

        init {
            title = itemView.resource_title
            extra = itemView.resource_extra
            image = itemView.resource_icon
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResourceHolder {
        return ResourceHolder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_resource, parent, false))
    }

    override fun onBindViewHolder(holder: ResourceHolder?, position: Int) {
        holder?.title?.text =  items[position].title
        holder?.extra?.text = items[position].extra
        holder?.image?.setImageResource((items[position].image))
    }

    override fun getItemCount(): Int {
        return items.size
    }
}