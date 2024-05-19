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
        val GPT4_O = GptModel("gpt-4o")
        val VALUES = listOf(GPT35_TURBO, GPT4, GPT4_TURBO, GPT4_TURBO_PREVIEW, GPT4_O)
    }
}

fun String.toGptModel() =
    GptModel(this)