package io.chthonic.mechanicuslovecraft.chatrepo.realm

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import io.chthonic.mechanicuslovecraft.chatrepo.realm.daos.ChatMessageDao
import io.chthonic.mechanicuslovecraft.chatrepo.realm.paging.ChatMessagePagingSourceFactory
import io.chthonic.mechanicuslovecraft.common.coroutines.CoroutineDispatcherProvider
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal class RealmChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val chatMessagePagingSourceFactory: ChatMessagePagingSourceFactory,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ChatRepository {

    private val coroutineScope = CoroutineScope(coroutineDispatcherProvider.io + Job())

    init {
        coroutineScope.launch {
            chatMessageDao.messagesUpdated.collect {
                Timber.v("D3V, allMessage change - invalidate")
                // invalidate paging source to force fetching of the new updated data
                chatMessagePagingSourceFactory.invalidate()
            }
        }
    }

    override suspend fun clear() {
        chatMessageDao.clear()
    }

    override suspend fun insertMessage(chatMessageRecord: ChatMessageRecord) {
        chatMessageDao.insertMessage(chatMessageRecord)
    }

    override fun observePagedMessages(): Flow<PagingData<ChatMessageRecord>> =
        Pager(
            config = PagingConfig(
                pageSize = ChatMessageDao.PAGE_SIZE,
                enablePlaceholders = false,
            ),
        ) {
            chatMessagePagingSourceFactory.get()
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