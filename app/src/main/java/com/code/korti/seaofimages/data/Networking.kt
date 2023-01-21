package com.code.korti.seaofimages.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object Networking {
    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(AuthInterceptor())
        .addNetworkInterceptor(HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()

    val api: Api
        get() = retrofit.create()
}