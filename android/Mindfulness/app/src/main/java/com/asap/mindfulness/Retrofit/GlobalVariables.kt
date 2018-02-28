package com.asap.mindfulness.Retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.security.cert.CertificateException


val url = "https://emac.asap.um.maine.edu:1337"

val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()


val service = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(APIClinet::class.java)



//var service = NetworkHandler.getRetrofit().create<APIClinet>(APIClinet::class.java!!)