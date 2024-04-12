package com.spundev.dynamicthemeexport.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.data.ColorFormat
import com.spundev.dynamicthemeexport.data.TintedSurfaceContainers

fun ColorScheme.toColorStringMap(colorFormat: ColorFormat): Map<String, String> {
    val base = mapOf(
        "primary" to colorFormat.formatter(primary),
        "onPrimary" to colorFormat.formatter(onPrimary),
        "primaryContainer" to colorFormat.formatter(primaryContainer),
        "onPrimaryContainer" to colorFormat.formatter(onPrimaryContainer),
        "inversePrimary" to colorFormat.formatter(inversePrimary),
        "secondary" to colorFormat.formatter(secondary),
        "onSecondary" to colorFormat.formatter(onSecondary),
        "secondaryContainer" to colorFormat.formatter(secondaryContainer),
        "onSecondaryContainer" to colorFormat.formatter(onSecondaryContainer),
        "tertiary" to colorFormat.formatter(tertiary),
        "onTertiary" to colorFormat.formatter(onTertiary),
        "tertiaryContainer" to colorFormat.formatter(tertiaryContainer),
        "onTertiaryContainer" to colorFormat.formatter(onTertiaryContainer),
        "background" to colorFormat.formatter(background),
        "onBackground" to colorFormat.formatter(onBackground),
        "surface" to colorFormat.formatter(surface),
        "onSurface" to colorFormat.formatter(onSurface),
        "surfaceVariant" to colorFormat.formatter(surfaceVariant),
        "onSurfaceVariant" to colorFormat.formatter(onSurfaceVariant),
        "surfaceTint" to colorFormat.formatter(surfaceTint),
        "inverseSurface" to colorFormat.formatter(inverseSurface),
        "inverseOnSurface" to colorFormat.formatter(inverseOnSurface),
        "error" to colorFormat.formatter(error),
        "onError" to colorFormat.formatter(onError),
        "errorContainer" to colorFormat.formatter(errorContainer),
        "onErrorContainer" to colorFormat.formatter(onErrorContainer),
        "outline" to colorFormat.formatter(outline),
        "outlineVariant" to colorFormat.formatter(outlineVariant),
        "scrim" to colorFormat.formatter(scrim),
        "surfaceBright" to colorFormat.formatter(surfaceBright),
        "surfaceDim" to colorFormat.formatter(surfaceDim),
        "surfaceContainerLowest" to colorFormat.formatter(surfaceContainerLowest),
        "surfaceContainerLow" to colorFormat.formatter(surfaceContainerLow),
        "surfaceContainer" to colorFormat.formatter(surfaceContainer),
        "surfaceContainerHigh" to colorFormat.formatter(surfaceContainerHigh),
        "surfaceContainerHighest" to colorFormat.formatter(surfaceContainerHighest),
    )

    val tintedSurfaceColors = getTintedSurfaceColors()
    val tintedSurfaceContainers = mapOf(
        "// surfaceContainerLowest" to colorFormat.formatter(tintedSurfaceColors.surfaceContainerLowest),
        "// surfaceContainerLow" to colorFormat.formatter(tintedSurfaceColors.surfaceContainerLow),
        "// surfaceContainer" to colorFormat.formatter(tintedSurfaceColors.surfaceContainer),
        "// surfaceContainerHigh" to colorFormat.formatter(tintedSurfaceColors.surfaceContainerHigh),
        "// surfaceContainerHighest" to colorFormat.formatter(tintedSurfaceColors.surfaceContainerHighest),
    )

    return base + tintedSurfaceContainers
}

/**
 * Generate a tinted alternative to the default surfaceContainer colors.
 * More info [TintedSurfaceContainers]
 */
internal fun ColorScheme.getTintedSurfaceColors(): TintedSurfaceContainers {
    return TintedSurfaceContainers(
        surfaceContainerLowest = surfaceColorAtElevation(1.dp),
        surfaceContainerLow = surfaceColorAtElevation(3.dp),
        surfaceContainer = surfaceColorAtElevation(6.dp),
        surfaceContainerHigh = surfaceColorAtElevation(8.dp),
        surfaceContainerHighest = surfaceColorAtElevation(12.dp),
    )
}