package io.chthonic.mechanicuslovecraft.domain

import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveStreamingResponseToMessageUseCaseImpl @Inject constructor(
    private val openAiService: OpenAiService,
) : ObserveStreamingResponseToMessageUseCase {
    override fun execute(message: InputString): Flow<String> {
        val stringBuilder = StringBuilder("")
        return openAiService.observeStreamingChatResponseToUserMessage(message.text).map {
            stringBuilder.append(it.content)
            stringBuilder.toString()
        }
    }
}