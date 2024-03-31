package io.chthonic.mechanicuslovecraft.data.openai.rest

import io.chthonic.mechanicuslovecraft.data.openai.rest.models.ChatRequest
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface OpenAiApi {

    @POST("chat/completions")
    suspend fun getChatResponse(@Body chatRequest: ChatRequest): ChatResponse
}