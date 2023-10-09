package com.example.aichatassistant.api

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class MyInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .build()

        val call = chain
            .withConnectTimeout(10, TimeUnit.MINUTES)
            .withReadTimeout(15,TimeUnit.MINUTES)
            .withWriteTimeout(15,TimeUnit.MINUTES)

        return call.proceed(request)
    }
}