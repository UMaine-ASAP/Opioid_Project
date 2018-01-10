package com.asap.mindfulness.Retrofit

import com.asap.mindfulness.Containers.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by jacob on 1/8/18.
 */
interface APIClinet {

    @GET("/resources")
    fun getResources(): Call<List<Resource>>

    @GET("/register_device/{device_id}")
    fun isRegistered(@Path("device_id") deviceId: String): Call<Exists>

    @POST("/audio_history")
    fun postAudioHistory(@Body audioHistory: AudioStatus): Call<Success>

    @GET("/audio_history/{device_id}")
    fun getAudioHistory(@Path("device_id") deviceId: String): Call<List<AudioStatus>>

    @POST("/survey")
    fun postSurvey(@Body survey: Survey): Call<Success>

    @GET("/survey/{device_id}")
    fun getSurvey(@Path("device_id") deviceId: String): Call<List<Survey>>

}