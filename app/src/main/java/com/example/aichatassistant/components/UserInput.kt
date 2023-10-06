package com.example.aichatassistant.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.aichatassistant.api.AiApi
import com.example.aichatassistant.api.MyInterceptor
import com.example.aichatassistant.api.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBox(messageModel: MessageModel){
    val context = LocalContext.current
    var textMessage by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onTertiary)
    ){
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 10.dp, bottom = 10.dp),
        ){
            TextField(
                value = textMessage,
                onValueChange = { textMessage = it },
                placeholder = {Text(text = "Type your message here")},
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp),
                maxLines = 5,

            )
            IconButton(
                onClick = {
                    if(textMessage.text == ""){
                        Toast.makeText(context, "Add message first!", Toast.LENGTH_SHORT).show()
                    }else{
                        messageModel.addMessage(Message(textMessage.text.trim(),1))
                        println(textMessage.text.trim())
                        postAiMessage(textMessage.text.trim())
                        textMessage = TextFieldValue("", TextRange(0, 0))
                    }
                },
               interactionSource = remember { MutableInteractionSource() },
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send",
                    modifier = Modifier
                        .rotate(-45f)
                        .size(23.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun postAiMessage(message: String){
    val client = OkHttpClient.Builder().apply{
        addInterceptor(MyInterceptor())
    }.build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://ai-assistant.alleasy.dev/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AiApi::class.java)

    GlobalScope.launch(Dispatchers.IO){
        try {
            val response = retrofit.sendMessage(Message(message))
            if (response.isSuccessful && response.body() != null) {
                 println(response.body()!!.aiMessage)
            } else {
                println("Error Code: ${response.code()}")
            }
        } catch (e: Exception) {
             println("An error occurred: ${e.message}")
        }
    }
}