package com.spundev.dynamicthemeexport.data

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.graphics.Color

/**
 * Legacy surface container colors before tone-based Surfaces
 *
 * Right now, both dynamic themes are being generated with a set of surface containers colors.
 * Unfortunately, sometimes the generated colors are barely tinted (almost grayscale).
 *
 * In recent versions of Compose Material3, the included components are now using tone-based
 * Surfaces and, if the default dynamic theme is used, these component will use the "grayscale" set
 * of surface containers.
 *
 * Before the tone-based Surfaces introduction, a lot of components were using a combination of
 * [ColorScheme.surface] + a tonalElevation level/value to create a new surface color that was
 * tinted using [ColorScheme.surfaceTint].
 * See [ColorScheme.surfaceColorAtElevation].
 *
 * We are including the legacy [ColorScheme.surface] + tonalElevation colors in case the user wants
 * to use them as an alternative to some of the generated surface containers.
 */
data class ElevatedSurfaceLevels(
    val surfaceLevel1: Color,
    val surfaceLevel2: Color,
    val surfaceLevel3: Color,
    val surfaceLevel4: Color,
    val surfaceLevel5: Color,
)