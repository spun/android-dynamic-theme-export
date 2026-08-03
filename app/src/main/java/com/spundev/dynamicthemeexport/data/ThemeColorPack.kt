package com.spundev.dynamicthemeexport.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.spundev.dynamicthemeexport.ext.toFormattedColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Set of colors we will use to style the Annotated text with the theme code.
 */
private data class CodeColors(
    val keyword: Color,
    val declarationName: Color,
    val functionName: Color,
    val parameterName: Color,
    val parameter: Color,
    val comment: Color,
) {
    companion object {
        fun getCodeColors(isDarkTheme: Boolean) = if (isDarkTheme) dark else light

        private val light = CodeColors(
            keyword = Color(0xFFCC5B12),
            declarationName = Color(0xFFA632C2),
            functionName = Color(0xFF4D4D4D),
            parameterName = Color(0xFF0077CC),
            parameter = Color(0xFF0088AA),
            comment = Color(0xFF5A8FA8)
        )

        private val dark = CodeColors(
            keyword = Color(0XFFEC823C),
            declarationName = Color(0XFFEC93FC),
            functionName = Color(0XFFEBEBEB),
            parameterName = Color(0XFF00AAFF),
            parameter = Color(0XFF33CCFF),
            comment = Color(0XFF7CC2E5)
        )
    }
}

/**
 * Simple data class that holds both colorSchemes (light/dark). This can be used in screens that
 * need both color schemes like the export screen.
 */
data class ThemeColorPack(
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
) {
    suspend fun toComposeThemeFile(
        colorFormat: ColorFormat,
        isDark: Boolean
    ): AnnotatedString = withContext(Dispatchers.Default) {
        val colors = CodeColors.getCodeColors(isDark)
        // List of colorSchemes we are going to use to generate our theme file output and include
        // some custom values required to create them in compose.
        val colorSchemeList = listOf(
            Triple("light", "lightColorScheme", lightColorScheme),
            Triple("dark", "darkColorScheme", darkColorScheme),
        )
        buildAnnotatedString {
            colorSchemeList.forEachIndexed { index, (schemeName, schemeFunction, colorScheme) ->
                withStyle(SpanStyle(color = colors.keyword)) { append("val ") }
                withStyle(SpanStyle(color = colors.declarationName)) { append("$schemeName ") }
                withStyle(SpanStyle(color = colors.functionName)) { appendLine("= $schemeFunction(") }
                colorScheme.toFormattedColors(colorFormat).forEach { formattedColor ->
                    val colorName = formattedColor.colorName
                    val colorOutput = formattedColor.output
                    val isEnabled = formattedColor.enabled
                    if (isEnabled) {
                        withStyle(SpanStyle(color = colors.parameterName)) { append("   $colorName = ") }
                        withStyle(SpanStyle(color = colors.functionName)) { append("Color(") }
                        withStyle(SpanStyle(color = colors.parameter)) { append(colorOutput) }
                        withStyle(SpanStyle(color = colors.functionName)) { appendLine("),") }
                    } else {
                        withStyle(SpanStyle(color = colors.comment)) { appendLine("   // $colorName = Color($colorOutput),") }
                    }
                }
                withStyle(SpanStyle(color = colors.functionName)) { append(")") }
                if (index < colorSchemeList.lastIndex) {
                    appendLine()
                    appendLine()
                }
            }
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
