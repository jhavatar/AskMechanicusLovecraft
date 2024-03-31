package io.chthonic.mechanicuslovecraft.data.localconfig

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo

@Module
@InstallIn(SingletonComponent::class)
internal class LocalConfigSingletonModule {

    @Provides
    fun provideLocalConfigRepo(impl: LocalConfigRepoImpl): LocalConfigRepo = impl
}