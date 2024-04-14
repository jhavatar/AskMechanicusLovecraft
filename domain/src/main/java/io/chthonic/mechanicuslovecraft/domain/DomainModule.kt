package io.chthonic.mechanicuslovecraft.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.chthonic.mechanicuslovecraft.domain.openai.TestOpenAiUseCaseImpl
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveChatHistoryUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveAllMessagesUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveStreamingResponseToMessageUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.SubmitMessageAndObserveStreamingResponseUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.openai.TestOpenAiUseCase

@Module
@InstallIn(SingletonComponent::class)
internal class DomainModule {

    @Provides
    fun provideTestOpenAi(impl: TestOpenAiUseCaseImpl): TestOpenAiUseCase = impl

    @Provides
    fun provideObserveStreamingResponseToMessageUseCase(
        impl: ObserveStreamingResponseToMessageUseCaseImpl
    ): ObserveStreamingResponseToMessageUseCase = impl

    @Provides
    fun provideObserveChatHistoryUseCase(
        impl: ObserveChatHistoryUseCaseImpl
    ): ObserveChatHistoryUseCase = impl

    @Provides
    fun provideSubmitMessageAndObserveStreamingResponseUseCase(
        impl: SubmitMessageAndObserveStreamingResponseUseCaseImpl
    ): SubmitMessageAndObserveStreamingResponseUseCase = impl

    @Provides
    fun provideObserveLatestMessagesUseCase(
        impl: ObserveAllMessagesUseCaseImpl
    ): ObserveAllMessagesUseCase = impl
}