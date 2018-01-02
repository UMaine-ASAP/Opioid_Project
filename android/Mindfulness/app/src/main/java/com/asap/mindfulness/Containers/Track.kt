package com.asap.mindfulness.Containers

/**
 * @author Spencer Ward
 * @created November 31, 2017
 *
 * A simple data class for holding track info.
 * @property title: The title of the track
 * @property desc: The description for the track, usually multiline
 * @property credits: Credits for the track
 * @property length: The length of the track in seconds
 */

class Track(val title: String, val desc: String, val credits: String, val length: String) {
    var path : Int = 0
}