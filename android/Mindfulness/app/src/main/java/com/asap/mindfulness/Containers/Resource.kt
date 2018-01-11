package com.asap.mindfulness.Containers

import com.asap.mindfulness.R

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

class Resource(val title : String, val extra: String, val type: Int, val image: String) {
    var img: Int = 0
    companion object {
        val WEBSITE = 0
        val VIDEO = 1
        val AUDIO = 2
        val SURVEY = 3
        val INTRODUCTION = 4
    }

    init {
//        img = when(type) {
//            WEBSITE -> Resource.getIdentifier(image)
//            VIDEO -> R.drawable.icon_video
//            AUDIO -> R.drawable.icon_audio
//            SURVEY -> R.drawable.icon_survey
//            INTRODUCTION -> R.drawable.icon_intro
//            else -> R.drawable.ic_dashboard_black_24dp
//        }
    }
}
