package com.tarekrefaei.livechatapp.data.remote.dto

import com.tarekrefaei.livechatapp.domain.model.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.*

@Serializable
data class MessageDto(
    val id: String,
    val username: String,
    val text: String,
    val timestamp: Long,
) {
    fun toMessage(): Message {
        val date = Date(timestamp)
        val formattedTime = DateFormat
            .getDateInstance(DateFormat.DEFAULT)
            .format(date)
        return Message(
            text = text,
            formattedTime = formattedTime,
            username = username
        )
    }
}
