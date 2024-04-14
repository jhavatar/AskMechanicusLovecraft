package io.chthonic.mechanicuslovecraft.presentation.console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.chthonic.mechanicuslovecraft.common.valueobjects.Role
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveChatHistoryUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveAllMessagesUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.SubmitMessageAndObserveStreamingResponseUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import io.chthonic.mechanicuslovecraft.domain.presentationapi.openai.TestOpenAiUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val UNKNOWN_COMMAND = "unknown command"
private const val NO_TRANS = "no transaction"
private const val KEY_NOT_SET = "key not set"

private const val COLOR_USER: Long = 0xFFFAFA91

//private val COLOR_ERROR = Color.valueOf(0xFFB0E5)
private const val COLOR_AI: Long = 0xFFFA84C6

@HiltViewModel
internal class ConsoleViewModel constructor(
    private val testOpenAiUseCase: TestOpenAiUseCase,
    private val observeChatHistoryUseCase: ObserveChatHistoryUseCase,
    private val submitMessageAndObserveStreamingResponseUseCase: SubmitMessageAndObserveStreamingResponseUseCase,
    private val observeAllMessagesUseCase: ObserveAllMessagesUseCase,
    initStateState: State,
) : ViewModel() {

    @Inject
    constructor(
        testOpenAiUseCase: TestOpenAiUseCase,
        observeChatHistoryUseCase: ObserveChatHistoryUseCase,
        submitMessageAndObserveStreamingResponseUseCase: SubmitMessageAndObserveStreamingResponseUseCase,
        observeAllMessagesUseCase: ObserveAllMessagesUseCase,
    ) : this(
        testOpenAiUseCase,
        observeChatHistoryUseCase,
        submitMessageAndObserveStreamingResponseUseCase,
        observeAllMessagesUseCase,
        State(),
    )

    data class State(
        val inputTextToDisplay: String = "",
        val inputSubmitEnabled: Boolean = true,
        val messages: Flow<List<MessageItem>> = emptyFlow(),
    )

    private val _state = MutableStateFlow(initStateState)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = state.value.copy(
                messages = observeAllMessagesUseCase.execute().map { lazyList ->
                    lazyList.map {
                        when (it.role) {
                            Role.User -> MessageItem.Input(
                                index = it.index,
                                text = it.content,
                            )

                            else -> MessageItem.Response(
                                index = it.index,
                                text = it.content
                            )
                        }
                    }
                },
            )
        }
    }

    fun onTextChanged(text: String) {
        Timber.v("D3V: onInputSubmitted, text = $text currentSate = ${state.value}")
        _state.value = state.value.copy(inputTextToDisplay = text)
    }

    fun onInputSubmitted() {
        val currentSate = state.value
        Timber.v("D3V: onInputSubmitted, currentSate = $currentSate")
        if (!currentSate.inputSubmitEnabled) return
        InputString.validateOrNull(currentSate.inputTextToDisplay)?.let { input ->
            _state.value = currentSate.copy(
                inputTextToDisplay = "",
                inputSubmitEnabled = false,
            )
            executeCommandLineInput(input)
        }
    }

    private fun executeCommandLineInput(input: InputString) {
        Timber.v("D3V: executeCommandLineInput ${input.text}")
        viewModelScope.launch {
            try {
                submitMessageAndObserveStreamingResponseUseCase.execute(input)
            } catch (e: Exception) {
                Timber.e(e, "D3V: submitMessageAndObserveStreamingResponseUseCase failed")
            } finally {
                Timber.v("D3V: submitMessageAndObserveStreamingResponseUseCase completed")
                _state.value = state.value.copy(
                    inputSubmitEnabled = true,
                )
            }
        }
    }

    private fun List<MessageItem>.append(messageItem: MessageItem): List<MessageItem> =
        this.toMutableList().apply {
            add(messageItem)
        }.toList()
}

sealed interface MessageItem {

    val index: Long
    val text: String
    val color: Long
    val name: String

    val formattedText: String

    data class Input(
        override val text: String,
        override val index: Long,
        override val name: String = "You"
    ) : MessageItem {
        override val color = COLOR_USER
        override val formattedText: String
            get() = "> $text"
    }

    data class Response(override val text: String, override val index: Long) : MessageItem {
        override val color = COLOR_AI
        override val name: String = "Adeptus Lovecraft"

        override val formattedText: String
            get() = text
    }
}