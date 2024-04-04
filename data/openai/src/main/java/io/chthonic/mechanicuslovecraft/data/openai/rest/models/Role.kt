package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import androidx.annotation.Keep

@Keep
@JvmInline
internal value class Role(val value: String) {
    companion object {
        val USER = Role("user")
        val ASSISTANT = Role("assistant")
    }
}