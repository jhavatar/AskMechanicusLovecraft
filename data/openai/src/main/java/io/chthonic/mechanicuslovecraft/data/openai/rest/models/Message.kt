package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
internal data class Message(
    val role: String,
    val content: String,
)