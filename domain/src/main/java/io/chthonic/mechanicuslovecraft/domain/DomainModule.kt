package io.chthonic.mechanicuslovecraft.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.chthonic.mechanicuslovecraft.domain.openai.TestOpenAiUseCaseImpl
import io.chthonic.mechanicuslovecraft.domain.presentationapi.openai.TestOpenAiUseCase

@Module
@InstallIn(SingletonComponent::class)
internal class DomainModule {

    @Provides
    fun provideTestOpenAi(impl: TestOpenAiUseCaseImpl): TestOpenAiUseCase = impl
}