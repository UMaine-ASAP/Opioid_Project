package com.asap.mindfulness.Containers

import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import com.asap.mindfulness.R
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.asap.mindfulness.Fragments.OnNavigationRequestListener
import com.asap.mindfulness.MediaActivity
import com.asap.mindfulness.QuoteActivity
import kotlinx.android.synthetic.main.activity_quote.*
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
        const val QUOTES = 4
        const val INTRODUCTION = 5
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

            if (res.extra.length > 30) {
                extra.text = res.extra.substring(0, 27) + "..."
            } else if (res.extra == "None") {
                extra.visibility = View.GONE
            } else {
                extra.text = res.extra
            }

            Log.e("RESOURCEMe", res.image.toString())
            Log.e("RESOURCE", R.mipmap.wikipedia_favicon.toString())
            image.setImageResource(res.image)

            itemView.setOnClickListener(when(res.type) {
                QUOTES -> { _: View? ->
                    val quoteIntent = Intent(itemView.context, QuoteActivity::class.java)
                    quoteIntent.putExtra(QuoteActivity.MODE, QuoteActivity.MODE_BROWSER)
                    itemView.context.startActivity(quoteIntent)
                }
                INTRODUCTION -> { _: View? ->
                    Toast.makeText(itemView.context, "This will open the intro page", Toast.LENGTH_SHORT)
                            .show()
                }
                else -> { _: View? ->
                    navigationListener?.onWebViewRequested(res.extra)
                }
            })
        }
    }
}
