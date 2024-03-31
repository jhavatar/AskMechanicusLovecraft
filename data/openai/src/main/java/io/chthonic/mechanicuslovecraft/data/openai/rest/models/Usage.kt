package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
internal data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int,
)