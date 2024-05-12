package io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig

import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.GptModel

interface LocalConfigRepo {
    sealed interface ConfigValue<T> {
        val key: String
        val defaultHardcodedValue: T

        data object OpenAiApiKey : ConfigValue<String?> {
            override val key: String = "OpenAiApiKey"
            override val defaultHardcodedValue: String? = null
        }

        data object OpenAiOrganization : ConfigValue<String?> {
            override val key: String = "OpenAiOrganization"
            override val defaultHardcodedValue: String? = null
        }

        data object OpenAiGptModel : ConfigValue<String> {
            override val key: String = "OpenAiGPTModel"
            override val defaultHardcodedValue: String = GptModel.GPT35_TURBO.value
        }
    }

    suspend fun initialize(defaultOpenAiKey: String, defaultOpenAiOrg: String)
    suspend fun <T> getValue(configValue: ConfigValue<T>): T
    suspend fun <T> setValue(configValue: ConfigValue<T>, value: T)
}