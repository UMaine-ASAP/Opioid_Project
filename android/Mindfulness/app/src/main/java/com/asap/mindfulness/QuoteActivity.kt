package com.asap.mindfulness

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import com.asap.mindfulness.Containers.*
import com.asap.mindfulness.Retrofit.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View
import android.content.SharedPreferences
import android.support.v4.app.NavUtils
import com.asap.mindfulness.Notifications.QuoteNotification
import com.asap.mindfulness.SQLite.DatabaseClass
import com.asap.mindfulness.SQLite.SQLManager
import com.transitionseverywhere.*
import kotlinx.android.synthetic.main.activity_quote.*
import java.util.*
import kotlin.collections.ArrayList as KotlinList


class QuoteActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val MODE = "mode"
        const val MODE_LOADING = 0
        const val MODE_MEDIA = 1
        const val MODE_BROWSER = 2
    }

    var isDoneSending = false

    lateinit var mPrefs: SharedPreferences

    var deviceId = ""

    var mode: Int = 0

    lateinit var quotesList: Array<String>
    lateinit var quotesListCredits: Array<String>
    val quotesUsed = KotlinList<Int>()

    override fun onClick(view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        mode = intent.getIntExtra(MODE, 0)

        mPrefs = getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        deviceId = mPrefs.getString(getString(R.string.sp_study_id), "None")

        refreshFields()
        setup() //-----------//>

         Log.d("SQL Debug", "Mode: " + mode.toString())

                              //|
        if (mode == 0) {      //|
            updateServer()    //|
            done()            //|
        }                     //|
    }                         //|
                              //|
    fun setup() {  //<-------//<

        quotesList = resources.getStringArray(R.array.quotes_array)
        quotesListCredits = resources.getStringArray(R.array.quotes_credits)

        // Load in the quote depending on the current MODE
        val quoteNum = when (mode) {
            MODE_LOADING -> {
                quoteProgressBar.visibility = View.VISIBLE
                val daysPassed = mPrefs.getInt(getString(R.string.sp_days_passed), 1)
                if (daysPassed == 1) {
                    52
                } else {
                    Random().nextInt(quotesList.size)
                }
            }
            MODE_BROWSER -> {
                close_quotes.visibility = View.VISIBLE
                close_quotes.setOnClickListener { _ ->
                    NavUtils.navigateUpFromSameTask(this)
                }

                grabNewQuote()
            }
            else -> {
                Random().nextInt(quotesList.size)
            }
        }
        quoteTextView.text = quotesList[quoteNum]
        quoteCredits.text = quotesListCredits[quoteNum]

        //TODO("use mod to update the quote. day%count")

        //val images = resources.obtainTypedArray(R.array.image_array)
        //quoteImageView.setImageResource(images.getResourceId(0,0))
        //quoteImageView.setImageResource(R.drawable.wood_grain)
        //TODO("use mod to update the image")

        view_quote.setOnClickListener(when(mode) {
            MODE_LOADING -> { _: View? ->
                if(isDoneSending) {
                    val intent = Intent(baseContext, ParentActivity::class.java)
                    startActivity(intent)
                }
            }
            MODE_MEDIA -> { p0: View? ->
                NavUtils.navigateUpFromSameTask(this)
            }
            else -> { _: View? ->
                if (quotesUsed.size == quotesList.size) {
                    NavUtils.navigateUpFromSameTask(this)
                } else {
                    TransitionManager.beginDelayedTransition(view_quote)
                    val quoteNumber = grabNewQuote()
                    quoteTextView.text = quotesList[quoteNumber]
                    quoteCredits.text = quotesListCredits[quoteNumber]
                }
            }
        })
    }

    fun grabNewQuote() : Int {
        var nextQuote: Int

        do {
            nextQuote = Random().nextInt(quotesList.size)
        } while (nextQuote in quotesUsed)

        quotesUsed.add(nextQuote)

        return nextQuote
    }

    fun refreshFields() {

        QuoteNotification.scheduleNotifications(this)

        val prefsEditor = mPrefs.edit()

        // Get days passed since start date
        val startDate = mPrefs.getLong(getString(R.string.sp_start_date), 0)
//        val daysPassed: Int = ((Date().time - startDate) / 1000 / 60 / 60 / 24 + 1).toInt()
        val daysPassed = daysBetween(Date(startDate), Date()) + 1
        prefsEditor.putInt(getString(R.string.sp_days_passed), daysPassed)

        val weekNum = daysPassed / 7

        val currentTrack = when (weekNum) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            5 -> 5
            else -> 5
        }
        prefsEditor.putInt(getString(R.string.sp_tracks_current), currentTrack)

        val currentSurvey = when {
            // weekNum == 0 -> TODO: Enrollment survey here
            // weekNum == 8 -> TODO: Final survey here
            weekNum > 8 -> "No survey to take!"
            else -> "https://survey.emhs.org/TakeSurvey.aspx?SurveyID=92KLl682M"
        }

        prefsEditor.putString(getString(R.string.sp_last_survey_link), currentSurvey)
        // Set the survey assignment date at the start of the week, relative to when the user first
        // enrolled
        prefsEditor.putLong(
                getString(R.string.sp_last_survey_date),
                if (currentSurvey == "No Survey to take!") {
                    -1L
                } else {
                    weekNum * 7 + startDate
                })

        prefsEditor.apply()
    }

    fun updateServer() {
        // check local sql database to see if needs to update server
        // call addSurvey for each survey in loacl db
        // call addAudioHistory for each audio track in local db
        //TODO("Intergrate local SQL LITE DB")

        //val deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val db = DatabaseClass(this, "Updatables").readableDatabase
        var cursor = db.query(true, "Audio_History", arrayOf("track_number, completion_status, creation_date, server_pushed, ID"), null, null, null, null, null, null)

        Log.d("SQL Debug", "Checking the db")

        if (cursor != null ) {
            Log.d("SQL Debug", "# of rows = " + cursor.count)
            if(cursor.count > 0) {
                while (!cursor.isLast) {
                    cursor.moveToNext()
                    var completion = false

                    if(cursor.getInt(1) > 0){
                        completion = true
                    }

                    var date: Date = Date()

                    date.time = cursor.getLong(2)

                    Log.d(" Note ", "Getting audio")

                    if(cursor.getInt(3) == 0) {

                        sendAudioHistory(AudioStatus(deviceId, cursor.getInt(0), completion, date), cursor.getInt(4))

                    }
                }

            }

        }

        cursor.close()

        val cursor2 = db.query(true, "Survey_History", arrayOf("resource_id, rating, creation_date, server_pushed, ID"), null, null, null, null, null, null)

        if(cursor2 != null){
            if(cursor2.count > 0){
                while(!cursor2.isLast){
                    cursor2.moveToNext()

                    var date: Date = Date()

                    date.time = cursor2.getLong(2)

                    Log.d(" Note ", "Getting survey")

                    if(cursor2.getInt(3) == 0) {

                        addSurvey(Survey(deviceId, cursor2.getInt(0), cursor2.getInt(1), date), cursor2.getInt(4))

                    }

                }
            }
        }

        cursor2.close()

        done()
    }

    fun done(){
        isDoneSending = true
        quoteProgressBar.visibility = View.INVISIBLE
        continueTextView.visibility = View.VISIBLE
    }

    fun addSurvey(survey: Survey, id: Int){
        val call = service.postSurvey(survey)

        call.enqueue(object : Callback<Success> {

            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {

                if (response == null || response.code() >= 300 || response.body()?.error == true){
                    done()
                    return
                }

                val db = SQLManager(this@QuoteActivity)
                db.registerDatabase("Updatables")

                val col = Array<String>(1, {"server_pushed"})
                val vals = Array<String>(1, {"1"})

                db.updateTable("Updatables","Survey_History", col, vals, "ID=" + id, null)


                done()
                // was successfull

            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                done()
                Log.d("onResponse", "Error")
            }
        })
    }

    fun sendAudioHistory(audio: AudioStatus, id: Int){

        val call = service.postAudioHistory(audio)

        call.enqueue(object : Callback<Success> {
            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {
                if (response == null || response.code() >= 300 || response.body()?.error == true) {
                    done()
                    return
                }

                val db = SQLManager(this@QuoteActivity)
                db.registerDatabase("Updatables")

                val col = Array<String>(1, {"server_pushed"})
                val vals = Array<String>(1, {"1"})

                db.updateTable("Updatables","Audio_History", col, vals, "ID=" + id, null)
                done()
                //successful
            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                done()
                Log.d("onResponse", "Error")
            }

        })
    }



    fun daysBetween(startDate: Date, endDate: Date): Int {

        fun getDatePart(date:Date):Calendar {
            val cal = Calendar.getInstance() // get calendar instance
            cal.setTime(date)
            cal.set(Calendar.HOUR_OF_DAY, 0) // set hour to midnight
            cal.set(Calendar.MINUTE, 0) // set minute in hour
            cal.set(Calendar.SECOND, 0) // set second in minute
            cal.set(Calendar.MILLISECOND, 0) // set millisecond in second
            return cal // return the date part
        }

        val sDate = getDatePart(startDate)
        val eDate = getDatePart(endDate)

        var daysBetween = 0
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1)
            daysBetween++
        }
        return daysBetween
    }
}
