package io.chthonic.mechanicuslovecraft.data.openai

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.chthonic.mechanicuslovecraft.common.coroutines.CoroutineDispatcherProvider
import io.chthonic.mechanicuslovecraft.common.valueobjects.Role
import io.chthonic.mechanicuslovecraft.data.openai.rest.OpenAiApi
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.ChatRequest
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.ChatResponseChunk
import io.chthonic.mechanicuslovecraft.data.openai.rest.models.Message
import io.chthonic.mechanicuslovecraft.domain.dataapi.localconfig.LocalConfigRepo
import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessage
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.OpenAiService
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.ChatResponseStreamChunk
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.GptModel
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.toGptModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapNotNull
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
    private val openAiApi: OpenAiApi,
    @Named("okhttp-openai") private val client: OkHttpClient,
    @Named("moshi-openai") private val moshi: Moshi,
    private val localConfigRepo: LocalConfigRepo,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : OpenAiService {

    private val coroutineScope: CoroutineScope =
        CoroutineScope(coroutineDispatcherProvider.io + Job())

    override suspend fun testChatResponse() {
        val response = openAiApi.getChatResponse(
            buildChatRequest(
                "Say this is a test!",
                model = localConfigRepo.getValue(LocalConfigRepo.ConfigValue.OpenAiGptModel)
                    .toGptModel(),
            )
        )
        Timber.v("D3V: testChatResponse, response = $response")
    }

    override suspend fun testStreamChatResponse() {
        coroutineScope.launch {
            streamChatResponse(
                buildChatRequest(
                    message = "Say this is a test!",
                    stream = true,
                    model = localConfigRepo.getValue(LocalConfigRepo.ConfigValue.OpenAiGptModel)
                        .toGptModel(),
                ),
            ).collect {
                Timber.v("D3V: testStreamChatResponse, response chunk = $it")
            }
        }
    }

    private fun buildChatRequest(
        message: String,
        systemMetaInfo: String? = null,
        model: GptModel,
        stream: Boolean = false,
    ): ChatRequest = buildChatRequest(
        messageHistory = listOf(
            Message(
                role = Role.User,
                content = message,
            )
        ),
        systemMetaInfo = systemMetaInfo,
        model = model,
        stream = stream,
    )

    private fun buildChatRequest(
        messageHistory: List<Message>,
        systemMetaInfo: String? = null,
        model: GptModel,
        stream: Boolean = false,
    ): ChatRequest = ChatRequest(
        messages = messageHistory + (systemMetaInfo?.let {
            listOf(
                Message(
                    content = systemMetaInfo,
                    role = Role.System,
                )
            )
        } ?: emptyList()),
        model = model,
        stream = stream,
    )

    override suspend fun observeStreamingResponseToChat(
        messageHistory: List<ChatMessage>,
        systemMetaInfo: String?,
    ): Flow<ChatResponseStreamChunk> {
        var responseRole: Role = Role.Assistant
        return streamChatResponse(
            buildChatRequest(
                messageHistory = messageHistory.map {
                    Message(
                        role = it.role,
                        content = it.content
                    )
                },
                systemMetaInfo = systemMetaInfo,
                model = localConfigRepo.getValue(LocalConfigRepo.ConfigValue.OpenAiGptModel)
                    .toGptModel(),
                stream = true,
            )
        ).mapNotNull { chunk ->
            chunk.choices.firstOrNull()?.delta?.let { delta ->
                responseRole = delta.role ?: responseRole
                delta.content?.let { content ->
                    ChatResponseStreamChunk(
                        messageId = chunk.id,
                        content = content,
                        created = chunk.created,
                        role = responseRole,
                    )
                }
            }
        }
    }


    private fun streamChatResponse(chatRequest: ChatRequest): Flow<ChatResponseChunk> =
        callbackFlow {
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
                    closeCallbackFlow()
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: Response?
                ) {
                    // forward exception to observer
                    this@callbackFlow.close(
                        when {
                            t?.message != null -> t
                            response?.code == 401 -> RuntimeException("Unauthorized API access")
                            else -> t ?: RuntimeException()
                        }
                    )
                    closeCallbackFlow(t)
                }
            }

            val requestJson = moshi.adapter(ChatRequest::class.java).toJson(chatRequest)
            val jsonType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val request = Request.Builder()
                .url(
                    BASE_URL.toHttpUrl().newBuilder().addPathSegments("chat/completions")
                        .build()
                )
                .post(requestJson.toRequestBody(contentType = jsonType))
                .build()

            val eventSource = EventSources.createFactory(client)
                .newEventSource(request = request, listener = eventSourceListener)
            coroutineScope.launch {
                try {
                    Timber.v("D3V: client.newCall")
                    client.newCall(request).execute()
                } catch (e: Exception) {
                    // exception forwarded in onFailure
                    Timber.e(e, "client.newCall failed")
                }
            }
            awaitClose {
                eventSource.cancel()
            }
        }
}