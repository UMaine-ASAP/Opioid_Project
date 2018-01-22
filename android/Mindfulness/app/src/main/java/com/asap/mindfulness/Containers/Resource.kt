package com.asap.mindfulness.Containers

import android.support.v4.content.res.ResourcesCompat
import com.asap.mindfulness.R
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.asap.mindfulness.Fragments.OnNavigationRequestListener
import kotlinx.android.synthetic.main.card_resource.view.*

/**
 * @author Spencer Ward
 * @created November 14, 2017
 *
 * A simple data class for holding resources.
 * @property title: Website, Video, Audio, Survey:
 *                      Title of the resource
 *                  Introduction:
 *                      Static title pulled in from @strings
 * @property extra: Websites, Video, Audio:
 *                      Web URL
*                   Survey:
 *                      Date requested
 *                  Introduction
 *                      Date started the app
 * @property type: Resource.WEBSITE
 *                 Resource.VIDEO
 *                 Resource.AUDIO
 *                 Resource.SURVEY
 *                 Resource.INTRODUCTION
 * @property image: Website
 *                      Holds the id of the favicon as a String
 *                  Video, Audio, Survey, Introduction
 *                      Ignored
 *
 */

class Resource(val title : String, val extra: String, val type: Int, val image: Int) {
    companion object {
        const val WEBSITE = 0
        const val VIDEO = 1
        const val AUDIO = 2
        const val SURVEY = 3
        const val INTRODUCTION = 4
    }

    class Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var extra: TextView
        var image: ImageView

        init {
            title = itemView.resource_title
            extra = itemView.resource_extra
            image = itemView.resource_icon
        }

        fun populate(res: Resource, navigationListener: OnNavigationRequestListener?) {
            title.text = res.title
            extra.text = res.extra
            image.setImageResource((res.image))
            itemView.setOnClickListener { _ ->
                if (res.type < Resource.INTRODUCTION) {
                    navigationListener?.onWebViewRequested(res.extra)
                } else {
                    // TODO: Launch Introduction Activity
                }

            }
        }
    }
}
