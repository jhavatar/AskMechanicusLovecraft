package io.chthonic.mechanicuslovecraft.data.localconfig

import android.app.Application
import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.TweaksGraph
import com.telefonica.tweaks.domain.tweaksGraph
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo.ConfigValue.OpenAiApiKey
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo.ConfigValue.OpenAiOrganization
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class LocalConfigRepoImpl @Inject constructor(private val app: Application) :
    LocalConfigRepo {
    override suspend fun initialize() {
        Tweaks.init(app, genLocalConfigScreen())
    }

    override suspend fun <T> getValue(configValue: LocalConfigRepo.ConfigValue<T>): T =
        Tweaks.getReference().getTweak(configValue.key, configValue.defaultHardcodedValue)

    override suspend fun <T> setValue(configValue: LocalConfigRepo.ConfigValue<T>, value: T) {
        Tweaks.getReference().setTweakValue(configValue.key, value)
    }

    private fun genLocalConfigScreen(): TweaksGraph = tweaksGraph {
        cover("Tweaks") {
            editableString(
                key = OpenAiApiKey.key,
                name = "OpenAi API key",
                defaultValue = flowOf(OpenAiApiKey.defaultHardcodedValue),
            )
            editableString(
                key = OpenAiOrganization.key,
                name = "OpenAi Organization",
                defaultValue = flowOf(OpenAiOrganization.defaultHardcodedValue)
            )
        }
    }
}