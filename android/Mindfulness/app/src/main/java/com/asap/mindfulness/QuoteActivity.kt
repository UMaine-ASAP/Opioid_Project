package com.asap.mindfulness

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
import android.support.v4.app.NavUtils
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

        mPrefs = this.getSharedPreferences(getString(R.string.sp_file_key), android.content.Context.MODE_PRIVATE)

        refreshFields()
        setup() //-----------//>
                              //|
        if (mode == 1) {      //|
            updateServer()    //|
        } else {              //|
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
                val startDate = Date()
                startDate.time = mPrefs.getLong("StartDate", 0)
                val numOfDays = daysBetween(startDate, Date())

//                numOfDays % quotesList.size
                0
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

        val currentTrack = daysPassed / 7 + 1
        prefsEditor.putInt(getString(R.string.sp_current_track), currentTrack)

        prefsEditor.apply()
    }

    fun updateServer() {
        // check local sql database to see if needs to update server
        // call addSurvey for each survey in loacl db
        // call addAudioHistory for each audio track in local db
        //TODO("Intergrate local SQL LITE DB")

        if(false){ // if sql has data

            // call retrofit functions to update data


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

                if (response == null) {
                    Log.d("onResponse", "response is null")
                    done()
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", response.body().toString())
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
                if (response == null) {
                    done()
                    Log.d("onResponse", "response is null")
                    return
                }

                if(response.code() >= 300){
                    done()
                    Log.d("onResponse", response.body().toString())
                    return
                }

                done()
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
