package com.angrykiwi.smartchat

import com.angrykiwi.smartchat.model.ChatGPTRequest
import com.angrykiwi.smartchat.model.ChatGPTResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface IChatGPTApi {
    @POST("completions")
    fun completions(
        @Header(value = "Authorization") auth: String,
        @Body body: ChatGPTRequest?
    ): Call<ChatGPTResponse?>?
}