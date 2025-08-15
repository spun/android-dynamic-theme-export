package com.spundev.dynamicthemeexport.ui.preview.components

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

/**
 * Styling configuration for a `ColorBlock`.
 */
@Immutable
data class ColorBlockStyle internal constructor(
    val contentPadding: Dp,
    val smallCellHeight: Dp,
    val bigCellHeight: Dp,
) {
    companion object {
        /**
         * Constant for default ColorBlock style.
         */
        @Stable
        internal val Default = ColorBlockStyle(
            contentPadding = 8.dp,
            smallCellHeight = 32.dp,
            bigCellHeight = 64.dp,
        )
    }
}

val DefaultColorBlockStyle = ColorBlockStyle.Default

/**
 * [DefaultColorBlockStyle] that limits the [ColorBlockStyle.bigCellHeight] to be the same as the
 * [ColorBlockStyle.smallCellHeight].
 * NOTE: This [ColorBlockStyle] is intended to be used in a [ColorBlockBasic].
 */
val ForceSmallColorBlockStyle = ColorBlockStyle.Default.copy(
    bigCellHeight = ColorBlockStyle.Default.smallCellHeight
)

/**
 * Formatter used to convert a [Color] to a hex string when a ColorBlock is selected.
 */
@OptIn(ExperimentalStdlibApi::class)
val ColorBlockCopyFormatter: (Color) -> String = {
    val color = it.toArgb()
    val customHexFormat = HexFormat {
        upperCase = true
        number.removeLeadingZeros = true
    }
    val r = color.red.toHexString(customHexFormat).padStart(2, '0')
    val g = color.green.toHexString(customHexFormat).padStart(2, '0')
    val b = color.blue.toHexString(customHexFormat).padStart(2, '0')
    "#$r$g$b"
}
