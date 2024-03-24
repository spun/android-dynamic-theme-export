package com.spundev.dynamicthemeexport.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.data.ThemeColorPack
import com.spundev.dynamicthemeexport.ui.theme.DynamicExportTheme
import com.spundev.dynamicthemeexport.util.freeScroll.freeScrollWithTransformGesture
import com.spundev.dynamicthemeexport.util.freeScroll.rememberFreeScrollState

// From ColorSchemeFixedAccentColorSample
// Source: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material3/material3/samples/src/main/java/androidx/compose/material3/samples/ColorSchemeSamples.kt;l=32;bpv=0;bpt=0
private data class FixedAccentColors(
    val primaryFixed: Color,
    val onPrimaryFixed: Color,
    val secondaryFixed: Color,
    val onSecondaryFixed: Color,
    val tertiaryFixed: Color,
    val onTertiaryFixed: Color,
    val primaryFixedDim: Color,
    val secondaryFixedDim: Color,
    val tertiaryFixedDim: Color,
    // NOTE: Variant colors are missing in the original FixedAccentColors
    val onPrimaryFixedVariant: Color,
    val onSecondaryFixedVariant: Color,
    val onTertiaryFixedVariant: Color,
)

@Composable
fun ColorRolesTable(
    themeColorPack: ThemeColorPack
) {
    // From ColorSchemeFixedAccentColorSample
    // Source: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material3/material3/samples/src/main/java/androidx/compose/material3/samples/ColorSchemeSamples.kt;l=32;bpv=0;bpt=0
    val fixedAccentColors = remember(themeColorPack) {
        val light = themeColorPack.lightColorScheme
        val dark = themeColorPack.darkColorScheme
        FixedAccentColors(
            primaryFixed = light.primaryContainer,
            onPrimaryFixed = light.onPrimaryContainer,
            secondaryFixed = light.secondaryContainer,
            onSecondaryFixed = light.onSecondaryContainer,
            tertiaryFixed = light.tertiaryContainer,
            onTertiaryFixed = light.onTertiaryContainer,
            primaryFixedDim = dark.primary,
            secondaryFixedDim = dark.secondary,
            tertiaryFixedDim = dark.tertiary,
            onPrimaryFixedVariant = light.primary,
            onSecondaryFixedVariant = light.secondary,
            onTertiaryFixedVariant = light.tertiary,
        )
    }

    var zoom: Float by remember { mutableFloatStateOf(1f) }
    Column(
        verticalArrangement = Arrangement.spacedBy(ColorTableSectionPadding),
        modifier = Modifier
            .fillMaxSize()
            .freeScrollWithTransformGesture(
                state = rememberFreeScrollState(),
                onGesture = { _, _, gestureZoom, _ ->
                    zoom = (zoom * gestureZoom).coerceIn(0.5f, 1f)
                })
            .graphicsLayer {
                scaleX = zoom
                scaleY = zoom
            }
            .padding(16.dp)
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(ColorTableSectionPadding)) {
            Row(horizontalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                    ColorBlockSimple(
                        text = "Primary",
                        color = MaterialTheme.colorScheme.primary,
                    )
                    ColorBlockSimple(
                        text = "Primary Container",
                        color = MaterialTheme.colorScheme.primaryContainer,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                    ColorBlockSimple(
                        text = "Secondary",
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    ColorBlockSimple(
                        text = "Secondary Container",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                    ColorBlockSimple(
                        text = "Tertiary",
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    ColorBlockSimple(
                        text = "Tertiary Container",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                ColorBlockSimple(
                    text = "Error",
                    color = MaterialTheme.colorScheme.error,
                )
                ColorBlockSimple(
                    text = "Error Container",
                    color = MaterialTheme.colorScheme.errorContainer,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
            ColorBlockWithFixedAccent(
                fixedText = "Primary Fixed",
                fixedColor = fixedAccentColors.primaryFixed,
                fixedDimText = "Primary Fixed Dim",
                fixedDimColor = fixedAccentColors.primaryFixedDim,
                onFixedText = "On Primary Fixed",
                onFixedColor = fixedAccentColors.onPrimaryFixed,
                onFixedVariantText = "On Primary Fixed Variant",
                onFixedVariantColor = fixedAccentColors.onPrimaryFixedVariant,
            )

            ColorBlockWithFixedAccent(
                fixedText = "Secondary Fixed",
                fixedColor = fixedAccentColors.secondaryFixed,
                fixedDimText = "Secondary Fixed Dim",
                fixedDimColor = fixedAccentColors.secondaryFixedDim,
                onFixedText = "On Secondary Fixed",
                onFixedColor = fixedAccentColors.onSecondaryFixed,
                onFixedVariantText = "On Secondary Fixed Variant",
                onFixedVariantColor = fixedAccentColors.onSecondaryFixedVariant,
            )

            ColorBlockWithFixedAccent(
                fixedText = "Tertiary Fixed",
                fixedColor = fixedAccentColors.tertiaryFixed,
                fixedDimText = "Tertiary Fixed Dim",
                fixedDimColor = fixedAccentColors.tertiaryFixedDim,
                onFixedText = "On Tertiary Fixed",
                onFixedColor = fixedAccentColors.onTertiaryFixed,
                onFixedVariantText = "On Tertiary Fixed Variant",
                onFixedVariantColor = fixedAccentColors.onTertiaryFixedVariant,
            )
        }

        SurfaceSection()
    }
}

@Composable
private fun ColorBlockSimple(
    text: String,
    color: Color,
) {
    val onText = "On $text"
    val onColor = MaterialTheme.colorScheme.contentColorFor(color)

    Column(modifier = Modifier.width(ColorCellWidth)) {
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(ColorTableBigCellHeight)
                .background(color)
                .padding(ColorCellContentPadding)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = onColor
            )
        }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(ColorTableSmallCellHeight)
                .background(onColor)
                .padding(ColorCellContentPadding)
        ) {
            Text(
                text = onText,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

/**
 * Block for Fixed accent colors
 */
@Composable
private fun ColorBlockWithFixedAccent(
    fixedText: String,
    fixedColor: Color,
    fixedDimText: String,
    fixedDimColor: Color,
    onFixedText: String,
    onFixedColor: Color,
    onFixedVariantText: String,
    onFixedVariantColor: Color
) {
    Column(
        modifier = Modifier
            .width(ColorCellWidth)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ColorTableBigCellHeight)
        ) {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(fixedColor)
                    .padding(ColorCellContentPadding)
            ) {
                Text(
                    text = fixedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = onFixedColor
                )
            }

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(fixedDimColor)
                    .padding(ColorCellContentPadding)
            ) {
                Text(
                    text = fixedDimText,
                    style = MaterialTheme.typography.bodySmall,
                    color = onFixedColor
                )
            }
        }

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(ColorTableSmallCellHeight)
                .background(onFixedColor)
                .padding(ColorCellContentPadding)
        ) {
            Text(
                text = onFixedText,
                style = MaterialTheme.typography.bodySmall,
                color = fixedColor
            )
        }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(ColorTableSmallCellHeight)
                .background(onFixedVariantColor)
                .padding(ColorCellContentPadding)
        ) {
            Text(
                text = onFixedVariantText,
                style = MaterialTheme.typography.bodySmall,
                color = fixedColor
            )
        }
    }
}

@Composable
private fun SurfaceSection() {

    // The surface section has the width of 3 cell blocks without any padding in between
    val surfaceSectionWidth = ((ColorCellWidth * 3) + (ColorTableCellPadding * 2))

    Row(horizontalArrangement = Arrangement.spacedBy(ColorTableSectionPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
            Row(
                modifier = Modifier
                    .width(surfaceSectionWidth)
                    .height(ColorTableBigCellHeight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceDim)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface Dim",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceBright)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface Bright",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Row(
                modifier = Modifier
                    .width(surfaceSectionWidth)
                    .height(ColorTableBigCellHeight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface Container Lowest",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface Container Low",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface Container",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface Container High",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Surface Container Highest",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Row(
                modifier = Modifier
                    .width(surfaceSectionWidth)
                    .height(ColorTableSmallCellHeight)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.onSurface)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "On Surface",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "On Surface Variant",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outline)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Outline",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Outline Variant",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // The last column with the inverse colors has the same size as the surface section with two
        // rows of big cells and a row of small cells with padding in between
        val inverseColumnHeight =
            (ColorTableBigCellHeight * 2) + ColorTableSmallCellHeight + (ColorTableCellPadding * 2)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(inverseColumnHeight)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .height(ColorTableSmallCellHeight)
                        .width(ColorCellWidth)
                        .background(MaterialTheme.colorScheme.inverseSurface)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Inverse Surface",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .height(ColorTableSmallCellHeight)
                        .width(ColorCellWidth)
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Inverse On Surface",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .height(ColorTableSmallCellHeight)
                        .width(ColorCellWidth)
                        .background(MaterialTheme.colorScheme.inversePrimary)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Inverse Primary",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(ColorTableSectionPadding),
                modifier = Modifier
                    .width(ColorCellWidth)
                    .height(ColorTableSmallCellHeight)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.scrim)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Scrim",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Color.Black)
                        .padding(ColorCellContentPadding)
                ) {
                    Text(
                        text = "Shadow *",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Space between cells on the sections
private val ColorTableCellPadding = 4.dp

// Space between sections
private val ColorTableSectionPadding = 16.dp

// Width of a table cell
private val ColorCellWidth = 180.dp

// Padding for the content of the cell
private val ColorCellContentPadding = 8.dp

// Height of the different color blocks
private val ColorTableSmallCellHeight = 32.dp
private val ColorTableBigCellHeight = 64.dp

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun ColorRolesTablePreview() {
    val context = LocalContext.current
    val themeColorPack = ThemeColorPack(
        lightColorScheme = dynamicLightColorScheme(context),
        darkColorScheme = dynamicDarkColorScheme(context)
    )

    DynamicExportTheme(themeColorPack = themeColorPack) {
        ColorRolesTable(themeColorPack = themeColorPack)
    }
}