package io.chthonic.mechanicuslovecraft.presentation.console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.chthonic.mechanicuslovecraft.domain.presentationapi.openai.TestOpenAiUseCase
import io.chthonic.mechanicuslovecraft.domain.presentationapi.models.InputString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val UNKNOWN_COMMAND = "unknown command"
private const val NO_TRANS = "no transaction"
private const val KEY_NOT_SET = "key not set"

private const val COLOR_CHEVRON = "#FAFA91"
private const val COLOR_ERROR = "#FFB0E5"
private const val COLOR_NON_ERROR = "#9FFF99"

@HiltViewModel
internal class ConsoleViewModel constructor(
    private val testOpenAiUseCase: TestOpenAiUseCase,
    initStateState: State,
) : ViewModel() {

    @Inject
    constructor(testOpenAiUseCase: TestOpenAiUseCase) : this(
        testOpenAiUseCase,
        State(),
    )

    data class State(
        val inputTextToDisplay: String = "",
        val inputSubmitEnabled: Boolean = true,
        val history: List<HistoryItem> = emptyList()
    ) {
        val historyToDisplay: String by lazy {
            history.joinToString(
                separator = "<br>",
            )
        }
    }

    private val _state = MutableStateFlow(initStateState)
    val state: StateFlow<State> = _state.asStateFlow()

    fun onTextChanged(text: String) {
        _state.value = state.value.copy(inputTextToDisplay = text)
    }

    fun onInputSubmitted() {
        val currentSate = state.value
        if (!currentSate.inputSubmitEnabled) return
        InputString.validateOrNull(currentSate.inputTextToDisplay)?.let { input ->
            _state.value = currentSate.copy(
                inputTextToDisplay = "",
                inputSubmitEnabled = false,
                history = currentSate.history.append(HistoryItem.InputHistory(input.text))
            )
            executeCommandLineInput(input)
        }
    }

    private fun executeCommandLineInput(input: InputString) {
        viewModelScope.launch {
            testOpenAiUseCase.execute()
//            val history = state.value.history
//            val updatedHistory = try {
//                executeCommandLineInputUseCase.execute(input)?.let {
//                    history.append(HistoryItem.OutputHistory(it, isError = false))
//                }
//            } catch (e: UnknownCommandException) {
//                history.append(HistoryItem.OutputHistory(UNKNOWN_COMMAND))
//            } catch (e: NoTransactionException) {
//                history.append(HistoryItem.OutputHistory(NO_TRANS))
//            } catch (e: KeyNotSetException) {
//                history.append(HistoryItem.OutputHistory(KEY_NOT_SET))
//            } catch (e: Exception) {
//                Log.e("TerminalViewModel", "executeCommandUseCase failed", e)
//                null
//            }
//            _state.value = state.value.copy(
//                inputSubmitEnabled = true,
//                history = updatedHistory ?: history
//            )
        }
    }

    private fun List<HistoryItem>.append(historyItem: HistoryItem): List<HistoryItem> =
        this.toMutableList().apply {
            add(historyItem)
        }.toList()
}

sealed class HistoryItem(val text: String) {
    class InputHistory(text: String) : HistoryItem(text) {
        override fun toString(): String = "<font color='$COLOR_CHEVRON'>></font> $text"
    }

    class OutputHistory(text: String, val isError: Boolean = true) : HistoryItem(text) {
        override fun toString(): String {
            val color = if (isError) COLOR_ERROR else COLOR_NON_ERROR
            return "<font color='$color'>$text</font>"
        }
    }
}