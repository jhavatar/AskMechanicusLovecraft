package io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo

import androidx.paging.PagingData
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages() : Flow<PagingData<ChatMessageRecord>>

    suspend fun nextMessageIndex(): Long

    suspend fun clear()

    suspend fun insertMessage(
        chatMessageRecord: ChatMessageRecord
    )

    fun observeAllMessages(): Flow<List<ChatMessageRecord>>

    fun observeLatestMessages(messageCount: Int): Flow<List<ChatMessageRecord>>
}