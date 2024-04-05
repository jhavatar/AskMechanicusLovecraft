package io.chthonic.mechanicuslovecraft.domain.dataapi.openai

import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.ChatMessageChunk
import kotlinx.coroutines.flow.Flow

interface OpenAiService {
    suspend fun testChatResponse()
    suspend fun testStreamChatResponse()

    fun observeStreamingChatResponseToUserMessage(
        userMessage: String,
        systemMetaInfo: String? = null
    ): Flow<ChatMessageChunk>
}