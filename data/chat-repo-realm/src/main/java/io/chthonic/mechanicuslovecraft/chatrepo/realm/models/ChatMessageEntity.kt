package io.chthonic.mechanicuslovecraft.chatrepo.realm.models

import androidx.annotation.Keep
import io.chthonic.mechanicuslovecraft.common.valueobjects.Role
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessage
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@Keep
class ChatMessageEntity : RealmObject {
    @PrimaryKey
    var index: Long = 0
    var created: RealmInstant = RealmInstant.now()
    var role: String = ""
    var content: String = ""
    var name: String? = null
    var isDone: Boolean = true
    var isError: Boolean = false

    fun toDomainModel() =
        ChatMessageRecord(
            index = index,
            created = created.epochSeconds.toInt(),
            isDone = isDone,
            isError = isError,
            value = ChatMessage(
                role = Role.validate(role),
                content = content,
                name = name,
            ),
        )
}