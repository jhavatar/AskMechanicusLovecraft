package io.chthonic.mechanicuslovecraft.domain.presentationapi.models

/**
 * InputString is validated to not contain a blank string and be trimmed.
 */
@JvmInline
value class InputString private constructor(val text: String) {

    companion object {
        fun validateOrNull(text: CharSequence?): InputString? =
            when {
                text.isNullOrBlank() -> null
                else -> InputString(text.trim().toString())
            }
    }
}