package io.chthonic.mechanicuslovecraft.domain.localconfig

import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo
import javax.inject.Inject

class InitializeLocalConfigUseCase @Inject constructor(
    private val localConfigRepo: LocalConfigRepo,
) {
    suspend fun execute(openAiApiKey: String, openAiOrganization: String) {
        localConfigRepo.initialize(
            defaultOpenAiKey = openAiApiKey,
            defaultOpenAiOrg = openAiOrganization,
        )
    }
}