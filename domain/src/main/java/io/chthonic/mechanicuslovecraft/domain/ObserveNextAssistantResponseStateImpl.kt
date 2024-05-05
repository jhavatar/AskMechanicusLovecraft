package io.chthonic.mechanicuslovecraft.domain

import io.chthonic.mechanicuslovecraft.common.valueobjects.Role
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveNextAssistantResponseState
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveNextAssistantResponseState.AssistantResponseState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ObserveNextAssistantResponseStateImpl @Inject constructor(
    private val chatMessageRepository: ChatRepository
) : ObserveNextAssistantResponseState {

    override fun execute(): Flow<AssistantResponseState> = flow {
        val nextIndex = chatMessageRepository.nextMessageIndex()
        chatMessageRepository.observeLatestMessage().collect {
            if ((it.value.role == Role.Assistant) && (it.index >= nextIndex)) {
                if (it.isDone) {
                    emit(AssistantResponseState.COMPLETED)
                    currentCoroutineContext().cancel()
                } else {
                    emit(AssistantResponseState.RECEIVING)
                }
            }
        }
    }
}