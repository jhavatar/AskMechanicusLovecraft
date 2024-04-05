package io.chthonic.mechanicuslovecraft.domain

import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val SYSTEM_META_INFO =
    "Pretend to be HP Lovecraft that is also a member of the Adeptus Mechanicus and respond to all question as he might"

class ObserveStreamingResponseToMessageUseCaseImpl @Inject constructor(
    private val openAiService: OpenAiService,
) : ObserveStreamingResponseToMessageUseCase {

    override fun execute(message: InputString): Flow<String> {
        val stringBuilder = StringBuilder("")
        return openAiService.observeStreamingChatResponseToUserMessage(
            userMessage = message.text,
            systemMetaInfo = SYSTEM_META_INFO
        ).map {
            stringBuilder.append(it.content)
            stringBuilder.toString()
        }
    }
}