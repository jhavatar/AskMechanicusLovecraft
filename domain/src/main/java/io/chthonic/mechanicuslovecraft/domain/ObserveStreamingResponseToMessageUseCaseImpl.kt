package io.chthonic.mechanicuslovecraft.domain

import io.chthonic.mechanicuslovecraft.common.valueobjects.Role
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessage
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveStreamingResponseToMessageUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import timber.log.Timber
import javax.inject.Inject

private val SYSTEM_META_INFO =
    "Pretend to be HP Lovecraft that is also a member of the Adeptus Mechanicus and respond to all question as he might"

internal class ObserveStreamingResponseToMessageUseCaseImpl @Inject constructor(
    private val openAiService: OpenAiService,
    private val chatRepository: ChatRepository,
) : ObserveStreamingResponseToMessageUseCase {

    override fun execute(message: InputString): Flow<String> {
        val stringBuilder = StringBuilder("")
        var created: Int = (System.currentTimeMillis() / 1000L).toInt()
        var role: Role = Role.Assistant
        return openAiService.observeStreamingResponseToChat(
            messageHistory = listOf(
                ChatMessage(
                    role = Role.User,
                    content = message.text,
                )
            ),
            systemMetaInfo = SYSTEM_META_INFO,
        ).map {
            created = it.created
            role = it.role
            stringBuilder.append(it.content)
            stringBuilder.toString()
        }.onCompletion {
            chatRepository.insertMessage(
                ChatMessageRecord(
                    index = chatRepository.nextMessageIndex().also {
                        Timber.v("D3V: execute, nextMessageIndex = $it")
                    },
                    created = created,
                    value = ChatMessage(
                        role = role,
                        content = stringBuilder.toString()
                    )
                )
            )
        }
    }
}