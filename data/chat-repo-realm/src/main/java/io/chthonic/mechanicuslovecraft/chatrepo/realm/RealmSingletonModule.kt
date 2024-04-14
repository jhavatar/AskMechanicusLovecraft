package io.chthonic.mechanicuslovecraft.chatrepo.realm

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.chthonic.mechanicuslovecraft.chatrepo.realm.models.ChatMessageEntity
import io.chthonic.mechanicuslovecraft.domain.dataapi.chatrepo.ChatRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RealmSingletonModule {

    @Provides
    @Singleton
    fun provideRealmDb(): Realm {
        val config = RealmConfiguration.Builder(schema = setOf(ChatMessageEntity::class))
            .schemaVersion(1L)
            .deleteRealmIfMigrationNeeded()
            .build()
        return Realm.open(config)
    }

    @Provides
    fun provideChatRepository(impl: RealmChatRepository): ChatRepository = impl
}