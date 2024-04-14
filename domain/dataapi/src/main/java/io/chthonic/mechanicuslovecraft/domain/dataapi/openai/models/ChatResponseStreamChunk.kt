package io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models

import io.chthonic.mechanicuslovecraft.common.valueobjects.Role

data class ChatResponseStreamChunk(
    val messageId: String, // same for all chunks in same message
    val created: Int, // Unix timestamp in seconds
    val role: Role, // The role of the author of this message.
    val content: String
)