package io.chthonic.mechanicuslovecraft.chatrepo.realm.daos

import io.chthonic.mechanicuslovecraft.chatrepo.realm.models.ChatMessageEntity
import io.chthonic.mechanicuslovecraft.common.coroutines.CoroutineDispatcherProvider
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val coroutineScope = CoroutineScope(coroutineContext)

    private val _lazyLoadingMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val lazyLoadingMessages: StateFlow<List<ChatMessageEntity>> = _lazyLoadingMessages.asStateFlow()

    val messagesUpdated: Flow<Unit> = lazyLoadingMessages.map { }

    init {
        coroutineScope.launch {
            realm.query(ChatMessageEntity::class)
                .sort(INDEX_FIELD_NAME, Sort.DESCENDING)
                .asFlow()
                .collect {
                    _lazyLoadingMessages.emit(it.list)
                }
        }
    }

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
                    isDone = message.isDone
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
            lazyLoadingMessages.value.let { messages ->
                val startIndex = page * pageSize // first page index is 0
                val endIndex = (page * pageSize + (pageSize - 1)).coerceAtMost(messages.size)
                if (startIndex < endIndex) {
                    messages.subList(startIndex, endIndex)
                } else {
                    emptyList()
                }
            }
        }

    fun getAllMessages(): Flow<List<ChatMessageEntity>> =
        realm.query(ChatMessageEntity::class)
            .sort(INDEX_FIELD_NAME, Sort.DESCENDING)
            .asFlow()
            .map {
                it.list
            }

    fun getLatestMessages(messageCount: Int): Flow<List<ChatMessageEntity>> =
        realm.query(ChatMessageEntity::class)
            .sort(INDEX_FIELD_NAME, Sort.DESCENDING)
            .limit(messageCount)
            .asFlow()
            .map {
                it.list
            }
}