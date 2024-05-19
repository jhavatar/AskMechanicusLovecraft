package io.chthonic.mechanicuslovecraft.presentation.console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import io.chthonic.mechanicuslovecraft.common.valueobjects.Role
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveAllMessagePagedUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveNextAssistantResponseState
import io.chthonic.mechanicuslovecraft.domain.presentationapi.ObserveNextAssistantResponseState.AssistantResponseState
import io.chthonic.mechanicuslovecraft.domain.presentationapi.SubmitMessageAndObserveStreamingResponseUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import io.chthonic.mechanicuslovecraft.presentation.console.ConsoleViewModel.InputCompanionWidget.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val UNKNOWN_COMMAND = "unknown command"
private const val NO_TRANS = "no transaction"
private const val KEY_NOT_SET = "key not set"

private const val COLOR_USER: Long = 0xFFFAFA91

private const val COLOR_AI: Long = 0xFFFA84C6

@HiltViewModel
internal class ConsoleViewModel(
    private val submitMessageAndObserveStreamingResponseUseCase: SubmitMessageAndObserveStreamingResponseUseCase,
    private val observeAllMessagePagedUseCase: ObserveAllMessagePagedUseCase,
    private val observeNextAssistantResponseState: ObserveNextAssistantResponseState,
    initStateState: State,
) : ViewModel() {

    @Inject
    constructor(
        submitMessageAndObserveStreamingResponseUseCase: SubmitMessageAndObserveStreamingResponseUseCase,
        observeAllMessagePagedUseCase: ObserveAllMessagePagedUseCase,
        observeNextAssistantResponseState: ObserveNextAssistantResponseState,
    ) : this(
        submitMessageAndObserveStreamingResponseUseCase,
        observeAllMessagePagedUseCase,
        observeNextAssistantResponseState,
        State(),
    )

    enum class InputCompanionWidget {
        SUBMIT_BUTTON, AI_PROCESSING_VIEW, AI_TALKING_VIEW
    }

    data class State(
        val inputTextToDisplay: String = "",
        val showInputCompanionWidget: InputCompanionWidget = SUBMIT_BUTTON,
        val messages: Flow<PagingData<MessageItem>> = emptyFlow(),
    ) {
        val isInputEnabled: Boolean = showInputCompanionWidget == SUBMIT_BUTTON
    }

    private val _state = MutableStateFlow(initStateState)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = state.value.copy(
                messages = observeAllMessagePagedUseCase.execute()
                    .catch {
                        Timber.e(it, "D3V: observeAllMessagePagedUseCase failed")
                    }
                    .map { pagingData ->
                        pagingData.map {
                            when (it.role) {
                                Role.User -> MessageItem.Input(
                                    index = it.index,
                                    text = it.content,
                                )

                                else -> MessageItem.Response(
                                    index = it.index,
                                    text = (if (it.isError) "⚠\uFE0F " else "") + it.content + (if (!it.isDone) "_" else ""),
                                    showError = it.isError,
                                )
                            }
                        }
                    }.also {
                        Timber.v("D3V: init observeAllMessagePagedUseCase = $it")
                    },
            )
        }
    }

    fun onTextChanged(text: String) {
        _state.value = state.value.copy(inputTextToDisplay = text)
    }

    fun onInputSubmitted() {
        val currentSate = state.value
        if (currentSate.showInputCompanionWidget != SUBMIT_BUTTON) return
        InputString.validateOrNull(currentSate.inputTextToDisplay)?.let { input ->
            _state.value = currentSate.copy(
                inputTextToDisplay = "",
                showInputCompanionWidget = AI_PROCESSING_VIEW,
            )
            observeAiProcessing()
            executeCommandLineInput(input)
        }
    }

    private fun observeAiProcessing() {
        viewModelScope.launch {
            observeNextAssistantResponseState.execute()
                .distinctUntilChanged()
                .catch {
                    Timber.e(it, "observeNextAssistantResponseState failed")
                }
                .collect {
                    _state.value = state.value.copy(
                        showInputCompanionWidget = when (it) {
                            AssistantResponseState.COMPLETED -> SUBMIT_BUTTON
                            AssistantResponseState.RECEIVING -> AI_TALKING_VIEW
                        }
                    )
                }
        }
    }

    private fun executeCommandLineInput(input: InputString) {
        viewModelScope.launch {
            try {
                submitMessageAndObserveStreamingResponseUseCase.execute(input)
            } catch (e: Exception) {
                Timber.e(e, "submitMessageAndObserveStreamingResponseUseCase failed")
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
    val showError: Boolean

    val formattedText: String

    data class Input(
        override val text: String,
        override val index: Long,
        override val name: String = "You",
    ) : MessageItem {
        override val color = COLOR_USER
        override val showError: Boolean = false
        override val formattedText: String
            get() = "> $text"
    }

    data class Response(
        override val text: String,
        override val index: Long,
        override val showError: Boolean,
    ) :
        MessageItem {
        override val color = COLOR_AI
        override val name: String = "Mechanicus Lovecraft"

        override val formattedText: String
            get() = text
    }
}