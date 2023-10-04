package com.example.aichatassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aichatassistant.components.AiChatAssistant
import com.example.aichatassistant.components.MessageModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiChatAssistant()
        }
    }
}
