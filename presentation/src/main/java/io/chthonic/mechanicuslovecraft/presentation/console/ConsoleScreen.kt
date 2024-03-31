package io.chthonic.mechanicuslovecraft.presentation.console

import android.graphics.Color.WHITE
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import io.chthonic.mechanicuslovecraft.presentation.R
import io.chthonic.mechanicuslovecraft.presentation.ktx.collectAsStateLifecycleAware
import io.chthonic.mechanicuslovecraft.presentation.theme.AppTheme
import io.chthonic.mechanicuslovecraft.presentation.theme.DraculaBlack
import io.chthonic.mechanicuslovecraft.presentation.theme.DraculaDarkerPurple

private const val SMALL_MARGIN = 8
private const val BIG_MARGIN = 8
private const val TEXT_SIZE = 16

@Composable
internal fun ConsoleScreen(
    viewModel: ConsoleViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateLifecycleAware(
        initial = ConsoleViewModel.State(),
        scope = viewModel.viewModelScope
    ).value
    TerminalContent(
        state,
        onTextChanged = viewModel::onTextChanged,
        onInputSubmitted = viewModel::onInputSubmitted
    )
}

@Preview
@Composable
private fun PreviewTerminalContent() {
    AppTheme(isDarkTheme = false) {
        TerminalContent(
            ConsoleViewModel.State(
                history = listOf(
                    HistoryItem.InputHistory("GET X"),
                    HistoryItem.OutputHistory("123", isError = false),
                    HistoryItem.InputHistory("Get Y"),
                    HistoryItem.OutputHistory("key not set", isError = true)
                ),
                inputTextToDisplay = "",
                inputSubmitEnabled = true
            ),
            onTextChanged = {},
            onInputSubmitted = {}
        )
    }
}

@Composable
private fun TerminalContent(
    state: ConsoleViewModel.State,
    onTextChanged: (String) -> Unit,
    onInputSubmitted: () -> Unit
) {
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
            state.historyToDisplay,
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
private fun OutputView(historyHtml: String, modifier: Modifier) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        HtmlText(
            html = historyHtml,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(
                    top = BIG_MARGIN.dp,
                    bottom = BIG_MARGIN.dp,
                    start = SMALL_MARGIN.dp,
                    end = SMALL_MARGIN.dp
                )
        )
    }
    LaunchedEffect(scrollState.maxValue) {
        // scroll to bottom of output
        scrollState.scrollTo(scrollState.maxValue)
    }
}

@Composable
private fun SubmitButton(
    inputSubmitEnabled: Boolean,
    modifier: Modifier,
    onInputSubmitted: () -> Unit
) {
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
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .focusable(true)
            .focusRequester(focusRequester)
    )
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                setTextColor(WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE.sp.value)
                setLineSpacing(0f, 1.2f)
            }
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}