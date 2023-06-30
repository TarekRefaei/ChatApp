package com.tarekrefaei.livechatapp.domain

import com.tarekrefaei.livechatapp.domain.model.Message
import com.tarekrefaei.livechatapp.util.Resource

interface MessageService {

    suspend fun getAllMessages():Resource<List<Message>>

    companion object{
        const val BASE_URL = "http://127.0.0.1:8080/"
    }

    sealed class Endpoints(val url:String){
        object GetAllMessages:Endpoints(url = "$BASE_URL/messages")
    }

}