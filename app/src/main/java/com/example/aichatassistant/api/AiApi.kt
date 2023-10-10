package com.example.aichatassistant.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AiApi {
    @POST("ai-assistant/chat-gpt")
    suspend fun sendMessage(@Body message: Message): Response<AiResponse>

    @POST("api/users")
    suspend fun testApi(@Body test: TestPost): Response<TestResponse>

    @DELETE("ai-assistant/chat-gpt/clear-chat")
    suspend fun deleteHistory(): Response<ClearHist>
}