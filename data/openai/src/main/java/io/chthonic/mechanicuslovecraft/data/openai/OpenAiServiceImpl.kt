package io.chthonic.mechanicuslovecraft.data.openai

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.chthonic.mechanicuslovecraft.data.openai.rest.OpenAiApi
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.*
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

private const val STREAMING_DONE_DATA = "[DONE]"

internal class OpenAiServiceImpl @Inject constructor(
    private val openAiApi: OpenAiApi, @Named("okhttp-openai") private val client: OkHttpClient,
    @Named("moshi-openai") private val moshi: Moshi,
) : OpenAiService {

    override suspend fun testChatResponse() {
        val response = getChatResponse()
        Timber.v("D3V: testChatResponse, response = $response")
    }

    override suspend fun testStreamChatResponse() {
        GlobalScope.launch {
            streamChatResponse().collect {
                Timber.v("D3V: testStreamChatResponse, response chunk = $it")
            }
        }
    }

    private suspend fun getChatResponse(): ChatResponse =
        openAiApi.getChatResponse(
            ChatRequest(
                messages = listOf(
                    Message(role = "user", content = "Say this is a test!")
                ),
                model = Model.GPT35_TURBO,
            )
        )

    private suspend fun streamChatResponse(): Flow<ChatResponseChunk> = callbackFlow {
        val responseAdapter: JsonAdapter<ChatResponseChunk> =
            moshi.adapter(ChatResponseChunk::class.java)

        val eventSourceListener = object : EventSourceListener() {
            private fun closeCallbackFlow(throwable: Throwable? = null) {
                if (!channel.isClosedForSend) {
                    channel.close(throwable)
                }
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                val isDone = data.equals(STREAMING_DONE_DATA, ignoreCase = true)
                when {
                    !isDone && data.isNotEmpty() -> responseAdapter.fromJson(data)
                        ?.let { response ->
                            trySendBlocking(response)
                        }

                    isDone -> closeCallbackFlow()
                }
            }

            override fun onClosed(eventSource: EventSource) {
                Timber.v("D3V: EventSourceListener.onClosed")
                closeCallbackFlow()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Timber.w("D3V: EventSourceListener.onFailure, t  = $t, response = $response")
                closeCallbackFlow(t)
            }
        }

        val requestJson = moshi.adapter(ChatRequest::class.java).toJson(
            ChatRequest(
                messages = listOf(
                    Message(role = "user", content = "Say this is a test!")
                ),
                model = Model.GPT35_TURBO,
                stream = true,
            )
        )
        val jsonType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val request = Request.Builder()
            .url(BASE_URL.toHttpUrl().newBuilder().addPathSegments("chat/completions").build())
            .post(requestJson.toRequestBody(contentType = jsonType))
            .build()

        val eventSource = EventSources.createFactory(client)
            .newEventSource(request = request, listener = eventSourceListener)
        client.newCall(request).execute()
        awaitClose {
            eventSource.cancel()
        }
    }
}