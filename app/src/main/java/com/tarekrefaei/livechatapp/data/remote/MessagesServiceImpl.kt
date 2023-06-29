package com.tarekrefaei.livechatapp.data.remote

import com.tarekrefaei.livechatapp.data.remote.dto.MessageDto
import com.tarekrefaei.livechatapp.domain.MessageService
import com.tarekrefaei.livechatapp.domain.model.Message
import com.tarekrefaei.livechatapp.util.Resource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MessagesServiceImpl(
    private val client: HttpClient
) : MessageService {


    override suspend fun getAllMessages(): Resource<List<Message>> {
        return try {
            val response: List<MessageDto> =
                client.get(urlString = MessageService.Endpoints.GetAllMessages.url).body()
            Resource.Success(response.map {
                it.toMessage()
            })
        } catch (e: Exception) {
            return Resource.Error(
                message = e.message.toString()
            )
        }
    }


}