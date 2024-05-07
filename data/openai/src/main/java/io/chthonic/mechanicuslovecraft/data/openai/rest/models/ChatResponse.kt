package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
internal data class ChatResponse(
    val id: String,
    val created: Int,
    val model: Model,
    val system_fingerprint: String?,
    val `object`: String,
    val choices: List<Choice>,
    val usage: Usage,
)