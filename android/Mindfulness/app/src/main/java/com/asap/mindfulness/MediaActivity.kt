package com.asap.mindfulness

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.asap.mindfulness.Containers.AudioStatus
import com.asap.mindfulness.Containers.Success
import com.asap.mindfulness.Retrofit.*
import kotlinx.android.synthetic.main.activity_media.*
import kotlinx.android.synthetic.main.pause_popup_window.*
import kotlinx.android.synthetic.main.pause_popup_window.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


import java.util.concurrent.TimeUnit.MILLISECONDS as TUM
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.MenuItem


class MediaActivity : AppCompatActivity() {

    private var deviceId = ""

    // source of the audio to be played
    private var audioSource = R.raw.track1
    private var audioIndex = 0

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
                // Changed over to using placeholders (uses String.format() formatting style)
                textTime.text = getString(R.string.media_timer,
                        TUM.toMinutes(mediaPlayer.currentPosition.toLong()),
                        TUM.toSeconds(mediaPlayer.currentPosition.toLong()) % 60,
                        TUM.toMinutes(mediaPlayer.duration.toLong()),
                        TUM.toSeconds(mediaPlayer.duration.toLong()) % 60)
                textTime.postDelayed(this, 1000)
            }
        }
    }

    // counter for the media player
    var cd: CountDownTimer = object: CountDownTimer(300000, 1000) {
        override fun onTick(millisUntilFinished:Long) {
            popupCounterTextView.text = getString(R.string.media_popup_timer,
                    TUM.toMinutes(millisUntilFinished),
                    TUM.toSeconds(millisUntilFinished) % 60)

        }
        override fun onFinish() {
            val audioStatus = AudioStatus(deviceId, audioIndex, true, Calendar.getInstance().getTime())
            sendAudioHistory(audioStatus)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        Log.d("DeviceID", deviceId)

        audioSource = intent.getIntExtra("path", R.raw.track1)
        audioIndex = intent.getIntExtra("index", 0)


        //creating mediaplayer and starting the audio
        mediaPlayer = MediaPlayer.create(this, audioSource)
        mediaPlayer.start()
        playerReleased = false

        textTitle.text = intent.getStringExtra("title")
        textTime.post(mUpdateTime)

        pauseButton.setOnClickListener { _ ->
            if(!isPaused)
                pause()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goBack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        goBack()
        finish()
        return true
    }

    fun goBack(){
        if(mediaPlayer.duration * 0.95 > mediaPlayer.currentPosition){
            // allow user to exit

            val audioStatus = AudioStatus(deviceId, audioIndex, true, Calendar.getInstance().getTime())
            sendAudioHistory(audioStatus)

            val myIntent = Intent(applicationContext, ParentActivity::class.java)
            startActivityForResult(myIntent, 0)

        }else{
            // prompt user that seession will not count
            val audioStatus = AudioStatus(deviceId, audioIndex, false, Calendar.getInstance().getTime())
            sendAudioHistory(audioStatus)
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
            popupCounterTextView = layout.textCountDown
            cd.start()
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun sendAudioHistory(audio: AudioStatus){
        val call = service.postAudioHistory(audio)

        call.enqueue(object : Callback<Success> {
            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {
                if (response == null) {
                    Log.d("onResponse", "response is null")
                    //TODO("save audio data locally")
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", response.body().toString())
                    //TODO("save audio data locally")
                    return
                }

                if(response.body()?.error == true) {
                    //TODO("save audio data locally")
                }

            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                Log.d("onResponse", "Error")
                //TODO("save audio data locally")
            }

        })
    }

}
