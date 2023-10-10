package com.example.aichatassistant.api

data class Message(
    val message: String,
)
data class AiResponse(
    val aiMessage: String
)

data class TestPost(
    val name: String,
    val job: String
)
data class TestResponse(
    val name: String,
    val job: String,
    val id: String,
    val createdAt:String
)
data class ClearHist(
    val status: Int,
    val message: String
)