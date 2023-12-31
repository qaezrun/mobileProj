package com.example.aichatassistant.api


suspend fun postAiMessage(message: String): String {
    var aiRes = ""
    val retrofit = RetrofitAiApi.getRetrofit()
    val aiApi = retrofit.create(AiApi::class.java)
    aiRes = try {
        val response = aiApi.sendMessage(Message(message))
        if(response.isSuccessful && response.body() != null) {
            response.body()!!.aiMessage
        } else {
            "Error code: ${response.code()}"
        }
    } catch (e: Exception) {
        "${e.message}: ${e.stackTrace}"
    }
    return aiRes
}

suspend fun deleteHistory():ClearHist{
    val retrofit = RetrofitAiApi.getRetrofit()
    val api = retrofit.create(AiApi::class.java)

    val res: ClearHist = try{
        val response = api.deleteHistory()
        if(response.isSuccessful && response.body() != null) {
            ClearHist(
                status = response.body()!!.status,
                message = response.body()!!.message
            )
        } else {
            ClearHist(
                status = response.code(),
                message = "Failed to delete: ${response.message()}"
            )
        }
    }catch(e:Exception){
        ClearHist(
            status = 10000,
            message = "Exception Err"
        )
    }
    return res
}

suspend fun testPublicApiPost(){
    val retrofit = RetrofitTest.getRetrofit()
    val api = retrofit.create(AiApi::class.java)

    try {
        val response = api.testApi(TestPost("morpheus","leader"))
        if(response.isSuccessful && response.body() != null) {
            println("Response from public api ${response.body()}")
        } else {
            "Error code on test public api: ${response.code()}"
        }
    } catch (e: Exception) {
        "Exception Error on test public api: ${e.stackTrace}"
    }
}