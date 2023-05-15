package com.angrykiwi.smartchat.model

data class ChatGPTRequest(
    var messages: List<MessageX>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Double = 0.7
)