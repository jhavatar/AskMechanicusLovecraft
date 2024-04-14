package io.chthonic.mechanicuslovecraft.domain.dataapi.models

data class ChatMessageRecord (
    val index: Long,
    val created: Int, // Unix timestamp in seconds
    val value: ChatMessage,
)