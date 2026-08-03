package com.spundev.dynamicthemeexport.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.data.ColorFormat
import com.spundev.dynamicthemeexport.data.ElevatedSurfaceLevels

data class FormattedColor(
    val colorName: String,
    val output: String,
    val enabled: Boolean = true
)

fun ColorScheme.toFormattedColors(colorFormat: ColorFormat): List<FormattedColor> {
    val base = listOf(
        FormattedColor(
            colorName = "primary",
            output = colorFormat.formatter(primary)
        ),
        FormattedColor(
            colorName = "onPrimary",
            output = colorFormat.formatter(onPrimary)
        ),
        FormattedColor(
            colorName = "primaryContainer",
            output = colorFormat.formatter(primaryContainer)
        ),
        FormattedColor(
            colorName = "onPrimaryContainer",
            output = colorFormat.formatter(onPrimaryContainer)
        ),
        FormattedColor(
            colorName = "inversePrimary",
            output = colorFormat.formatter(inversePrimary)
        ),
        FormattedColor(
            colorName = "secondary",
            output = colorFormat.formatter(secondary)
        ),
        FormattedColor(
            colorName = "onSecondary",
            output = colorFormat.formatter(onSecondary)
        ),
        FormattedColor(
            colorName = "secondaryContainer",
            output = colorFormat.formatter(secondaryContainer)
        ),
        FormattedColor(
            colorName = "onSecondaryContainer",
            output = colorFormat.formatter(onSecondaryContainer)
        ),
        FormattedColor(
            colorName = "tertiary",
            output = colorFormat.formatter(tertiary)
        ),
        FormattedColor(
            colorName = "onTertiary",
            output = colorFormat.formatter(onTertiary)
        ),
        FormattedColor(
            colorName = "tertiaryContainer",
            output = colorFormat.formatter(tertiaryContainer)
        ),
        FormattedColor(
            colorName = "onTertiaryContainer",
            output = colorFormat.formatter(onTertiaryContainer)
        ),
        FormattedColor(
            colorName = "background",
            output = colorFormat.formatter(background)
        ),
        FormattedColor(
            colorName = "onBackground",
            output = colorFormat.formatter(onBackground)
        ),
        FormattedColor(
            colorName = "surface",
            output = colorFormat.formatter(surface)
        ),
        FormattedColor(
            colorName = "onSurface",
            output = colorFormat.formatter(onSurface)
        ),
        FormattedColor(
            colorName = "surfaceVariant",
            output = colorFormat.formatter(surfaceVariant)
        ),
        FormattedColor(
            colorName = "onSurfaceVariant",
            output = colorFormat.formatter(onSurfaceVariant)
        ),
        FormattedColor(
            colorName = "surfaceTint",
            output = colorFormat.formatter(surfaceTint)
        ),
        FormattedColor(
            colorName = "inverseSurface",
            output = colorFormat.formatter(inverseSurface)
        ),
        FormattedColor(
            colorName = "inverseOnSurface",
            output = colorFormat.formatter(inverseOnSurface)
        ),
        FormattedColor(
            colorName = "error",
            output = colorFormat.formatter(error)
        ),
        FormattedColor(
            colorName = "onError",
            output = colorFormat.formatter(onError)
        ),
        FormattedColor(
            colorName = "errorContainer",
            output = colorFormat.formatter(errorContainer)
        ),
        FormattedColor(
            colorName = "onErrorContainer",
            output = colorFormat.formatter(onErrorContainer)
        ),
        FormattedColor(
            colorName = "outline",
            output = colorFormat.formatter(outline)
        ),
        FormattedColor(
            colorName = "outlineVariant",
            output = colorFormat.formatter(outlineVariant)
        ),
        FormattedColor(
            colorName = "scrim",
            output = colorFormat.formatter(scrim)
        ),
        FormattedColor(
            colorName = "surfaceBright",
            output = colorFormat.formatter(surfaceBright)
        ),
        FormattedColor(
            colorName = "surfaceContainer",
            output = colorFormat.formatter(surfaceContainer)
        ),
        FormattedColor(
            colorName = "surfaceContainerHigh",
            output = colorFormat.formatter(surfaceContainerHigh)
        ),
        FormattedColor(
            colorName = "surfaceContainerHighest",
            output = colorFormat.formatter(surfaceContainerHighest)
        ),
        FormattedColor(
            colorName = "surfaceContainerLow",
            output = colorFormat.formatter(surfaceContainerLow)
        ),
        FormattedColor(
            colorName = "surfaceContainerLowest",
            output = colorFormat.formatter(surfaceContainerLowest)
        ),
        FormattedColor(
            colorName = "surfaceDim",
            output = colorFormat.formatter(surfaceDim)
        ),
        FormattedColor(
            colorName = "primaryFixed",
            output = colorFormat.formatter(primaryFixed)
        ),
        FormattedColor(
            colorName = "primaryFixedDim",
            output = colorFormat.formatter(primaryFixedDim)
        ),
        FormattedColor(
            colorName = "onPrimaryFixed",
            output = colorFormat.formatter(onPrimaryFixed)
        ),
        FormattedColor(
            colorName = "onPrimaryFixedVariant",
            output = colorFormat.formatter(onPrimaryFixedVariant)
        ),
        FormattedColor(
            colorName = "secondaryFixed",
            output = colorFormat.formatter(secondaryFixed)
        ),
        FormattedColor(
            colorName = "secondaryFixedDim",
            output = colorFormat.formatter(secondaryFixedDim)
        ),
        FormattedColor(
            colorName = "onSecondaryFixed",
            output = colorFormat.formatter(onSecondaryFixed)
        ),
        FormattedColor(
            colorName = "onSecondaryFixedVariant",
            output = colorFormat.formatter(onSecondaryFixedVariant)
        ),
        FormattedColor(
            colorName = "tertiaryFixed",
            output = colorFormat.formatter(tertiaryFixed)
        ),
        FormattedColor(
            colorName = "tertiaryFixedDim",
            output = colorFormat.formatter(tertiaryFixedDim)
        ),
        FormattedColor(
            colorName = "onTertiaryFixed",
            output = colorFormat.formatter(onTertiaryFixed)
        ),
        FormattedColor(
            colorName = "onTertiaryFixedVariant",
            output = colorFormat.formatter(onTertiaryFixedVariant)
        ),
    )

    val elevatedSurfaceLevels = getElevatedSurfaceLevels()
    val extraSurfaceValues = listOf(
        FormattedColor(
            colorName = "surfaceLevel1",
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel1),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel2",
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel2),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel3",
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel3),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel4",
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel4),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel5",
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel5),
            enabled = false
        ),
    )

    return base + extraSurfaceValues
}

/**
 * Copy of Material3 internal ElevationTokens
 */
private object ElevationTokens {
    // val Level0 = 0.0.dp
    val Level1 = 1.0.dp
    val Level2 = 3.0.dp
    val Level3 = 6.0.dp
    val Level4 = 8.0.dp
    val Level5 = 12.0.dp
}

/**
 * Generate the legacy elevation-based surface color.
 * More info [ElevatedSurfaceLevels]
 */
internal fun ColorScheme.getElevatedSurfaceLevels(): ElevatedSurfaceLevels {
    return ElevatedSurfaceLevels(
        surfaceLevel1 = surfaceColorAtElevation(ElevationTokens.Level1),
        surfaceLevel2 = surfaceColorAtElevation(ElevationTokens.Level2),
        surfaceLevel3 = surfaceColorAtElevation(ElevationTokens.Level3),
        surfaceLevel4 = surfaceColorAtElevation(ElevationTokens.Level4),
        surfaceLevel5 = surfaceColorAtElevation(ElevationTokens.Level5),
    )
}