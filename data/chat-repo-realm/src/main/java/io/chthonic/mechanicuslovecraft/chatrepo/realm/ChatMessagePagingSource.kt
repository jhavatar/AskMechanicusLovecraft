package io.chthonic.mechanicuslovecraft.chatrepo.realm

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.chthonic.mechanicuslovecraft.chatrepo.realm.daos.ChatMessageDao
import io.chthonic.mechanicuslovecraft.chatrepo.realm.models.ChatMessageEntity
import javax.inject.Inject

internal class ChatMessagePagingSource @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
) : PagingSource<Int, ChatMessageEntity>() {
    override fun getRefreshKey(state: PagingState<Int, ChatMessageEntity>): Int? =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessageEntity> {
        val pageNumber = params.key ?: 1
        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val response = chatMessageDao.getPageOfMessages(pageNumber)
        val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
        return LoadResult.Page(
            data = response,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}