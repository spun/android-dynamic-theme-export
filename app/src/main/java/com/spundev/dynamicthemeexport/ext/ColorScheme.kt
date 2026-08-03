package com.spundev.dynamicthemeexport.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.data.ColorFormat
import com.spundev.dynamicthemeexport.data.ElevatedSurfaceLevels

data class FormattedColor(
    val colorName: String,
    val color: Color,
    val output: String,
    val enabled: Boolean = true
)

fun ColorScheme.toFormattedColors(colorFormat: ColorFormat): List<FormattedColor> {
    val base = listOf(
        FormattedColor(
            colorName = "primary",
            color = primary,
            output = colorFormat.formatter(primary)
        ),
        FormattedColor(
            colorName = "onPrimary",
            color = onPrimary,
            output = colorFormat.formatter(onPrimary)
        ),
        FormattedColor(
            colorName = "primaryContainer",
            color = primaryContainer,
            output = colorFormat.formatter(primaryContainer)
        ),
        FormattedColor(
            colorName = "onPrimaryContainer",
            color = onPrimaryContainer,
            output = colorFormat.formatter(onPrimaryContainer)
        ),
        FormattedColor(
            colorName = "inversePrimary",
            color = inversePrimary,
            output = colorFormat.formatter(inversePrimary)
        ),
        FormattedColor(
            colorName = "secondary",
            color = secondary,
            output = colorFormat.formatter(secondary)
        ),
        FormattedColor(
            colorName = "onSecondary",
            color = onSecondary,
            output = colorFormat.formatter(onSecondary)
        ),
        FormattedColor(
            colorName = "secondaryContainer",
            color = secondaryContainer,
            output = colorFormat.formatter(secondaryContainer)
        ),
        FormattedColor(
            colorName = "onSecondaryContainer",
            color = onSecondaryContainer,
            output = colorFormat.formatter(onSecondaryContainer)
        ),
        FormattedColor(
            colorName = "tertiary",
            color = tertiary,
            output = colorFormat.formatter(tertiary)
        ),
        FormattedColor(
            colorName = "onTertiary",
            color = onTertiary,
            output = colorFormat.formatter(onTertiary)
        ),
        FormattedColor(
            colorName = "tertiaryContainer",
            color = tertiaryContainer,
            output = colorFormat.formatter(tertiaryContainer)
        ),
        FormattedColor(
            colorName = "onTertiaryContainer",
            color = onTertiaryContainer,
            output = colorFormat.formatter(onTertiaryContainer)
        ),
        FormattedColor(
            colorName = "background",
            color = background,
            output = colorFormat.formatter(background)
        ),
        FormattedColor(
            colorName = "onBackground",
            color = onBackground,
            output = colorFormat.formatter(onBackground)
        ),
        FormattedColor(
            colorName = "surface",
            color = surface,
            output = colorFormat.formatter(surface)
        ),
        FormattedColor(
            colorName = "onSurface",
            color = onSurface,
            output = colorFormat.formatter(onSurface)
        ),
        FormattedColor(
            colorName = "surfaceVariant",
            color = surfaceVariant,
            output = colorFormat.formatter(surfaceVariant)
        ),
        FormattedColor(
            colorName = "onSurfaceVariant",
            color = onSurfaceVariant,
            output = colorFormat.formatter(onSurfaceVariant)
        ),
        FormattedColor(
            colorName = "surfaceTint",
            color = surfaceTint,
            output = colorFormat.formatter(surfaceTint)
        ),
        FormattedColor(
            colorName = "inverseSurface",
            color = inverseSurface,
            output = colorFormat.formatter(inverseSurface)
        ),
        FormattedColor(
            colorName = "inverseOnSurface",
            color = inverseOnSurface,
            output = colorFormat.formatter(inverseOnSurface)
        ),
        FormattedColor(
            colorName = "error",
            color = error,
            output = colorFormat.formatter(error)
        ),
        FormattedColor(
            colorName = "onError",
            color = onError,
            output = colorFormat.formatter(onError)
        ),
        FormattedColor(
            colorName = "errorContainer",
            color = errorContainer,
            output = colorFormat.formatter(errorContainer)
        ),
        FormattedColor(
            colorName = "onErrorContainer",
            color = onErrorContainer,
            output = colorFormat.formatter(onErrorContainer)
        ),
        FormattedColor(
            colorName = "outline",
            color = outline,
            output = colorFormat.formatter(outline)
        ),
        FormattedColor(
            colorName = "outlineVariant",
            color = outlineVariant,
            output = colorFormat.formatter(outlineVariant)
        ),
        FormattedColor(
            colorName = "scrim",
            color = scrim,
            output = colorFormat.formatter(scrim)
        ),
        FormattedColor(
            colorName = "surfaceBright",
            color = surfaceBright,
            output = colorFormat.formatter(surfaceBright)
        ),
        FormattedColor(
            colorName = "surfaceContainer",
            color = surfaceContainer,
            output = colorFormat.formatter(surfaceContainer)
        ),
        FormattedColor(
            colorName = "surfaceContainerHigh",
            color = surfaceContainerHigh,
            output = colorFormat.formatter(surfaceContainerHigh)
        ),
        FormattedColor(
            colorName = "surfaceContainerHighest",
            color = surfaceContainerHighest,
            output = colorFormat.formatter(surfaceContainerHighest)
        ),
        FormattedColor(
            colorName = "surfaceContainerLow",
            color = surfaceContainerLow,
            output = colorFormat.formatter(surfaceContainerLow)
        ),
        FormattedColor(
            colorName = "surfaceContainerLowest",
            color = surfaceContainerLowest,
            output = colorFormat.formatter(surfaceContainerLowest)
        ),
        FormattedColor(
            colorName = "surfaceDim",
            color = surfaceDim,
            output = colorFormat.formatter(surfaceDim)
        ),
        FormattedColor(
            colorName = "primaryFixed",
            color = primaryFixed,
            output = colorFormat.formatter(primaryFixed)
        ),
        FormattedColor(
            colorName = "primaryFixedDim",
            color = primaryFixedDim,
            output = colorFormat.formatter(primaryFixedDim)
        ),
        FormattedColor(
            colorName = "onPrimaryFixed",
            color = onPrimaryFixed,
            output = colorFormat.formatter(onPrimaryFixed)
        ),
        FormattedColor(
            colorName = "onPrimaryFixedVariant",
            color = onPrimaryFixedVariant,
            output = colorFormat.formatter(onPrimaryFixedVariant)
        ),
        FormattedColor(
            colorName = "secondaryFixed",
            color = secondaryFixed,
            output = colorFormat.formatter(secondaryFixed)
        ),
        FormattedColor(
            colorName = "secondaryFixedDim",
            color = secondaryFixedDim,
            output = colorFormat.formatter(secondaryFixedDim)
        ),
        FormattedColor(
            colorName = "onSecondaryFixed",
            color = onSecondaryFixed,
            output = colorFormat.formatter(onSecondaryFixed)
        ),
        FormattedColor(
            colorName = "onSecondaryFixedVariant",
            color = onSecondaryFixedVariant,
            output = colorFormat.formatter(onSecondaryFixedVariant)
        ),
        FormattedColor(
            colorName = "tertiaryFixed",
            color = tertiaryFixed,
            output = colorFormat.formatter(tertiaryFixed)
        ),
        FormattedColor(
            colorName = "tertiaryFixedDim",
            color = tertiaryFixedDim,
            output = colorFormat.formatter(tertiaryFixedDim)
        ),
        FormattedColor(
            colorName = "onTertiaryFixed",
            color = onTertiaryFixed,
            output = colorFormat.formatter(onTertiaryFixed)
        ),
        FormattedColor(
            colorName = "onTertiaryFixedVariant",
            color = onTertiaryFixedVariant,
            output = colorFormat.formatter(onTertiaryFixedVariant)
        ),
    )

    val elevatedSurfaceLevels = getElevatedSurfaceLevels()
    val extraSurfaceValues = listOf(
        FormattedColor(
            colorName = "surfaceLevel1",
            color = elevatedSurfaceLevels.surfaceLevel1,
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel1),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel2",
            color = elevatedSurfaceLevels.surfaceLevel2,
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel2),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel3",
            color = elevatedSurfaceLevels.surfaceLevel3,
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel3),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel4",
            color = elevatedSurfaceLevels.surfaceLevel4,
            output = colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel4),
            enabled = false
        ),
        FormattedColor(
            colorName = "surfaceLevel5",
            color = elevatedSurfaceLevels.surfaceLevel5,
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