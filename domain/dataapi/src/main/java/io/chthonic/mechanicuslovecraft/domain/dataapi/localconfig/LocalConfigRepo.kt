package io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig

interface LocalConfigRepo {
    sealed interface ConfigValue<T> {
        val key: String
        val defaultHardcodedValue: T

        data object OpenAiApiKey : ConfigValue<String> {
            override val key: String = "OpenAiApiKey"
            override val defaultHardcodedValue: String = ""
        }

        data object OpenAiOrganization : ConfigValue<String> {
            override val key: String = "OpenAiOrganization"
            override val defaultHardcodedValue: String = ""
        }
    }

    suspend fun initialize()
    suspend fun <T> getValue(configValue: ConfigValue<T>): T
    suspend fun <T> setValue(configValue: ConfigValue<T>, value: T)
}