package io.chthonic.mechanicuslovecraft.domain.presentationapi

import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ObserveAllMessagesUseCase {
    fun execute(): Flow<List<ChatMessage>>
}