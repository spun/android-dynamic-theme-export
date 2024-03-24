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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.R
import com.spundev.dynamicthemeexport.data.ThemeColorPack
import com.spundev.dynamicthemeexport.ui.export.ExportScreen
import com.spundev.dynamicthemeexport.ui.preview.ColorRolesTable
import com.spundev.dynamicthemeexport.ui.theme.DynamicExportTheme

@Composable
fun MainScreen() {

    // Start with the current system theme and allow theme changes
    val initialDarkThemeValue = isSystemInDarkTheme()
    var isDarkThemeSelected by rememberSaveable { mutableStateOf(initialDarkThemeValue) }

    // We are getting the light and dark dynamic themes here instead of inside DynamicExportTheme
    // composable because the "Color roles" table and the export screen need both colorSchemes.
    val context = LocalContext.current
    val themeColorPack = remember(context) {
        ThemeColorPack(
            lightColorScheme = dynamicLightColorScheme(context),
            darkColorScheme = dynamicDarkColorScheme(context)
        )
    }

    DynamicExportTheme(
        darkTheme = isDarkThemeSelected,
        themeColorPack = themeColorPack,
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.systemBarsPadding()) {

                var selectedScreenIndex by rememberSaveable { mutableIntStateOf(0) }
                MainTopBar(
                    isDarkThemeSelected = isDarkThemeSelected,
                    selectedScreenIndex = selectedScreenIndex,
                    onThemeChange = { isDarkThemeSelected = it },
                    onScreenSelectionChange = { selectedScreenIndex = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                HorizontalDivider()

                // Screen content
                when (selectedScreenIndex) {
                    0 -> ColorRolesTable(themeColorPack = themeColorPack)
                    1 -> ExportScreen(themeColorPack = themeColorPack)
                    else -> Text("Unknown")
                }
            }
        }
    }
}

@Composable
private fun MainTopBar(
    isDarkThemeSelected: Boolean,
    selectedScreenIndex: Int,
    onThemeChange: (isDarkSelected: Boolean) -> Unit,
    onScreenSelectionChange: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ScreenContentSelector(
            selectedIndex = selectedScreenIndex,
            onSelectionChange = onScreenSelectionChange
        )

        LightDarkSelector(
            isDarkThemeSelected = isDarkThemeSelected,
            onThemeChange = onThemeChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContentSelector(
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
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
                onClick = { onSelectionChange(index) },
                selected = index == selectedIndex
            ) {
                Text(label)
            }
        }
    }
}

@Composable
private fun LightDarkSelector(
    isDarkThemeSelected: Boolean,
    onThemeChange: (isDarkSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconDrawableRes = if (isDarkThemeSelected) {
        R.drawable.ic_dark_mode_24
    } else {
        R.drawable.ic_light_mode_24
    }

    Switch(
        checked = !isDarkThemeSelected,
        onCheckedChange = { onThemeChange(!it) },
        thumbContent = {
            // Icon isn't focusable, no need for content description
            Icon(
                painter = painterResource(iconDrawableRes),
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        },
        colors = SwitchDefaults.colors().copy(
            // Use same values from checked
            uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
            uncheckedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedBorderColor = Color.Transparent,
            uncheckedIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}
