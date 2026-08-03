package com.spundev.dynamicthemeexport.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.R
import com.spundev.dynamicthemeexport.data.ThemeColorPack
import com.spundev.dynamicthemeexport.ui.export.ExportScreen
import com.spundev.dynamicthemeexport.ui.preview.PreviewGridScreen

@Composable
fun MainScreen(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    themeColorPack: ThemeColorPack,
    onDarkThemeChange: (isDarkTheme: Boolean) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            var currentScreenIndex by rememberSaveable { mutableIntStateOf(0) }
            MainTopBar(
                isDarkTheme = isDarkTheme,
                currentScreenIndex = currentScreenIndex,
                onDarkThemeChange = onDarkThemeChange,
                onCurrentScreenChange = { currentScreenIndex = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            HorizontalDivider()

            // Screen content
            when (currentScreenIndex) {
                0 -> PreviewGridScreen()
                1 -> ExportScreen(
                    themeColorPack = themeColorPack,
                    isDarkTheme = isDarkTheme,
                )

                else -> Text("Unknown")
            }
        }
    }
}

@Composable
private fun MainTopBar(
    isDarkTheme: Boolean,
    currentScreenIndex: Int,
    onDarkThemeChange: (isDarkTheme: Boolean) -> Unit,
    onCurrentScreenChange: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarInset = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.windowInsetsPadding(topBarInset)
    ) {
        ScreenContentSelector(
            currentScreenIndex = currentScreenIndex,
            onCurrentScreenChange = onCurrentScreenChange
        )

        LightDarkSelector(
            isDarkTheme = isDarkTheme,
            onDarkThemeChange = onDarkThemeChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContentSelector(
    currentScreenIndex: Int,
    onCurrentScreenChange: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Preview", "Export")
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onCurrentScreenChange(index) },
                selected = index == currentScreenIndex
            ) {
                Text(label)
            }
        }
    }
}

@Composable
private fun LightDarkSelector(
    isDarkTheme: Boolean,
    onDarkThemeChange: (isDarkTheme: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconDrawableRes = if (isDarkTheme) {
        R.drawable.ic_dark_mode_24
    } else {
        R.drawable.ic_light_mode_24
    }

    val is36dot1 = Build.VERSION.SDK_INT_FULL == Build.VERSION_CODES_FULL.BAKLAVA_1
    val switchDefaultsColors = SwitchDefaults.colors()
    val switchColors = switchDefaultsColors.copy(
        // Use same values from checked
        uncheckedThumbColor = switchDefaultsColors.checkedThumbColor,
        uncheckedTrackColor = switchDefaultsColors.checkedTrackColor,
        uncheckedBorderColor = switchDefaultsColors.checkedBorderColor,
        // We use checkedTrackColor for the iconColor to fix an issue with dynamic dark color
        // schemes on devices running API 36.1 where the generated colors might not have enough
        // contrast between the thumb (OnPrimary) and icon (OnPrimaryContainer). See b/462919296.
        // NOTE: This is fixed/reverted on API >= 37, so we only need to change it for 36.1
        uncheckedIconColor = if (is36dot1) switchDefaultsColors.checkedTrackColor else switchDefaultsColors.checkedIconColor,
        // The checked state indicates that a light color scheme is active. We've only seen issues
        // with dark color schemes, so this workaround shouldn't be necessary, but added it anyway
        // to be safe.
        checkedIconColor = if (is36dot1) switchDefaultsColors.checkedTrackColor else switchDefaultsColors.checkedIconColor,
    )

    Switch(
        checked = !isDarkTheme,
        onCheckedChange = { onDarkThemeChange(!it) },
        thumbContent = {
            // Icon isn't focusable, no need for content description
            Icon(
                painter = painterResource(iconDrawableRes),
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        },
        colors = switchColors,
        modifier = modifier
    )
}
