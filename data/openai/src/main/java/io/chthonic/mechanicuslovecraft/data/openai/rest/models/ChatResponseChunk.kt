package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.GptModel

@Keep
internal data class ChatResponseChunk(
    val id: String,
    val created: Int,
    val model: GptModel,
    val system_fingerprint: String?,
    val `object`: String,
    val choices: List<ChoiceChunk>,
)