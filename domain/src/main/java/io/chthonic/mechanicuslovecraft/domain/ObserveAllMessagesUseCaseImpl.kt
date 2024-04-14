package io.chthonic.mechanicuslovecraft.domain

import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.ktx.toPresentationModel
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveAllMessagesUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ObserveAllMessagesUseCaseImpl @Inject constructor(private val chatRepository: ChatRepository) :
    ObserveAllMessagesUseCase {

    override fun execute(): Flow<List<ChatMessage>> =
        chatRepository.observeAllMessages().map {
            it.map {
                it.toPresentationModel()
            }
        }
}