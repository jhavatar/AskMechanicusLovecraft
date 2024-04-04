package io.chthonic.mechanicuslovecraft

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.chthonic.mechanicuslovecraft.common.coroutines.CoroutineDispatcherProvider

@Module
@InstallIn(SingletonComponent::class)
class CommonSingletonModule {

    @Provides
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider =
        CoroutineDispatcherProvider()
}