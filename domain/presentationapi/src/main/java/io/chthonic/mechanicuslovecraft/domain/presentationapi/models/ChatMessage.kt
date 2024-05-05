package io.chthonic.mechanicuslovecraft.domain.presentationapi.models

import io.chthonic.mechanicuslovecraft.common.valueobjects.Role

data class ChatMessage(
    val content: String,
    val index: Long,
    val role: Role,
    val created: Int, // Unix timestamp in seconds
    val isDone: Boolean,
)