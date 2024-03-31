package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
internal data class ChoiceChunk(
    val index: Int,
    val delta: ChunkDelta,
    val finish_reason: String?,
)