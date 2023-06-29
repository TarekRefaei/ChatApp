package com.tarekrefaei.livechatapp.presentation.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import com.tarekrefaei.livechatapp.domain.model.Message
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChatScreen(
    username: String?,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        chatViewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                chatViewModel.connectToChat()
            } else if (event == Lifecycle.Event.ON_STOP) {
                chatViewModel.disconnect()
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state = chatViewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true,
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .height(32.dp)
                )
            }
            items(state.messages) { message ->
                val isOwnUsername = message.username == username
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isOwnUsername) {
                        Alignment.CenterStart
                    } else {
                        Alignment.CenterEnd
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .width(200.dp)
                            .drawBehind {
                                val cornerRadius = 10.dp.toPx()
                                val triangleHeight = 20.dp.toPx()
                                val triangleWidth = 25.dp.toPx()
                                val trianglePath = Path().apply {
                                    if (isOwnUsername) {
                                        moveTo(x = size.width, y = size.height - cornerRadius)
                                        lineTo(x = size.width, y = size.height + triangleHeight)
                                        lineTo(
                                            x = size.width - triangleWidth,
                                            y = size.height - cornerRadius
                                        )
                                        close()
                                    } else {
                                        moveTo(x = 0f, y = size.height - cornerRadius)
                                        lineTo(x = 0f, y = size.height + triangleHeight)
                                        lineTo(
                                            x = triangleWidth,
                                            y = size.height - cornerRadius
                                        )
                                        close()
                                    }
                                }
                                drawPath(
                                    path = trianglePath,
                                    color = if (isOwnUsername) Color.Green else Color.Black
                                )
                            }
                            .background(
                                color = if (isOwnUsername) Color.Green else Color.Black,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = message.username,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = message.text,
                            color = Color.White
                        )
                        Text(
                            text = message.formattedTime,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = chatViewModel.messageText.value,
                onValueChange = chatViewModel::onMessageChange,
                placeholder = {
                    Text(text = "Enter Your Message")
                },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = chatViewModel::sendMessage
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }

}