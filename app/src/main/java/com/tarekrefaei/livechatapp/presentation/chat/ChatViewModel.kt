package com.tarekrefaei.livechatapp.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarekrefaei.livechatapp.domain.ChatSocketService
import com.tarekrefaei.livechatapp.domain.MessageService
import com.tarekrefaei.livechatapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun connectToChat() {
        getAllMessages()
        savedStateHandle.get<String>("username")?.let { username ->
            viewModelScope.launch {
                when (val result = chatSocketService.initSession(username = username)) {
                    is Resource.Error -> {
                        _toastEvent.emit(
                            value = result.message ?: "Unknown Error"
                        )
                        _state.value = state.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = true,
                        )
                    }
                    is Resource.Success -> {
                        when (val message = chatSocketService.observeMessage()) {
                            is Resource.Error -> {
                                _toastEvent.emit(
                                    value = result.message ?: "Unknown Error"
                                )
                                _state.value = state.value.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            is Resource.Loading -> {
                                _state.value = state.value.copy(
                                    isLoading = true,
                                )
                            }
                            is Resource.Success -> {
                                message.data?.onEach { newMessage ->
                                    val newList = state.value.messages.toMutableList().apply {
                                        add(0, newMessage)
                                    }
                                    _state.value = state.value.copy(
                                        isLoading = false,
                                        messages = newList
                                    )
                                }?.launchIn(viewModelScope)
                            }
                        }
                    }
                }
            }
        }
    }


    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    fun getAllMessages() {
        viewModelScope.launch {
            when (val result = messageService.getAllMessages()) {
                is Resource.Error -> {
                    _toastEvent.emit(
                        value = result.message ?: "Unknown Error"
                    )
                    _state.value = state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        isLoading = true,
                    )
                }
                is Resource.Success -> {
                    result.data?.let {
                        _state.value = state.value.copy(
                            messages = it,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(message = messageText.value)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}