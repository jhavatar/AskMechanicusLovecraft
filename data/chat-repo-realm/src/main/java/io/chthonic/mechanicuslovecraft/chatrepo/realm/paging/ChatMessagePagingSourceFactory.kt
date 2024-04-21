package io.chthonic.mechanicuslovecraft.chatrepo.realm.paging

import io.chthonic.mechanicuslovecraft.chatrepo.realm.daos.ChatMessageDao
import javax.inject.Inject

internal class ChatMessagePagingSourceFactory @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
) {

    private var chatMessagePagingSource: ChatMessagePagingSource =
        ChatMessagePagingSource(chatMessageDao)
        @Synchronized get
        @Synchronized set

    @Synchronized
    fun get(): ChatMessagePagingSource = chatMessagePagingSource

    @Synchronized
    fun invalidate() {
        val oldSource = chatMessagePagingSource
        val newSource = ChatMessagePagingSource(chatMessageDao)
        chatMessagePagingSource = newSource
        oldSource.invalidate()
    }
}