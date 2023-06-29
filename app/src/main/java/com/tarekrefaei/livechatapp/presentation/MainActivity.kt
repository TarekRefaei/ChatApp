package com.tarekrefaei.livechatapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tarekrefaei.livechatapp.presentation.chat.ChatScreen
import com.tarekrefaei.livechatapp.presentation.login.LoginScreen
import com.tarekrefaei.livechatapp.presentation.ui.theme.LiveChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveChatAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController, startDestination = "login_screen"
                ) {
                    composable(route = "login_screen") {
                        LoginScreen(onNavigate = navController::navigate)
                    }
                    composable(
                        route = "chat_screen/{username}",
                        arguments = listOf(
                            navArgument(name = "username") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ) {
                        val username = it.arguments?.getString("username")
                        ChatScreen(username = username)
                    }
                }
            }
        }
    }
}
