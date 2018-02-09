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
//import sun.plugin2.util.PojoUtil.toJson
import android.content.SharedPreferences
import android.provider.Settings
import android.support.v4.app.NavUtils
import com.asap.mindfulness.SQLite.DatabaseClass
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

        deviceId = mPrefs.getString(getString(R.string.sp_name), "None")

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
                val daysPassed = mPrefs.getInt(getString(R.string.sp_days_passed), 0)
                if (daysPassed == 0) {
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
        var nextQuote = -1

        do {
            nextQuote = Random().nextInt(quotesList.size)
        } while (nextQuote in quotesUsed)

        quotesUsed.add(nextQuote)

        return nextQuote
    }

    fun refreshFields() {
        val prefsEditor = mPrefs.edit()

        // Get days passed since start date
        val startDate = mPrefs.getLong(getString(R.string.sp_start_date), 0)
//        val daysPassed: Int = ((Date().time - startDate) / 1000 / 60 / 60 / 24 + 1).toInt()
        val daysPassed = daysBetween(Date(startDate), Date())
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
        prefsEditor.putInt(getString(R.string.sp_current_track), currentTrack)

        val currentSurvey = when (weekNum) {
            // 0 -> TODO: Enrollment survey here
            // 8 -> TODO: Final survey here
            else -> "https://survey.emhs.org/TakeSurvey.aspx?SurveyID=92KLl682M"
        }
        prefsEditor.putString(getString(R.string.sp_last_survey_link), currentSurvey)
        // Set the survey assignment date at the start of the week, relative to when the user first
        // enrolled
        prefsEditor.putLong(getString(R.string.sp_last_survey_date), weekNum * 7 + startDate)

        prefsEditor.apply()
    }

    fun updateServer() {
        // check local sql database to see if needs to update server
        // call addSurvey for each survey in loacl db
        // call addAudioHistory for each audio track in local db
        //TODO("Intergrate local SQL LITE DB")

        //val deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val db = DatabaseClass(this, "Updatables").readableDatabase
        var cursor = db.query(true, "Audio_History", arrayOf("track_number, completion_status, creation_date"), null, null, null, null, null, null)

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

                    sendAudioHistory(AudioStatus(deviceId,cursor.getInt(0), completion, date))
                }

            }

        }

        cursor.close()

        cursor = db.query(true, "Survey_History", arrayOf("resource_id, rating, creation_date"), null, null, null, null, null, null)

        if(cursor != null){
            if(cursor.count > 0){
                while(!cursor.isLast){
                    cursor.moveToNext()

                    var date: Date = Date()

                    date.time = cursor.getLong(2)

                    addSurvey(Survey(deviceId, cursor.getInt(0), cursor.getInt(1), date))

                }
            }
        }

        done()
    }

    fun done(){
        isDoneSending = true
        quoteProgressBar.visibility = View.INVISIBLE
        continueTextView.visibility = View.VISIBLE
    }

    fun addSurvey(survey: Survey){
        val call = service.postSurvey(survey)

        call.enqueue(object : Callback<Success> {

            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {

                if (response == null || response.code() >= 300 || response.body()?.error == true){
                    done()
                    return
                }

                done()
                // was successfull

            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                done()
                Log.d("onResponse", "Error")
            }
        })
    }

    fun sendAudioHistory(audio: AudioStatus){

        val call = service.postAudioHistory(audio)

        call.enqueue(object : Callback<Success> {
            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {
                if (response == null || response.code() >= 300 || response.body()?.error == true) {
                    done()
                    return
                }

                done()
                //successful
            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                done()
                Log.d("onResponse", "Error")
            }

        })
    }







//
//    fun completedSurveys(deviceId: String){
//        val call = service.getSurvey(deviceId)
//
//        call.enqueue(object : Callback<List<Survey>> {
//
//            override fun onResponse(c: Call<List<Survey>>?, response: Response<List<Survey>>?) {
//                if (response == null) {
//                    Log.d("onResponse", "response is null")
//                    return
//                }
//
//                if (response.code() >= 300) {
//                    Log.d("onResponse", "response is null")
//                    return
//                }
//
//                if (response.body() != null) {
//                    for (response in response.body()!!) {
//                        Log.d("Print out", response.toString())
//                    }
//                }
//
//            }
//
//            override fun onFailure(c: Call<List<Survey>>?, t: Throwable?) {
//                Log.d("onResponse", "error")
//            }
//
//        })
//    }
//
//    fun getAudioHistory(deviceId: String){
//        val call = service.getAudioHistory(deviceId)
//
//        call.enqueue(object: Callback<List<AudioStatus>> {
//
//            override fun onResponse(call: Call<List<AudioStatus>>?, response: Response<List<AudioStatus>>?) {
//                if (response == null) {
//                    Log.d("onResponse", "response is null")
//                    return
//                }
//
//                if(response.code() >= 300){
//                    Log.d("onResponse", response.body().toString())
//                    return
//                }
//
//                // successfull
//            }
//
//            override fun onFailure(call: Call<List<AudioStatus>>?, t: Throwable?) {
//                Log.d("onResponse", "Error")
//            }
//        })
//    }
//
//    // need to fix date json conversion
//
//    // works
//    fun checkDevice(deviceId: String) {
//
//        val call = service.isRegistered(deviceId)
//
//        call.enqueue(object : Callback<Exists> {
//
//            override fun onResponse(call: Call<Exists>?, response: Response<Exists>?) {
//                if (response == null) {
//                    Log.d("onResponse", "response is null")
//                    return
//                }
//
//                if(response.code() >= 300){
//                    Log.d("onResponse", "response is null")
//                    return
//                }
//
//                Log.d("Print out", response.body().toString())
//
//            }
//
//            override fun onFailure(call: Call<Exists>?, t: Throwable?) {
//                Log.d("onResponse", "Error")
//            }
//        })
//    }
//
//    //works but json conversion needs work
//    fun resources() {
//
//        val call = service.getResources()
//
//        call.enqueue(object : Callback<List<Resource>> {
//
//            override fun onResponse(c: Call<List<Resource>>?, response: Response<List<Resource>>?) {
//                if (response == null) {
//                    Log.d("onResponse", "response is null")
//                    return
//                }
//
//                if(response.code() >= 300){
//                    Log.d("onResponse", "response is null")
//                    return
//                }
//
//                if(response.body() != null) {
//                    for (response in response.body()!!) {
//                        Log.d("Print out", response.toString())
//                    }
//                }
//
//            }
//
//            override fun onFailure(c: Call<List<Resource>>?, t: Throwable?) {
//                Log.d("onResponse", "error")
//            }
//
//        })
//    }



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
