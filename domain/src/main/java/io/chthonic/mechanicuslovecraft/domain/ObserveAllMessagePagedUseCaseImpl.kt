package io.chthonic.mechanicuslovecraft.domain

import androidx.paging.PagingData
import androidx.paging.map
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.ktx.toPresentationModel
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveAllMessagePagedUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveAllMessagePagedUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : ObserveAllMessagePagedUseCase {
    override fun execute(): Flow<PagingData<ChatMessage>> =
        chatRepository.observePagedMessages().map { pagingData ->
            pagingData.map {
                it.toPresentationModel()
            }
        }
}