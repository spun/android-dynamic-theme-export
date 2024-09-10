package com.spundev.dynamicthemeexport.ui.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Block for Fixed accent colors
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorBlockWithFixedAccent(
    fixedText: String,
    fixedColor: Color,
    fixedDimText: String,
    fixedDimColor: Color,
    onFixedText: String,
    onFixedColor: Color,
    onFixedVariantText: String,
    onFixedVariantColor: Color,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: ColorBlockStyle = DefaultColorBlockStyle
) {
    Column(modifier = modifier
        .combinedClickable(
            onClick = { },
            onLongClick = {
                val formatter = ColorBlockCopyFormatter
                buildString {
                    appendLine("$fixedText: ${formatter(fixedColor)}")
                    appendLine("$fixedDimText: ${formatter(fixedDimColor)}")
                    appendLine("$onFixedText: ${formatter(onFixedColor)}")
                    appendLine("$onFixedVariantText: ${formatter(onFixedVariantColor)}")
                }.let(onCopy)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(style.bigCellHeight)
        ) {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(fixedColor)
                    .padding(style.contentPadding)
            ) {
                Text(
                    text = fixedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = onFixedColor
                )
            }

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(fixedDimColor)
                    .padding(style.contentPadding)
            ) {
                Text(
                    text = fixedDimText,
                    style = MaterialTheme.typography.bodySmall,
                    color = onFixedColor
                )
            }
        }

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(style.smallCellHeight)
                .background(onFixedColor)
                .padding(style.contentPadding)
        ) {
            Text(
                text = onFixedText, style = MaterialTheme.typography.bodySmall, color = fixedColor
            )
        }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(style.smallCellHeight)
                .background(onFixedVariantColor)
                .padding(style.contentPadding)
        ) {
            Text(
                text = onFixedVariantText,
                style = MaterialTheme.typography.bodySmall,
                color = fixedColor
            )
        }
    }
}

@Preview
@Composable
private fun ColorBlockWithFixedAccentPreview() {
    ColorBlockWithFixedAccent(
        fixedText = "Fixed",
        fixedColor = Color(red = 0xFF, green = 0xDD, blue = 0xB6),
        fixedDimText = "Fixed Dim",
        fixedDimColor = Color(red = 0xFD, green = 0xB9, blue = 0x67),
        onFixedText = "onFixed",
        onFixedColor = Color(red = 0x2B, green = 0x17, blue = 0x00),
        onFixedVariantText = "onFixedVariant",
        onFixedVariantColor = Color(red = 0x85, green = 0x53, blue = 0x04),
        onCopy = {},
        modifier = Modifier.width(180.dp)
    )
}