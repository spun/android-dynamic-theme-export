package com.spundev.dynamicthemeexport.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb

fun ColorScheme.toColorStringMap(): Map<String, Int> {
    return mapOf(
        "primary" to primary.toArgb(),
        "onPrimary" to onPrimary.toArgb(),
        "primaryContainer" to primaryContainer.toArgb(),
        "onPrimaryContainer" to onPrimaryContainer.toArgb(),
        "inversePrimary" to inversePrimary.toArgb(),
        "secondary" to secondary.toArgb(),
        "onSecondary" to onSecondary.toArgb(),
        "secondaryContainer" to secondaryContainer.toArgb(),
        "onSecondaryContainer" to onSecondaryContainer.toArgb(),
        "tertiary" to tertiary.toArgb(),
        "onTertiary" to onTertiary.toArgb(),
        "tertiaryContainer" to tertiaryContainer.toArgb(),
        "onTertiaryContainer" to onTertiaryContainer.toArgb(),
        "background" to background.toArgb(),
        "onBackground" to onBackground.toArgb(),
        "surface" to surface.toArgb(),
        "onSurface" to onSurface.toArgb(),
        "surfaceVariant" to surfaceVariant.toArgb(),
        "onSurfaceVariant" to onSurfaceVariant.toArgb(),
        "surfaceTint" to surfaceTint.toArgb(),
        "inverseSurface" to inverseSurface.toArgb(),
        "inverseOnSurface" to inverseOnSurface.toArgb(),
        "error" to error.toArgb(),
        "onError" to onError.toArgb(),
        "errorContainer" to errorContainer.toArgb(),
        "onErrorContainer" to onErrorContainer.toArgb(),
        "outline" to outline.toArgb(),
        "outlineVariant" to outlineVariant.toArgb(),
        "scrim" to scrim.toArgb(),
        "surfaceBright" to surfaceBright.toArgb(),
        "surfaceDim" to surfaceDim.toArgb(),
        "surfaceContainer" to surfaceContainer.toArgb(),
        "surfaceContainerHigh" to surfaceContainerHigh.toArgb(),
        "surfaceContainerHighest" to surfaceContainerHighest.toArgb(),
        "surfaceContainerLow" to surfaceContainerLow.toArgb(),
        "surfaceContainerLowest" to surfaceContainerLowest.toArgb(),
    )
}