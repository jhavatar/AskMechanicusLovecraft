package io.chthonic.mechanicuslovecraft.chatrepo.realm

import androidx.paging.*
import io.chthonic.mechanicuslovecraft.chatrepo.realm.daos.ChatMessageDao
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RealmChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val chatMessageRemoteMediator: ChatMessageRemoteMediator,
    private val chatMessagePagingSource: ChatMessagePagingSource
) : ChatRepository {

    override suspend fun clear() {
        chatMessageDao.clear()
    }

    override suspend fun insertMessage(chatMessageRecord: ChatMessageRecord) {
        chatMessageDao.insertMessage(chatMessageRecord)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun observeMessages(): Flow<PagingData<ChatMessageRecord>> =
        Pager(
            config = PagingConfig(
                pageSize = ChatMessageDao.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = chatMessageRemoteMediator
        ) {
            chatMessagePagingSource
        }.flow.map { pagingData ->
            pagingData.map {
                it.toDomainModel()
            }
        }

    override suspend fun nextMessageIndex(): Long =
        chatMessageDao.getMaxIndex()?.let { it + 1 } ?: 0L

    override fun observeAllMessages(): Flow<List<ChatMessageRecord>> =
        chatMessageDao.getAllMessages().map {
            it.map {
                it.toDomainModel()
            }
        }

    override fun observeLatestMessages(messageCount: Int): Flow<List<ChatMessageRecord>> =
        chatMessageDao.getLatestMessages(messageCount).map {
            it.map {
                it.toDomainModel()
            }
        }
}