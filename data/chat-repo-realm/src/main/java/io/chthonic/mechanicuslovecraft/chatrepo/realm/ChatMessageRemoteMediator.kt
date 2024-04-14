package io.chthonic.mechanicuslovecraft.chatrepo.realm

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import io.chthonic.mechanicuslovecraft.chatrepo.realm.daos.ChatMessageDao
import io.chthonic.mechanicuslovecraft.chatrepo.realm.models.ChatMessageEntity
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
internal class ChatMessageRemoteMediator @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
) : RemoteMediator<Int, ChatMessageEntity>() {

    override suspend fun initialize(): InitializeAction =
        if (chatMessageDao.getMessageCount() > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatMessageEntity>
    ): MediatorResult {
        return try {
            val page: Int = when (loadType) {
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.REFRESH -> 1
                LoadType.APPEND -> if (state.isEmpty()) {
                    1
                } else {
                    val itemCount = (state.firstItemOrNull()?.index.also {
                        Timber.v("load append, first index = $it")
                    } ?: 0) - (state.lastItemOrNull()?.index.also {
                        Timber.v("load append, last index = $it")
                    } ?: 0)
                    (itemCount / ChatMessageDao.PAGE_SIZE + 1).toInt()
                }
            }

            val data =
                chatMessageDao.getPageOfMessages(page = page, pageSize = ChatMessageDao.PAGE_SIZE)
            MediatorResult.Success(
                endOfPaginationReached = data.isEmpty()
            )

        } catch (e: IOException) {
            MediatorResult.Error(e)
        }
    }
}
