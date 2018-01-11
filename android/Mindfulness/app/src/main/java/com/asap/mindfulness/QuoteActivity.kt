package com.asap.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.asap.mindfulness.Containers.*
import com.asap.mindfulness.Retrofit.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)
    }

    fun addSurvey(survey: Survey){
        val call = service.postSurvey(survey)

        call.enqueue(object : Callback<Success> {

            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {

                if (response == null) {
                    Log.d("onResponse", "response is null")
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", response.body().toString())
                    return
                }

                // was successfull

            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                Log.d("onResponse", "Error")
            }
        })
    }

    fun completedSurveys(deviceId: String){
        val call = service.getSurvey(deviceId)

        call.enqueue(object : Callback<List<Survey>> {

            override fun onResponse(c: Call<List<Survey>>?, response: Response<List<Survey>>?) {
                if (response == null) {
                    Log.d("onResponse", "response is null")
                    return
                }

                if (response.code() >= 300) {
                    Log.d("onResponse", "response is null")
                    return
                }

                if (response.body() != null) {
                    for (response in response.body()!!) {
                        Log.d("Print out", response.toString())
                    }
                }

            }

            override fun onFailure(c: Call<List<Survey>>?, t: Throwable?) {
                Log.d("onResponse", "error")
            }

        })
    }

    fun getAudioHistory(deviceId: String){
        val call = service.getAudioHistory(deviceId)

        call.enqueue(object: Callback<List<AudioStatus>> {

            override fun onResponse(call: Call<List<AudioStatus>>?, response: Response<List<AudioStatus>>?) {
                if (response == null) {
                    Log.d("onResponse", "response is null")
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", response.body().toString())
                    return
                }

                // successfull
            }

            override fun onFailure(call: Call<List<AudioStatus>>?, t: Throwable?) {
                Log.d("onResponse", "Error")
            }
        })
    }

    // need to fix date json conversion
    fun sendAudioHistory(audio: AudioStatus){
        val call = service.postAudioHistory(audio)

        call.enqueue(object : Callback<Success> {
            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {
                if (response == null) {
                    Log.d("onResponse", "response is null")
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", response.body().toString())
                    return
                }

                // successfull
            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                Log.d("onResponse", "Error")
            }

        })
    }

    // works
    fun checkDevice(deviceId: String) {

        val call = service.isRegistered(deviceId)

        call.enqueue(object : Callback<Exists> {

            override fun onResponse(call: Call<Exists>?, response: Response<Exists>?) {
                if (response == null) {
                    Log.d("onResponse", "response is null")
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", "response is null")
                    return
                }

                Log.d("Print out", response.body().toString())

            }

            override fun onFailure(call: Call<Exists>?, t: Throwable?) {
                Log.d("onResponse", "Error")
            }
        })
    }

    //works but json conversion needs work
    fun resources() {

        val call = service.getResources()

        call.enqueue(object : Callback<List<Resource>> {

            override fun onResponse(c: Call<List<Resource>>?, response: Response<List<Resource>>?) {
                if (response == null) {
                    Log.d("onResponse", "response is null")
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", "response is null")
                    return
                }

                if(response.body() != null) {
                    for (response in response.body()!!) {
                        Log.d("Print out", response.toString())
                    }
                }

            }

            override fun onFailure(c: Call<List<Resource>>?, t: Throwable?) {
                Log.d("onResponse", "error")
            }

        })
    }
}