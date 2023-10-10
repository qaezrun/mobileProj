package com.example.aichatassistant.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/*
    Ben's API: "https://ai-assistant.alleasy.dev/"
    Test API: https://reqres.in/
*/
object RetrofitAiApi {
    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .baseUrl("https://ai-assistant.alleasy.dev/")
            .client(OkHttpClient.Builder().addInterceptor(MyInterceptor()).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getRetrofit(): Retrofit {
        return retrofitBuilder
    }
}

object RetrofitTest{
    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .baseUrl("https://reqres.in/")
            .client(OkHttpClient.Builder().addInterceptor(MyInterceptor()).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getRetrofit(): Retrofit {
        return retrofitBuilder
    }
}