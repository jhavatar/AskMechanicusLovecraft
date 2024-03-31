package io.chthonic.mechanicuslovecraft.domain.dataapi.openai

interface OpenAiService {
    suspend fun testChatResponse()
    suspend fun testStreamChatResponse()
}