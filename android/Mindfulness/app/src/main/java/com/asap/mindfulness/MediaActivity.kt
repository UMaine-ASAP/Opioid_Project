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
import android.content.SharedPreferences
import android.provider.Settings
import android.view.MenuItem
import com.asap.mindfulness.Containers.Track
import com.asap.mindfulness.Fragments.RatingFragment
import com.asap.mindfulness.SQLite.SQLManager


class MediaActivity : AppCompatActivity() {


    lateinit var mPrefs: SharedPreferences

    companion object {
        const val TRACK_INTENT = "track"
        const val INDEX_INTENT = "index"
    }

    private var deviceId = ""

    private lateinit var mTrack: Track

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

        mPrefs = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        mTrack = intent.getParcelableExtra(TRACK_INTENT)

        audioSource = mTrack.path
        audioIndex = intent.getIntExtra(INDEX_INTENT, 0)

        deviceId = mPrefs.getString(getString(R.string.sp_name), "None")


        //val fm = supportFragmentManager
//        val editNameDialogFragment = RatingFragment.newInstance()
//        editNameDialogFragment.show(fm,"dialog")

        //creating mediaplayer and starting the audio
        mediaPlayer = MediaPlayer.create(this, audioSource)

        mediaPlayer.start()

        mediaPlayer.setOnCompletionListener {
            // Launch quote page in Media mode
            val quoteIntent = Intent(baseContext, QuoteActivity::class.java)
            quoteIntent.putExtra(QuoteActivity.MODE, QuoteActivity.MODE_MEDIA)
            startActivity(quoteIntent)
        }

        playerReleased = false

        textTitle.text = mTrack.title
        textTime.post(mUpdateTime)

        pauseButton.setOnClickListener { _ ->
            if(!isPaused)
                pause()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("SQL Debug","Back button pressed")
        goBack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        goBack()
        //finish()
        return true
    }

    fun goBack(){
        if(mediaPlayer.duration * 0.1 > mediaPlayer.currentPosition){
            // allow user to exit

            val audioStatus = AudioStatus(deviceId, audioIndex, true, Calendar.getInstance().getTime())
            sendAudioHistory(audioStatus)

            //val myIntent = Intent(applicationContext, ParentActivity::class.java)
            //startActivityForResult(myIntent, 0)

            val fm = supportFragmentManager
            val editNameDialogFragment = RatingFragment.newInstance()
            editNameDialogFragment.show(fm,"dialog")

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
                if (response == null || response.code() >= 300 || response.body()?.error == true) {
                    addAudioToDatabase(audio)
                    return
                }
                //successful
            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                Log.d("onResponse", "Error")
                addAudioToDatabase(audio)
            }

        })
    }

    fun addAudioToDatabase(audio: AudioStatus) {
        val db = SQLManager(this)
        db.registerDatabase("Updatables")
        Log.d("SQL Debug", "Adding a row to table")
        db.insertRow("Updatables", "Audio_History", "track_number, completion_status, creation_date",
                String.format("%d,%b,%d",
                        audio.track_number,
                        audio.completion_status,
                        audio.creation_date.time
                )
        )
    }

}
