package com.spundev.dynamicthemeexport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.spundev.dynamicthemeexport.data.rememberThemeColorPack
import com.spundev.dynamicthemeexport.ui.MainScreen
import com.spundev.dynamicthemeexport.ui.theme.DynamicExportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Start with the current system theme and allow theme changes
            val initialDarkThemeValue = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable { mutableStateOf(initialDarkThemeValue) }

            // We are getting a "pack" with light and dark dynamic themes here instead of
            // inside DynamicExportTheme composable because our "MainScreen" needs both
            // colorSchemes to populate the export pane.
            val themeColorPack = rememberThemeColorPack()

            // Update the edge to edge configuration to match the theme
            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { isDarkTheme },
                )
                onDispose {}
            }

            DynamicExportTheme(
                // Get the correct ColorScheme for the current isDarkTheme
                // (our light/dark toggle) selected value.
                colorScheme = themeColorPack.getColorScheme(isDarkTheme),
            ) {
                MainScreen(
                    isDarkTheme = isDarkTheme,
                    themeColorPack = themeColorPack,
                    onDarkThemeChange = { isDarkTheme = it }
                )
            }
        }
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

