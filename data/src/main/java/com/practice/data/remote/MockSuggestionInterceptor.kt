package com.practice.data.remote

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockSuggestionInterceptor : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!request.url.encodedPath.endsWith("suggestTasks")) {
            return chain.proceed(request)
        }

        val bodyString = request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        }.orEmpty()

        val suggestRequest = runCatching {
            gson.fromJson(bodyString, SuggestRequest::class.java)
        }.getOrDefault(SuggestRequest(goal = "", history = emptyList()))

        val suggestions = SuggestionEngine.generate(
            goal = suggestRequest.goal,
            history = suggestRequest.history
        )
        val responseJson = gson.toJson(SuggestResponse(tasks = suggestions))

        return Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(request)
            .body(responseJson.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
