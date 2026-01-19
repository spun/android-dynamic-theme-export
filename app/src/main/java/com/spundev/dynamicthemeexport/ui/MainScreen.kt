package com.spundev.dynamicthemeexport.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import com.spundev.dynamicthemeexport.ui.preview.ColorRolesTable

@Composable
fun MainScreen(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    themeColorPack: ThemeColorPack,
    onDarkThemeChange: (isDarkTheme: Boolean) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.systemBarsPadding()) {

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
                0 -> ColorRolesTable(themeColorPack = themeColorPack)
                1 -> ExportScreen(themeColorPack = themeColorPack)
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
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
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

    val switchDefaultsColors = SwitchDefaults.colors()
    val switchColors = switchDefaultsColors.copy(
        // Use same values from checked
        uncheckedThumbColor = switchDefaultsColors.checkedThumbColor,
        uncheckedTrackColor = switchDefaultsColors.checkedTrackColor,
        uncheckedBorderColor = switchDefaultsColors.checkedBorderColor,
        // We use checkedTrackColor for the iconColor to fix an issue with
        // dynamic dark color schemes on devices running API 36.1 where
        // the default colors might not have enough contrast (b/462919296).
        checkedIconColor = switchDefaultsColors.checkedTrackColor,
        uncheckedIconColor = switchDefaultsColors.checkedTrackColor,
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
