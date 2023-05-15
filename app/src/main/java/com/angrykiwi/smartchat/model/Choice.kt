package com.angrykiwi.smartchat.model

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)