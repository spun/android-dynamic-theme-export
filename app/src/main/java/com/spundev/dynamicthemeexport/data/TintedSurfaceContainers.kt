package com.spundev.dynamicthemeexport.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.graphics.Color
import com.spundev.dynamicthemeexport.ext.getTintedSurfaceColors

/**
 * Alternative to the dynamic tone-based Surfaces
 * Right now, both dynamic themes are being generated with a set of surface containers colors with
 * barely any tint (sometimes almost grayscale).
 * Before the introduction of these tone-based Surfaces, a lot of components were using a
 * combination of [ColorScheme.surface] + a tonalElevation level/value to create a new surface color
 * that was tinted using [ColorScheme.surfaceTint].
 * See [ColorScheme.surfaceColorAtElevation].
 *
 * In recent versions of Compose Material3, the included components are now using tone-based
 * Surfaces and, if the default dynamic theme is used, these component will use only the "grayscale"
 * set of surface containers.
 * This is probably something that future updates to the dynamicTheme functions will fix but until
 * then, we are including an alternative set of surfaceContainer colors created using the old method
 * of surface + tonalElevation.
 * See [ColorScheme.getTintedSurfaceColors].
 */
data class TintedSurfaceContainers(
    val surfaceContainerLow: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
)