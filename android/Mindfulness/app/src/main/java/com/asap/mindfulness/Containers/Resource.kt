package com.asap.mindfulness.Containers

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import com.asap.mindfulness.R
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.asap.mindfulness.Fragments.OnNavigationRequestListener
import com.asap.mindfulness.IntroductionActivity
import com.asap.mindfulness.QuoteActivity
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

class Resource(context: Context, val title : String, var extra: String, val type: Int) {
    companion object {
        const val WEBSITE = 0
        const val VIDEO = 1
        const val AUDIO = 2
        const val SURVEY = 3
        const val QUOTES = 4
        const val INTRODUCTION = 5
    }
    val image: Int

    init {
        if (type == SURVEY) {
            extra = context.getSharedPreferences(context.getString(R.string.sp_file_key),
                    Context.MODE_PRIVATE).getString(context.getString(R.string.sp_last_survey_link),
                    "No survey assigned!")
        }

        image = when(type) {
//                        Resource.WEBSITE -> getResources().getIdentifier(cursor.getString(4), "drawable", "com.asap.mindfulness.Fragments")
            WEBSITE -> R.drawable.ic_resource_web
            VIDEO -> R.drawable.ic_resource_video
            AUDIO -> R.drawable.ic_resource_audio
            SURVEY -> R.drawable.ic_resource_survey
            INTRODUCTION -> R.drawable.ic_resource_intro
            QUOTES -> R.drawable.ic_resource_quotes
            else -> R.drawable.ic_dashboard_black_24dp
        }
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

            extra.visibility = if (res.extra == "None") {
                View.GONE
            } else {
                View.VISIBLE
            }

            extra.text = if (res.extra.length > 30) {
                res.extra.substring(0, 27) + "..."
            } else {
                res.extra
            }

            Log.d("Resources", res.type.toString())
//            if (res.type == WEBSITE) {
//                Log.d("Resources", "Changing Tint!")
//                ImageViewCompat.setImageTintList(itemView.resource_icon,
//                        ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.colorPrimary)))
//            } else {
//                // Reset the tint if the view is being recycled
//                ImageViewCompat.setImageTintList(itemView.resource_icon,null)
//            }

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
                    val introIntent = Intent(itemView.context, IntroductionActivity::class.java)
                    itemView.context.startActivity(introIntent)
                }
                else -> { _: View? ->
                    navigationListener?.onWebViewRequested(res.extra)
                }
            })
        }
    }
}
