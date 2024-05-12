package io.chthonic.mechanicuslovecraft.data.localconfig

import android.app.Application
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.TweaksGraph
import com.telefonica.tweaks.domain.tweaksGraph
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo.ConfigValue.*
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.GptModel
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class LocalConfigRepoImpl @Inject constructor(private val app: Application) :
    LocalConfigRepo {
    override suspend fun initialize(defaultOpenAiKey: String, defaultOpenAiOrg: String) {
        Tweaks.init(
            app,
            genLocalConfigScreen(
                defaultOpenAiKey = defaultOpenAiKey,
                defaultOpenAiOrg = defaultOpenAiOrg,
            )
        )
    }

    override suspend fun <T> getValue(configValue: LocalConfigRepo.ConfigValue<T>): T =
        Tweaks.getReference().getTweak(configValue.key, configValue.defaultHardcodedValue)

    override suspend fun <T> setValue(configValue: LocalConfigRepo.ConfigValue<T>, value: T) {
        Tweaks.getReference().setTweakValue(configValue.key, value)
    }

    private fun genLocalConfigScreen(
        defaultOpenAiKey: String,
        defaultOpenAiOrg: String,
    ): TweaksGraph = tweaksGraph {
        cover("Settings") {
            editableString(
                key = OpenAiApiKey.key,
                name = "OpenAi API key",
                defaultValue = flowOf(defaultOpenAiKey),
            )
            editableString(
                key = OpenAiOrganization.key,
                name = "OpenAi Organization",
                defaultValue = flowOf(defaultOpenAiOrg)
            )
            dropDownMenu(
                key = OpenAiGptModel.key,
                name = "GPT Model",
                values = listOf(
                    GptModel.GPT35_TURBO.value,
                    GptModel.GPT4.value,
                    GptModel.GPT4_TURBO.value,
                    GptModel.GPT4_TURBO_PREVIEW.value
                ),
                defaultValue = flowOf(OpenAiGptModel.defaultHardcodedValue)
            )
        }
    }
}