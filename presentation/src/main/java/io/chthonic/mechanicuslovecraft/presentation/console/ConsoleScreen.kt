package io.chthonic.mechanicuslovecraft.presentation.console

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.chthonic.mechanicuslovecraft.presentation.R
import io.chthonic.mechanicuslovecraft.presentation.console.widgets.MessageItemView
import io.chthonic.mechanicuslovecraft.presentation.ktx.collectAsStateLifecycleAware
import io.chthonic.mechanicuslovecraft.presentation.ktx.items
import io.chthonic.mechanicuslovecraft.presentation.theme.DraculaBlack
import io.chthonic.mechanicuslovecraft.presentation.theme.DraculaDarkerPurple
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

private const val TEXT_SIZE = 16
private const val SMALL_MARGIN = 8
private const val BIG_MARGIN = 8

@Composable
internal fun ConsoleScreen(
    viewModel: ConsoleViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateLifecycleAware(
        initial = ConsoleViewModel.State(),
        scope = viewModel.viewModelScope
    ).value
    Timber.v("D3V ConsoleScreen, state = $state")
    TerminalContent(
        viewModel.viewModelScope.coroutineContext,
        state,
        onTextChanged = viewModel::onTextChanged,
        onInputSubmitted = viewModel::onInputSubmitted
    )
}

//@Preview
//@Composable
//private fun PreviewTerminalContent() {
//    AppTheme(isDarkTheme = false) {
//        TerminalContent(
//            ConsoleViewModel.State(
////                history = listOf(
////                    HistoryItem.InputHistory("GET X"),
////                    HistoryItem.OutputHistory("123", isError = false),
////                    HistoryItem.InputHistory("Get Y"),
////                    HistoryItem.OutputHistory("key not set", isError = true)
////                ),
//                inputTextToDisplay = "",
//                inputSubmitEnabled = true
//            ),
//            onTextChanged = {},
//            onInputSubmitted = {}
//        )
//    }
//}

@Composable
private fun TerminalContent(
    coroutineContext: CoroutineContext,
    state: ConsoleViewModel.State,
    onTextChanged: (String) -> Unit,
    onInputSubmitted: () -> Unit,
) {
    Timber.v("D3V: TerminalContent")
    val inputBarMargin = SMALL_MARGIN.dp
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (output, submitButton, inputText, inputBar) = createRefs()
        val barrier = createTopBarrier(submitButton, inputText, inputBar)

        // the bottom input bar
        Box(
            Modifier
                .constrainAs(inputBar) {
                    top.linkTo(barrier)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .defaultMinSize(minWidth = Dp.Unspecified, minHeight = 56.dp)
                .background(DraculaDarkerPurple)
                .fillMaxWidth()
        )

        SubmitButton(
            state.inputSubmitEnabled,
            Modifier.constrainAs(submitButton) {
                top.linkTo(barrier, inputBarMargin)
                bottom.linkTo(parent.bottom, inputBarMargin)
                end.linkTo(parent.end, inputBarMargin)
            },
            onInputSubmitted
        )

        InputText(
            state.inputTextToDisplay,
            state.inputSubmitEnabled,
            Modifier.constrainAs(inputText) {
                top.linkTo(barrier, inputBarMargin)
                bottom.linkTo(parent.bottom, inputBarMargin)
                start.linkTo(parent.start, inputBarMargin)
                end.linkTo(submitButton.start, inputBarMargin)
                width = Dimension.fillToConstraints
            },
            onTextChanged,
            onInputSubmitted
        )

        OutputView(
            state.messages.collectAsStateLifecycleAware(
                initial = emptyList(),
                context = coroutineContext,
            ).value,
            Modifier.constrainAs(output) {
                top.linkTo(parent.top)
                bottom.linkTo(inputBar.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
private fun OutputView(
    lazyItems: List<MessageItem>,
    modifier: Modifier
) {
    Timber.v("D3V:OutputView, size = ${lazyItems.size}")
    val lazyColumnListState = rememberLazyListState()
    LazyColumn(
        reverseLayout = true,
        state = lazyColumnListState,
        modifier = modifier
            .background(DraculaBlack)
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(
                top = BIG_MARGIN.dp,
                bottom = BIG_MARGIN.dp,
                start = SMALL_MARGIN.dp,
                end = SMALL_MARGIN.dp
            )
    ) {
        items(
            lazyItems,
            itemKey = { it.index },
        ) { item ->
            item?.let {
                MessageItemView(it) { }
            }
        }
    }

    LaunchedEffect(lazyItems.size) {
        // auto scroll to bottom of output
        if (lazyItems.isNotEmpty()) {
            lazyColumnListState.scrollToItem(0)
        }
    }
}

@Composable
private fun SubmitButton(
    inputSubmitEnabled: Boolean,
    modifier: Modifier,
    onInputSubmitted: () -> Unit
) {
    Timber.v("D3V: SubmitButton enabled = $inputSubmitEnabled")
    Button(
        onClick = onInputSubmitted,
        modifier = modifier,
        enabled = inputSubmitEnabled
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_keyboard_return_24),
            contentDescription = "Enter"
        )
    }
}

@Composable
private fun InputText(
    textToDisplay: String,
    inputSubmitEnabled: Boolean,
    modifier: Modifier,
    onTextChanged: (String) -> Unit,
    onInputSubmitted: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    TextField(
        value = textToDisplay,
        onValueChange = onTextChanged,
        enabled = inputSubmitEnabled,
        placeholder = { Text(stringResource(id = R.string.input_hint)) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { onInputSubmitted() }
        ),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            disabledTextColor = Color.Transparent,
            backgroundColor = DraculaBlack,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        textStyle = TextStyle.Default.copy(
            fontSize = TEXT_SIZE.sp,
        ),
        singleLine = false,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .focusable(true)
            .focusRequester(focusRequester)
    )
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
}