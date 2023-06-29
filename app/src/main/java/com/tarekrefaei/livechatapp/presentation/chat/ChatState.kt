package com.tarekrefaei.livechatapp.presentation.chat

import com.tarekrefaei.livechatapp.domain.model.Message

data class ChatState(
    val messages : List<Message> = emptyList(),
    val isLoading : Boolean = false,
    val error: String? = null
)
