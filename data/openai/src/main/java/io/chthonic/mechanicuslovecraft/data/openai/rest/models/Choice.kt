package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
internal data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String,
)