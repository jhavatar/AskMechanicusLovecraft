package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
internal data class ChunkDelta(
    val content: String?,
    val role: Role?,
)