package com.tarekrefaei.livechatapp.domain

import com.tarekrefaei.livechatapp.domain.model.Message
import com.tarekrefaei.livechatapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        username: String
    ): Resource<Unit>

    suspend fun sendMessage(message: String): Resource<Unit>

    fun observeMessage(): Resource<Flow<Message>>

    suspend fun closeSession() : Resource<Unit>

    companion object{
        const val BASE_URL = "ws://192.168.1.3/8082"
    }

    sealed class Endpoints(val url:String){
        object ChatSocket: Endpoints(url = "$BASE_URL/chat-socket")
    }

}