package io.chthonic.mechanicuslovecraft.domain.openai

import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import io.chthonic.mechanicuslovecraft.domain.presentationapi.openai.TestOpenAiUseCase
import javax.inject.Inject

internal class TestOpenAiUseCaseImpl @Inject constructor(private val openAiService: OpenAiService) :
    TestOpenAiUseCase {
    override suspend fun execute() {
        openAiService.testStreamChatResponse()
    }
}