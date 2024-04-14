package io.chthonic.mechanicuslovecraft.domain.presentationapi

import androidx.paging.PagingData
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ObserveChatHistoryUseCase {
    fun execute(): Flow<PagingData<ChatMessage>>
}