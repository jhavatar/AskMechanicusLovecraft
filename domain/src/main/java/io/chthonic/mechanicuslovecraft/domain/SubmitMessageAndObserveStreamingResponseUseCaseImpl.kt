package io.chthonic.mechanicuslovecraft.domain

import io.chthonic.mechanicuslovecraft.common.valueobjects.Role
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessage
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import io.chthonic.mechanicuslovecraft.domain.presentationapi.SubmitMessageAndObserveStreamingResponseUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject

private val SYSTEM_META_INFO =
    "Pretend to be HP Lovecraft that is also a member of the Adeptus Mechanicus and respond to all question as he might"

private const val MESSAGE_HISTORY_WINDOW_SIZE = 20

internal class SubmitMessageAndObserveStreamingResponseUseCaseImpl @Inject constructor(
    private val openAiService: OpenAiService,
    private val chatRepository: ChatRepository,
) : SubmitMessageAndObserveStreamingResponseUseCase {
    override suspend fun execute(inputString: InputString) {
        val requestIndex = chatRepository.nextMessageIndex()

        chatRepository.insertMessage(
            ChatMessageRecord(
                index = requestIndex,
                created = (System.currentTimeMillis() / 1000L).toInt(),
                value = ChatMessage(
                    role = Role.User,
                    content = inputString.text,
                )
            )
        )

        val chatHistory =
            chatRepository.observeLatestMessages(MESSAGE_HISTORY_WINDOW_SIZE).firstOrNull()?.map {
                ChatMessage(
                    role = it.value.role,
                    content = it.value.content,
                )
            }
        observeResponse(requestIndex + 1L, inputString, chatHistory)
    }

    private suspend fun observeResponse(
        answerIndex: Long,
        message: InputString,
        chatHistory: List<ChatMessage>? = null
    ) {
        val stringBuilder = StringBuilder("")
        var created: Int = (System.currentTimeMillis() / 1000L).toInt()
        var role: Role = Role.Assistant
        openAiService.observeStreamingResponseToChat(
            messageHistory = (chatHistory ?: emptyList()) + listOf(
                ChatMessage(
                    role = Role.User,
                    content = message.text,
                )
            ),
            systemMetaInfo = SYSTEM_META_INFO,
        ).collect {
            created = it.created
            role = it.role
            stringBuilder.append(it.content)
            Timber.v("D3V: observeResponse chunk = $it")

            chatRepository.insertMessage(
                ChatMessageRecord(
                    index = answerIndex,
                    created = created,
                    value = ChatMessage(
                        role = role,
                        content = stringBuilder.toString()
                    )
                )
            )
        }
        Timber.v("D3V: observeResponse done")
    }
}