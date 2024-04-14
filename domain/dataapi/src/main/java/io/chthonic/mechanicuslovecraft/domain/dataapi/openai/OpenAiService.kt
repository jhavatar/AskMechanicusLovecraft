package io.chthonic.mechanicuslovecraft.domain.dataapi.openai

import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessage
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.ChatResponseStreamChunk
import kotlinx.coroutines.flow.Flow

interface OpenAiService {
    suspend fun testChatResponse()
    suspend fun testStreamChatResponse()

    fun observeStreamingResponseToChat(
        messageHistory: List<ChatMessage>,
        systemMetaInfo: String? = null
    ): Flow<ChatResponseStreamChunk>
}