package io.chthonic.mechanicuslovecraft.data.openai

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.chthonic.mechanicuslovecraft.data.openai.rest.OpenAiApi
import io.chthonic.mechanicuslovecraft.data.openai.rest.OpenAiHeadersInterceptor
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.Model
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.ModelJsonAdapter
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.Role
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.RoleJsonAdapter
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

internal const val BASE_URL = "https://api.openai.com/v1/"

@Module
@InstallIn(SingletonComponent::class)
internal class OpenAiSingletonModule {

    @Provides
    @Named("okhttp-openai")
    fun provideOkHttpForOpenAi(
        client: OkHttpClient,
        headersInterceptor: OpenAiHeadersInterceptor,
    ): OkHttpClient =
        client.newBuilder().addInterceptor(headersInterceptor).build()

    @Provides
    @Named("moshi-openai")
    fun provideMoshiForOpenAi(moshi: Moshi): Moshi = moshi.newBuilder()
        .add(Model::class.java, ModelJsonAdapter())
        .add(Role::class.java, RoleJsonAdapter())
        .build()

    @Provides
    @Singleton
    fun provideOpenAiApi(
        @Named("okhttp-openai") client: OkHttpClient,
        @Named("moshi-openai") moshi: Moshi,
    ): OpenAiApi =
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    moshi
                )
            )
            .build().create(OpenAiApi::class.java)

    @Provides
    @Singleton
    fun provideOpenAiService(impl: OpenAiServiceImpl): OpenAiService = impl

}