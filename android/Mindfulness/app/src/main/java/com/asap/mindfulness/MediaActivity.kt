package com.asap.mindfulness

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_media.*
import kotlinx.android.synthetic.main.pause_popup_window.*
import kotlinx.android.synthetic.main.pause_popup_window.view.*


import java.util.concurrent.TimeUnit

class MediaActivity : AppCompatActivity() {


    // source of the audio to be played
    private var audioSource = R.raw.track1

    // private variables for the main view
    private lateinit var mediaPlayer: MediaPlayer
    private var playerReleased = true

    // private variables for the pop up view
    private lateinit var popupWindow: PopupWindow
    private lateinit var popupCounterTextView: TextView
    private lateinit var popupResumeButton: Button

    // private variables for the media player
    private var time = 0
    private var isPaused = false

    private val mUpdateTime = object : Runnable {
        override fun run() {

            if (!playerReleased) {
                textTime.text = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.currentPosition.toLong()).toString() +
                        ":" + (TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.currentPosition.toLong()) % 60).toString() +
                        " / " + TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.duration.toLong()) +
                        ":" + (TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.duration.toLong()) % 60).toString()
                textTime.postDelayed(this, 1000)
            }
        }
    }

    // counter for the media player
    var cd: CountDownTimer = object: CountDownTimer(300000, 1000) {
        override fun onTick(millisUntilFinished:Long) {
            popupCounterTextView.setText(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished).toString() + ":" + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)%60).toString())

        }
        override fun onFinish() {
            // exit the activity because time ran out
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        audioSource = intent.getIntExtra("path", R.raw.track3)


        //creating mediaplayer and starting the audio
        mediaPlayer = MediaPlayer.create(this, audioSource)
        mediaPlayer.start()
        playerReleased = true

        textTitle.text = intent.getStringExtra("title")
        textTime.post(mUpdateTime)

        pauseButton.setOnClickListener { _ ->
            if(!isPaused)
                pause()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(mediaPlayer.duration * 0.95 > mediaPlayer.currentPosition){
            // allow user to exit
            TODO("Allow user to exit the activity")
        }else{
            // prompt user that seession will not count
            TODO("Prompt user that exiting will not count")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        playerReleased = true
    }

    private fun pause(){
        showPausePopup()
        time = mediaPlayer.currentPosition
        mediaPlayer.pause()
        isPaused = true
    }

    private fun resume(){
        mediaPlayer.seekTo(time)
        mediaPlayer.start()
        isPaused = false
        popupWindow.dismiss()
        cd.cancel()
    }

    private fun showPausePopup() {
        try {
            // We need to get the instance of the LayoutInflater
            val inflater: LayoutInflater = LayoutInflater.from(this@MediaActivity)
            getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            val layout = inflater.inflate(R.layout.pause_popup_window, popup_1)
            popupWindow = PopupWindow(layout, 300, 370, true)
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0)
            popupResumeButton =  layout.resumeButton
            popupResumeButton.setOnClickListener { _ ->
                if(isPaused)
                    resume()
            }
            popupCounterTextView = layout.countDownTextView
            cd.start()
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

}
