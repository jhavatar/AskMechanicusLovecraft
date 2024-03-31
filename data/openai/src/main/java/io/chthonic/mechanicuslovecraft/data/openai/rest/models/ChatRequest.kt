package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
internal data class ChatRequest(
    val messages: List<Message>,
    val model: Model,
    val stream: Boolean = false,
)