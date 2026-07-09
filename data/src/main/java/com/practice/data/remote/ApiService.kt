package com.practice.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("suggestTasks")
    suspend fun suggestTasks(
        @Body request: SuggestRequest
    ): SuggestResponse
}

data class SuggestRequest(
    val goal: String,
    val history: List<String> = emptyList()
)

data class SuggestResponse(
    val tasks: List<String>
)
