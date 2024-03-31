package io.chthonic.mechanicuslovecraft.domain.localconfig

import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo.ConfigValue.OpenAiApiKey
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo.ConfigValue.OpenAiOrganization
import javax.inject.Inject

class InitializeLocalConfigUseCase @Inject constructor(
    private val localConfigRepo: LocalConfigRepo,
) {
    suspend fun execute(openAiApiKey: String, openAiOrganization: String) {
        localConfigRepo.initialize()
        if (localConfigRepo.getValue(OpenAiApiKey) == OpenAiApiKey.defaultHardcodedValue) {
            localConfigRepo.setValue(OpenAiApiKey, openAiApiKey)
        }
        if (localConfigRepo.getValue(OpenAiOrganization) == OpenAiOrganization.defaultHardcodedValue) {
            localConfigRepo.setValue(OpenAiOrganization, openAiOrganization)
        }
    }
}