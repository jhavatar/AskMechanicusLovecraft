package io.chthonic.mechanicuslovecraft.domain.dataapi.models

import io.chthonic.mechanicuslovecraft.common.valueobjects.Role

data class ChatMessage(
    val role: Role, // The role of the author of this message.
    val content: String,
    val name: String? = null,
)