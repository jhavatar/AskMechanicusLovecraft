package io.chthonic.mechanicuslovecraft.presentation.console.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.chthonic.mechanicuslovecraft.presentation.console.MessageItem

private const val TEXT_SIZE = 16
private val COLOR_ERROR = Color(0xFF900C3F)

@Composable
fun MessageItemView(
    messageItem: MessageItem,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .padding(PaddingValues(start = 16.dp, top = 8.dp, bottom = 4.dp, end = 8.dp))
            .fillMaxWidth()
    ) {
        Text(
            text = "${messageItem.name}:",
            fontSize = TEXT_SIZE.sp,
            color = Color(messageItem.color),
            fontWeight = FontWeight.Bold,
        )
        SelectionContainer {
            Text(
                text = messageItem.formattedText,
                fontSize = TEXT_SIZE.sp,
                color = if (messageItem.showError) COLOR_ERROR else Color(messageItem.color),
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Text(
            text = "${messageItem.index + 1}",
            fontSize = 8.sp,
            color = Color(messageItem.color),
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Preview
@Composable
private fun PreviewInputMessageItemView() {
    MessageItemView(
        messageItem = MessageItem.Input(
            text = "Hello World?",
            index = 50L,
        )
    ) {}
}

@Preview
@Composable
private fun PreviewResponseMessageItemView() {
    MessageItemView(
        messageItem = MessageItem.Response(
            text = "Hello World?",
            index = 51L,
            showError = false,
        )
    ) {}
}