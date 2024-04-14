package io.chthonic.mechanicuslovecraft.domain

import androidx.paging.PagingData
import androidx.paging.map
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveChatHistoryUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ObserveChatHistoryUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : ObserveChatHistoryUseCase {
    override fun execute(): Flow<PagingData<ChatMessage>> =
        chatRepository.observeMessages().map {
            it.map {
                ChatMessage(
                    index = it.index,
                    created = it.created,
                    role = it.value.role,
                    content = it.value.content,
                )
            }
        }
}