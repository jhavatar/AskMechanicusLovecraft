package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
@JvmInline
internal value class Model(val value: String) {

    companion object {
        val GPT4 = Model("gpt-4")
        val GPT35_TURBO = Model("gpt-3.5-turbo")
        val GPT4_TURBO_PREVIEW = Model("gpt-4-turbo-preview")
    }
}
