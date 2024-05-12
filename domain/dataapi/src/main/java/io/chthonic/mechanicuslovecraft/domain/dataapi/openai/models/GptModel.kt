package io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models

import androidx.annotation.Keep

@Keep
@JvmInline
value class GptModel(val value: String) {

    companion object {
        val GPT35_TURBO = GptModel("gpt-3.5-turbo")
        val GPT4 = GptModel("gpt-4")
        val GPT4_TURBO = GptModel("gpt-4-turbo")
        val GPT4_TURBO_PREVIEW = GptModel("gpt-4-turbo-preview")
    }
}

fun String.toGptModel() =
    GptModel(this)