package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep
import io.chthonic.mechanicuslovecraft.common.valueobjects.Role

@Keep
internal data class Message(
    val role: Role,
    val content: String,
)