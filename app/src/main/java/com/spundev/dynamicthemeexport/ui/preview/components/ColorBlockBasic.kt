package com.spundev.dynamicthemeexport.ui.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorBlockBasic(
    text: String,
    color: Color,
    onColor: Color,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: ColorBlockStyle = DefaultColorBlockStyle
) {
    Box(
        modifier = modifier
            .height(style.bigCellHeight)
            .background(color)
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    val colorString = ColorBlockCopyFormatter(color)
                    onCopy("$text: $colorString")
                }
            )
            .padding(style.contentPadding)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = onColor
        )
    }
}

@Preview
@Composable
private fun ColorBlockBasicPreview() {
    ColorBlockBasic(
        text = "Basic",
        color = Color(red = 0xFF, green = 0xDD, blue = 0xB6),
        onColor = Color(red = 0x2B, green = 0x17, blue = 0x00),
        onCopy = {},
        modifier = Modifier.width(180.dp)
    )
}

@Preview
@Composable
private fun ColorBlockBasicSmallPreview() {
    ColorBlockBasic(
        text = "Basic small",
        color = Color(red = 0xFF, green = 0xDD, blue = 0xB6),
        onColor = Color(red = 0x2B, green = 0x17, blue = 0x00),
        onCopy = {},
        style = ForceSmallColorBlockStyle,
        modifier = Modifier.width(180.dp)
    )
}
