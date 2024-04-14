package io.chthonic.mechanicuslovecraft.domain.presentationapi

import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString

interface SubmitMessageAndObserveStreamingResponseUseCase {
    suspend fun execute(inputString: InputString)
}