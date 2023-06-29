package com.tarekrefaei.livechatapp.di

import com.tarekrefaei.livechatapp.data.remote.ChatSocketServiceImpl
import com.tarekrefaei.livechatapp.data.remote.MessagesServiceImpl
import com.tarekrefaei.livechatapp.domain.ChatSocketService
import com.tarekrefaei.livechatapp.domain.MessageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient() : HttpClient {
        return HttpClient(CIO){
            install(Logging)
            install(WebSockets)
            install(ContentNegotiation){
                json()
            }
        }
    }

    @Provides
    @Singleton
    fun provideMessageService(client: HttpClient):MessageService {
        return MessagesServiceImpl(client = client)
    }

    @Provides
    @Singleton
    fun provideChatSocketService(client: HttpClient):ChatSocketService {
        return ChatSocketServiceImpl(client = client)
    }


}