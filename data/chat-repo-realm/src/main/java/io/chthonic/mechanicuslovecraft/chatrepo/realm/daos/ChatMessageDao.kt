package io.chthonic.mechanicuslovecraft.chatrepo.realm.daos

import io.chthonic.mechanicuslovecraft.chatrepo.realm.models.ChatMessageEntity
import io.chthonic.mechanicuslovecraft.common.coroutines.CoroutineDispatcherProvider
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val INDEX_FIELD_NAME = "index"

@Singleton
internal class ChatMessageDao @Inject constructor(
    private val realm: Realm,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    companion object {
        const val PAGE_SIZE = 20
    }

    private val coroutineContext = coroutineDispatcherProvider.io + Job()

    suspend fun clear() {
        withContext(coroutineContext) {
            realm.write {
                deleteAll()
            }
        }
    }

    suspend fun insertMessage(message: ChatMessageRecord) {
        withContext(coroutineContext) {
            realm.write {
                val entity = ChatMessageEntity().apply {
                    index = message.index
                    created = RealmInstant.from(message.created.toLong(), 0)
                    role = message.value.role.value
                    content = message.value.content
                    name = message.value.name
                }
                copyToRealm(entity, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    suspend fun getMessageCount(): Int = withContext(coroutineContext) {
        realm.query(ChatMessageEntity::class).count().asFlow().map { it.toInt() }.firstOrNull() ?: 0
    }

    suspend fun getMaxIndex(): Long? = withContext(coroutineContext) {
        realm.query(ChatMessageEntity::class).max(INDEX_FIELD_NAME, Long::class).asFlow()
            .firstOrNull()
    }

    suspend fun getPageOfMessages(page: Int, pageSize: Int = PAGE_SIZE): List<ChatMessageEntity> =
        withContext(coroutineContext) {
            val maxIndex = getMaxIndex()
            Timber.v("D3V: getPageOfMessages, page = $page, pageSize = $pageSize, maxIndex = $maxIndex")
            val pageMaxIndexIncluded = if (maxIndex != null) {
                maxIndex - ((page - 1) * pageSize).toLong()
            } else {
                -1L
            }
            Timber.v("D3V: getPageOfMessages, pageMaxIndexIncluded = $pageMaxIndexIncluded")
            if (pageMaxIndexIncluded >= 0L) {
                realm.query(
                    ChatMessageEntity::class,
                    "$INDEX_FIELD_NAME <= $0",
                    pageMaxIndexIncluded
                )
                    .sort(INDEX_FIELD_NAME, Sort.DESCENDING).limit(pageSize).asFlow()
                    .firstOrNull()?.list
                    ?: emptyList()
            } else {
                emptyList()
            }
        }

    fun getAllMessages(): Flow<List<ChatMessageEntity>> =
        realm.query(ChatMessageEntity::class)
            .sort(INDEX_FIELD_NAME, Sort.DESCENDING)//.limit(PAGE_SIZE)
            .asFlow()
            .map {
                it.list
            }

    fun getLatestMessages(messageCount: Int): Flow<List<ChatMessageEntity>> =
        realm.query(ChatMessageEntity::class).sort(INDEX_FIELD_NAME, Sort.DESCENDING)
            .limit(messageCount)
            .asFlow()
            .map {
                it.list
            }
}