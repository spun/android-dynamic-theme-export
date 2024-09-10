package com.spundev.dynamicthemeexport.ui.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorBlockPair(
    text: String,
    color: Color,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: ColorBlockStyle = DefaultColorBlockStyle
) {
    val onText = "On $text"
    val onColor = MaterialTheme.colorScheme.contentColorFor(color)

    Column(
        modifier = modifier
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    val formatter = ColorBlockCopyFormatter
                    buildString {
                        appendLine("$text: ${formatter(color)}")
                        appendLine("$onText: ${formatter(onColor)}")
                    }.let(onCopy)
                }
            )
    ) {
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(style.bigCellHeight)
                .background(color)
                .padding(style.contentPadding)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = onColor
            )
        }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(style.smallCellHeight)
                .background(onColor)
                .padding(style.contentPadding)
        ) {
            Text(
                text = onText,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

@Preview
@Composable
private fun ColorBlockPairPreview() {
    ColorBlockPair(
        text = "Simple block",
        color = Color(red = 0xFF, green = 0xDD, blue = 0xB6),
        onCopy = {},
        modifier = Modifier.width(180.dp)
    )
}
