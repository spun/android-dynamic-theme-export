package com.spundev.dynamicthemeexport.data

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

/**
 * All the supported color creation formats
 */
sealed class ColorFormat(val formatter: (Color) -> String) {

    /**
     * Color(red = 1f, green = 1f, blue = 1f)
     */
    data object FloatComponents : ColorFormat({ color ->
        "Color(red = ${color.red}f, green = ${color.green}f, blue = ${color.blue}f)"
    })

    /**
     * Color(0xFF0000FF)
     */
    @OptIn(ExperimentalStdlibApi::class)
    data object SRGBInteger : ColorFormat({ color ->
        "Color(0x${color.toArgb().toHexString(HexFormat.UpperCase)})"
    })

    /**
     * Color(red = 0xFF, green = 0xFF, blue = 0xFF)
     */
    @OptIn(ExperimentalStdlibApi::class)
    data object IntegerComponentsHex : ColorFormat({
        val color = it.toArgb()
        val customHexFormat = HexFormat {
            upperCase = true
            number.removeLeadingZeros = true
        }
        "Color(" +
            "red = 0x${color.red.toHexString(customHexFormat).padStart(2, '0')}, " +
            "green = 0x${color.green.toHexString(customHexFormat).padStart(2, '0')}, " +
            "blue = 0x${color.blue.toHexString(customHexFormat).padStart(2, '0')}" +
        ")"
    })

    /**
     * Color(red = 0xFF, green = 0xFF, blue = 0xFF)
     */
    data object IntegerComponents : ColorFormat({
        val color = it.toArgb()
        "Color(red = ${color.red}, green = ${color.green}, blue = ${color.blue})"
    })
}

/**
 * A custom [Saver] for the [ColorFormat] sealed class that allows saving and restoring a
 * [ColorFormat] across configuration changes.
 *
 * This saver converts the [ColorFormat] instance into a [String] representation when saving
 * and reconstructs the [ColorFormat] instance from the [String] when restoring.
 *
 * Usage:
 * ```
 * var colorFormat: ColorFormat by rememberSaveable(stateSaver = ColorFormatSaver) {
 *     mutableStateOf(ColorFormat.SRGBInteger)
 * }
 * ```
 *
 * @throws IllegalArgumentException if the [String] does not correspond to any known [ColorFormat].
 */
val ColorFormatSaver = Saver<ColorFormat, String>(
    save = {
        when (it) {
            ColorFormat.FloatComponents -> "FloatComponents"
            ColorFormat.IntegerComponents -> "IntegerComponents"
            ColorFormat.IntegerComponentsHex -> "IntegerComponentsHex"
            ColorFormat.SRGBInteger -> "SRGBInteger"
        }
    },
    restore = {
        when (it) {
            "FloatComponents" -> ColorFormat.FloatComponents
            "IntegerComponents" -> ColorFormat.IntegerComponents
            "IntegerComponentsHex" -> ColorFormat.IntegerComponentsHex
            "SRGBInteger" -> ColorFormat.SRGBInteger
            else -> throw IllegalArgumentException("Unknown ColorFormat type")
        }
    }
)
