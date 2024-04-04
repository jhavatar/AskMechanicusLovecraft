package io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models

data class ChatMessageChunk(
    val messageId: String, // same for all chunks in same message
    val created: Int, // Unix timestamp in seconds
    val role: String, // The role of the author of this message.
    val content: String
)