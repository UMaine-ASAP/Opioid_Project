package com.asap.mindfulness.Containers

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
 * @property image: The uri of the resource to be loaded in as the image
 *
 */

class Resource(val title : String, val extra: String, val type: Int) {
    var image: Int = 0
    companion object {
        val WEBSITE = 0
        val VIDEO = 1
        val AUDIO = 2
        val SURVEY = 3
        val INTRODUCTION = 4
    }
}
