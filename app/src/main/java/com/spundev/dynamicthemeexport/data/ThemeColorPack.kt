package com.spundev.dynamicthemeexport.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.spundev.dynamicthemeexport.ext.toColorStringMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simple data class that holds both colorSchemes (light/dark). This can be used in screens that
 * need both color schemes like the export screen.
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

    fun getColorScheme(darkTheme: Boolean): ColorScheme =
        if (darkTheme) darkColorScheme else lightColorScheme
}

/**
 * Create a ThemeColorPack
 */
@Composable
fun rememberThemeColorPack(): ThemeColorPack {
    val context = LocalContext.current
    return remember(context) {
        ThemeColorPack(
            lightColorScheme = dynamicLightColorScheme(context),
            darkColorScheme = dynamicDarkColorScheme(context)
        )
    }
}
