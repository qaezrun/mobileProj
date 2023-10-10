package com.example.aichatassistant.components

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Message(
    val message:String,
    val from: Int,
    var sentStat: Boolean
)

class MessageModel: ViewModel() {
    private val messageList = mutableStateListOf<Message>()
    fun addMessage(message: Message) {
        messageList.add(message)
    }
    val getMessageList: List<Message>
        get() = messageList
    fun toggleSentStat(messageIndex: Int) {
        messageList[messageIndex].sentStat = false
    }
    fun clearRecentMessages(){
        messageList.clear()
    }
}