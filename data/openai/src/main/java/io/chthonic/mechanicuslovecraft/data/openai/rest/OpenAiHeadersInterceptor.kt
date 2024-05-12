package io.chthonic.mechanicuslovecraft.data.openai.rest

import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class OpenAiHeadersInterceptor @Inject constructor(
    private val localConfigRepo: LocalConfigRepo,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val apiKey =
            runBlocking { localConfigRepo.getValue(LocalConfigRepo.ConfigValue.OpenAiApiKey) }
        val apiOrganization =
            runBlocking { localConfigRepo.getValue(LocalConfigRepo.ConfigValue.OpenAiOrganization) }
        val request = original.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("OpenAI-Organization", apiOrganization ?: "")
            .build()
        return chain.proceed(request)
    }
}