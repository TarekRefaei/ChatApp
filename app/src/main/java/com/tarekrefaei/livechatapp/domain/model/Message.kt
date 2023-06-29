package com.tarekrefaei.livechatapp.domain.model

import java.util.*

data class Message(
    val username: String,
    val text: String,
    val formattedTime: String,
)
