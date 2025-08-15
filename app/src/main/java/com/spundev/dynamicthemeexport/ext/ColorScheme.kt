package com.spundev.dynamicthemeexport.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.data.ColorFormat
import com.spundev.dynamicthemeexport.data.ElevatedSurfaceLevels

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

    val elevatedSurfaceLevels = getElevatedSurfaceLevels()
    val extraSurfaceValues = mapOf(
        "// surfaceLevel1" to colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel1),
        "// surfaceLevel2" to colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel2),
        "// surfaceLevel3" to colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel3),
        "// surfaceLevel4" to colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel4),
        "// surfaceLevel5" to colorFormat.formatter(elevatedSurfaceLevels.surfaceLevel5),
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
