package io.chthonic.mechanicuslovecraft.domain.presentationapi

import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import kotlinx.coroutines.flow.Flow

interface ObserveStreamingResponseToMessageUseCase {
    suspend fun execute(message: InputString): Flow<String>
}