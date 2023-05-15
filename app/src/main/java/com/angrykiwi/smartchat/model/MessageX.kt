package com.angrykiwi.smartchat.model

data class MessageX(
    var content: String,
    val role: String = "user"
)