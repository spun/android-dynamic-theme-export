package com.spundev.dynamicthemeexport.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers.BLUE_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.GREEN_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.RED_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.YELLOW_DOMINATED_EXAMPLE
import com.android.tools.screenshot.PreviewTest
import com.spundev.dynamicthemeexport.data.ThemeColorPack
import com.spundev.dynamicthemeexport.ui.preview.ColorRolesTable
import com.spundev.dynamicthemeexport.ui.theme.DynamicExportTheme

// Device size that fits the full preview table
private const val PREVIEW_PERFECT_FIT = "spec:width=780dp,height=810dp,dpi=160"

@PreviewTest
@Preview(
    name = "Red",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = RED_DOMINATED_EXAMPLE
)
@Preview(
    name = "Red - Dark",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = RED_DOMINATED_EXAMPLE,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Blue",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = BLUE_DOMINATED_EXAMPLE
)
@Preview(
    name = "Blue - Dark",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = BLUE_DOMINATED_EXAMPLE,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Green",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = GREEN_DOMINATED_EXAMPLE
)
@Preview(
    name = "Green - Dark",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = GREEN_DOMINATED_EXAMPLE,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Yellow",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = YELLOW_DOMINATED_EXAMPLE
)
@Preview(
    name = "Yellow - Dark",
    device = PREVIEW_PERFECT_FIT,
    wallpaper = YELLOW_DOMINATED_EXAMPLE,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, backgroundColor = 0xFF000000
)
@Composable
fun PreviewScreenDynamicColorsPreview() {
    val context = LocalContext.current
    val themeColorPack = ThemeColorPack(
        lightColorScheme = dynamicLightColorScheme(context),
        darkColorScheme = dynamicDarkColorScheme(context)
    )
    DynamicExportTheme(themeColorPack = themeColorPack) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            ColorRolesTable()
        }
    }
}
