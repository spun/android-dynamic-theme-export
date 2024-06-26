package com.spundev.dynamicthemeexport.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.spundev.dynamicthemeexport.data.ThemeColorPack


@Composable
fun DynamicExportTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColorPack: ThemeColorPack,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) themeColorPack.darkColorScheme else themeColorPack.lightColorScheme,
        typography = Typography,
        content = content
    )
}