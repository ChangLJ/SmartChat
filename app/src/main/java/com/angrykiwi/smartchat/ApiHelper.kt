package com.angrykiwi.smartchat

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiHelper(context: Context) {
    private var api: IChatGPTApi? = null
    private val timeoutValue = 60L

    init {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(timeoutValue, TimeUnit.SECONDS) // 設置連線Timeout
            .readTimeout(timeoutValue, TimeUnit.SECONDS)
            .writeTimeout(timeoutValue, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.chatgpt_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        api = retrofit.create(IChatGPTApi::class.java)
    }

    fun getApi(): IChatGPTApi? {
        return api
    }
}