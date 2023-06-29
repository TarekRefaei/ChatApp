package com.tarekrefaei.livechatapp.data.remote

import com.tarekrefaei.livechatapp.data.remote.dto.MessageDto
import com.tarekrefaei.livechatapp.domain.ChatSocketService
import com.tarekrefaei.livechatapp.domain.model.Message
import com.tarekrefaei.livechatapp.util.Resource
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocket.url}?username = $username")
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Couldn't established connection")
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun sendMessage(message: String): Resource<Unit> {
        return try {
            socket?.send(
                Frame.Text(message)
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    override fun observeMessage(): Resource<Flow<Message>> {
        return try {
            val messageFlow = socket
                ?.incoming
                ?.receiveAsFlow()
                ?.filter {
                    it is Frame.Text
                }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow { }
            Resource.Success(messageFlow)
        } catch (e: Exception) {
            Resource.Error(message = e.message.toString())
        }
    }

    override suspend fun closeSession(): Resource<Unit> {
        return if (socket?.isActive == true) {
            socket?.close()
            Resource.Success(Unit)
        } else {
            Resource.Error(message = "Something wrong happen socket isn't close")
        }
    }

}