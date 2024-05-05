package io.chthonic.mechanicuslovecraft.domain.presentationapi

import kotlinx.coroutines.flow.Flow

interface ObserveNextAssistantResponseState {
    enum class AssistantResponseState {
        RECEIVING, COMPLETED
    }

    fun execute(): Flow<AssistantResponseState>
}