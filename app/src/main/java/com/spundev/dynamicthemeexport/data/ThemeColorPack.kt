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
 * Contains the two  [AnnotatedString] necessary to display a [ThemeColorPack] in our export screen.
 * @param code The styled code that can be copied into a project.
 * @param gutter The preview colors that will be overlaid with the code to add visual information
 *  like in an IDE gutter.
 */
data class ThemeExport(
    val code: AnnotatedString,
    val gutter: AnnotatedString
) {
    companion object {
        val Empty = ThemeExport(
            code = AnnotatedString(""),
            gutter = AnnotatedString("")
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
    suspend fun toComposeThemeExport(
        colorFormat: ColorFormat,
        isDark: Boolean
    ): ThemeExport = withContext(Dispatchers.Default) {
        val colors = CodeColors.getCodeColors(isDark)
        // List of colorSchemes we are going to use to generate our theme file output and include
        // some custom values required to create them in compose.
        val colorSchemeList = listOf(
            Triple("light", "lightColorScheme", lightColorScheme),
            Triple("dark", "darkColorScheme", darkColorScheme),
        )
        // Use two builders, one for the code and one for gutter with color previews
        val codeBuilder = AnnotatedString.Builder()
        val gutterBuilder = AnnotatedString.Builder()
        colorSchemeList.forEachIndexed { index, (schemeName, schemeFunction, colorScheme) ->
            codeBuilder.apply {
                withStyle(SpanStyle(color = colors.keyword)) { append("val ") }
                withStyle(SpanStyle(color = colors.declarationName)) { append("$schemeName ") }
                withStyle(SpanStyle(color = colors.functionName)) { appendLine("= $schemeFunction(") }
                gutterBuilder.appendLine()
                colorScheme.toFormattedColors(colorFormat).forEach { formattedColor ->
                    val colorName = formattedColor.colorName
                    val colorValue = formattedColor.color
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
                    gutterBuilder.withStyle(SpanStyle(colorValue)) { appendLine("▌") }
                }
                withStyle(SpanStyle(color = colors.functionName)) { append(")") }
                if (index < colorSchemeList.lastIndex) {
                    appendLine()
                    appendLine()
                    gutterBuilder.apply {
                        appendLine()
                        appendLine()
                    }
                }
            }
        }
        ThemeExport(
            code = codeBuilder.toAnnotatedString(),
            gutter = gutterBuilder.toAnnotatedString()
        )
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
