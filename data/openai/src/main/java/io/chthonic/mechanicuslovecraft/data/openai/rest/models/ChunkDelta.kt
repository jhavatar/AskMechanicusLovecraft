package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep
import io.chthonic.mechanicuslovecraft.common.valueobjects.Role

@Keep
internal data class ChunkDelta(
    val content: String?,
    val role: Role?,
)