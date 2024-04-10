package com.spundev.dynamicthemeexport.ext

import androidx.compose.material3.ColorScheme
import com.spundev.dynamicthemeexport.data.ColorFormat

fun ColorScheme.toColorStringMap(colorFormat: ColorFormat): Map<String, String> {
    return mapOf(
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
        "surfaceContainer" to colorFormat.formatter(surfaceContainer),
        "surfaceContainerHigh" to colorFormat.formatter(surfaceContainerHigh),
        "surfaceContainerHighest" to colorFormat.formatter(surfaceContainerHighest),
        "surfaceContainerLow" to colorFormat.formatter(surfaceContainerLow),
        "surfaceContainerLowest" to colorFormat.formatter(surfaceContainerLowest),
    )
}