package io.chthonic.mechanicuslovecraft.chatrepo.realm.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.chthonic.mechanicuslovecraft.chatrepo.realm.daos.ChatMessageDao
import io.chthonic.mechanicuslovecraft.chatrepo.realm.models.ChatMessageEntity
import timber.log.Timber

private const val STARTING_PAGE_INDEX = 0

internal class ChatMessagePagingSource(
    private val chatMessageDao: ChatMessageDao,
) : PagingSource<Int, ChatMessageEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ChatMessageEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessageEntity> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            Timber.v("D3V: load. page = $page, params.key = ${params.key}, params.loadSize = ${params.loadSize}, params.placeholdersEnabled = ${params.placeholdersEnabled}")
            val response = chatMessageDao.getPageOfMessages(page, params.loadSize)
            LoadResult.Page(
                data = response,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page.minus(1),
                nextKey = if (response.isEmpty()) null else page.plus(
                    (params.loadSize / ChatMessageDao.PAGE_SIZE).coerceAtLeast(1)
                )
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}