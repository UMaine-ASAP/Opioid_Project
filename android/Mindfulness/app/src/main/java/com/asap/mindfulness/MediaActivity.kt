package com.asap.mindfulness
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.asap.mindfulness.Containers.AudioStatus
import com.asap.mindfulness.Containers.Success
import com.asap.mindfulness.Retrofit.*
import kotlinx.android.synthetic.main.activity_media.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


import java.util.concurrent.TimeUnit.MILLISECONDS as TUM
import android.content.Intent
import android.content.SharedPreferences
import android.view.MenuItem
import com.asap.mindfulness.Containers.Track
import com.asap.mindfulness.Containers.CompletionHandler
import com.asap.mindfulness.Fragments.RatingFragment
import com.asap.mindfulness.SQLite.SQLManager
import android.support.v4.app.NavUtils
import com.asap.mindfulness.Fragments.CountdownDialogFragment


class MediaActivity : AppCompatActivity(), CompletionHandler {




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

    override fun complete() {
        val navIntent = NavUtils.getParentActivityIntent(this)
        navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        NavUtils.navigateUpTo(this, navIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        mPrefs = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        mTrack = intent.getParcelableExtra(TRACK_INTENT)

        audioSource = mTrack.path
        audioIndex = intent.getIntExtra(INDEX_INTENT, 0)

        deviceId = mPrefs.getString(getString(R.string.sp_study_id), "None")


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
        Log.d("SQL Debug","Back button pressed")
        goBack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        goBack()
        return true
    }

    fun goBack(){
        mediaPlayer.pause()
        if(mediaPlayer.duration * 0.9 < mediaPlayer.currentPosition){
            // allow user to exit

            val audioStatus = AudioStatus(deviceId, mTrack.index, true, Calendar.getInstance().getTime())
            sendAudioHistory(audioStatus)
            time = mediaPlayer.currentPosition
            mediaPlayer.pause()
            isPaused = true

            openRatingFrag()

        }else{
            // prompt user that seession will not count
            val audioStatus = AudioStatus(deviceId, mTrack.index, false, Calendar.getInstance().getTime())
            sendAudioHistory(audioStatus)

           openRatingFrag()
        }
    }

    fun openRatingFrag(){
        val fm = supportFragmentManager
        val editNameDialogFragment = RatingFragment.newInstance(mTrack.index, this, this)
        editNameDialogFragment.show(fm,"dialog")
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
    }

    private fun showPausePopup() {
//        try {
//            // We need to get the instance of the LayoutInflater
//            val inflater: LayoutInflater = LayoutInflater.from(this@MediaActivity)
//            getSystemService(Context.LAYOUT_INFLATER_SERVICE)
//            val layout = inflater.inflate(R.layout.fragment_countdown_dialog, popup_1)
//            ViewCompat.setElevation(layout, 8f)
//            popupWindow = PopupWindow(layout, 600, 600, true)
//            popupWindow.setOnDismissListener {
//                if(isPaused)
//                    resume()
//            }
//            popupWindow.isOutsideTouchable = true
//            popupWindow.isFocusable = true
//            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0)
//            popupResumeButton =  layout.resumeButton
//            popupResumeButton.setOnClickListener { _ ->
//                if(isPaused)
//                    resume()
//            }
//            popupCounterTextView = layout.textCountDown
//            cd.start()
//        } catch (e:Exception) {
//            e.printStackTrace()
//        }

        val fragTransaction = fragmentManager.beginTransaction()
        val prevFrag = fragmentManager.findFragmentByTag("media_countdown")
        if (prevFrag != null) {
            fragTransaction.remove(prevFrag)
        }
        fragTransaction.addToBackStack(null)

        CountdownDialogFragment.newInstance(object: CountdownDialogFragment.CountdownListener {
            override fun onDismissed() {
                if(isPaused)
                    resume()
            }

            override fun onCountdownFinished() {
                val audioStatus = AudioStatus(deviceId, mTrack.index, true, Calendar.getInstance().getTime())
                sendAudioHistory(audioStatus)

                complete()
            }
        }).show(fragTransaction, "media_countdown")
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

        if (audio.completion_status) {
            val sessions = mPrefs.getInt(getString(R.string.sp_tracks_completed), 0)
            with (mPrefs.edit()) {
                putInt(getString(R.string.sp_tracks_completed), sessions + 1)
                apply()
            }
        }

        Log.d(" Note ", "Adding audio")

        val db = SQLManager(this)
        db.registerDatabase("Updatables")
        Log.d("SQL Debug", "Adding a row to table")
        db.insertRow("Updatables", "Audio_History", "track_number, completion_status, creation_date, server_pushed",
                String.format("%d,%b,%d,%d",
                        audio.track_number,
                        audio.completion_status,
                        audio.creation_date.time,
                        0
                )
        )
    }

}
