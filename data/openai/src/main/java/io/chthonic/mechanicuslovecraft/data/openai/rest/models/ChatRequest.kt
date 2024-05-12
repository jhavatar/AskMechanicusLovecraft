package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.GptModel

@Keep
internal data class ChatRequest(
    val messages: List<Message>,
    val model: GptModel,
    val stream: Boolean = false,
)