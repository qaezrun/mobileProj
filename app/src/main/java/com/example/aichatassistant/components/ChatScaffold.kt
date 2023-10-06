package com.example.aichatassistant.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichatassistant.ui.theme.AiChatAssistantTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatAssistant(){
    val message = remember { MessageModel() }
    message.addMessage(Message("What can I help you today?", 0))
    AiChatAssistantTheme {
        Scaffold (
            topBar = {
                topBar()
            },
            bottomBar = {
                ChatBox(message)
            },
            modifier = Modifier.fillMaxSize()
        ){padding ->
            Surface (
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ){
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth()
                ){
                    items(message.getMessageList) { message ->
                        Response(message.from,message.message)
                    }
                }
            }
        }
    }
}


@Composable
fun topBar(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ){
        Row(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(
                text = "CHAT ASSISTANCE",
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }

}
