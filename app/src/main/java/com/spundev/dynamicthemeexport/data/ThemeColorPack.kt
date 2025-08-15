package com.spundev.dynamicthemeexport.data

import androidx.compose.material3.ColorScheme
import com.spundev.dynamicthemeexport.ext.toColorStringMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simple data class that holds both colorSchemes (light/dark). This can be used in screens that
 * need both color schemes like the preview table or the export screen.
 */
data class ThemeColorPack(
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
) {

    suspend fun toComposeThemeFile(
        colorFormat: ColorFormat
    ): String = withContext(Dispatchers.Default) {
        buildString {
            // Light theme
            appendLine("val light = lightColorScheme(")
            lightColorScheme.toColorStringMap(colorFormat).forEach { (key, value) ->
                appendLine("    $key = $value,")
            }
            appendLine(")")

            appendLine()

            // Dark theme
            appendLine("val dark = darkColorScheme(")
            darkColorScheme.toColorStringMap(colorFormat).forEach { (key, value) ->
                appendLine("    $key = $value,")
            }
            appendLine(")")
        }
    }

    fun toViewsThemeFile() {
        throw NotImplementedError()
    }
}
