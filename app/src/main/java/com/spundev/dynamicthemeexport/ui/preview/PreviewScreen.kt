package com.spundev.dynamicthemeexport.ui.preview

import android.content.ClipData
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import com.spundev.dynamicthemeexport.data.ElevatedSurfaceLevels
import com.spundev.dynamicthemeexport.ext.getElevatedSurfaceLevels
import com.spundev.dynamicthemeexport.ui.preview.components.ColorBlockBasic
import com.spundev.dynamicthemeexport.ui.preview.components.ColorBlockPair
import com.spundev.dynamicthemeexport.ui.preview.components.ColorBlockWithFixedAccent
import com.spundev.dynamicthemeexport.ui.preview.components.DefaultColorBlockStyle
import com.spundev.dynamicthemeexport.ui.preview.components.ForceSmallColorBlockStyle
import com.spundev.dynamicthemeexport.ui.theme.DynamicExportTheme
import com.spundev.dynamicthemeexport.util.freeScroll.freeScroll
import com.spundev.dynamicthemeexport.util.freeScroll.rememberFreeScrollState
import kotlinx.coroutines.launch

@Deprecated(
    message = "Use PreviewGridScreen instead.",
    replaceWith = ReplaceWith(
        expression = "PreviewGridScreen()",
        imports = ["com.spundev.dynamicthemeexport.ui.preview.PreviewGridScreen"]
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun ColorRolesTable() {
    // Legacy elevated surface colors
    val currentColorScheme = MaterialTheme.colorScheme
    val elevatedSurfaceLevels = remember(currentColorScheme) {
        currentColorScheme.getElevatedSurfaceLevels()
    }

    var zoom: Float by remember { mutableFloatStateOf(1f) }
    val freeScrollState = rememberFreeScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(ColorTableSectionPadding),
        modifier = Modifier
            .fillMaxSize()
            .freeScroll(freeScrollState)
            .pointerInput(Unit) {
                // NOTE: We need to do this instead of using detectGestures because detectGestures
                // has a few checks triggered by the combinedClickable in our color blocks
                // (copy-to-clipboard feature) that break the while loop.
                // This is a simplified version that only calculates zoom changes.
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    while (true) {
                        val event = awaitPointerEvent()
                        val isConsumed = event.changes.fastAny { it.isConsumed }
                        if (event.changes.size > 1 && !isConsumed) {
                            val zoomChange = event.calculateZoom()
                            zoom = (zoom * zoomChange).coerceIn(0.5f, 1f)
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
            }
            .graphicsLayer {
                scaleX = zoom
                scaleY = zoom
            }
            .padding(16.dp)
    ) {
        // Copy to clipboard
        val context = LocalContext.current
        val clipboard = LocalClipboard.current
        val scope = rememberCoroutineScope()
        val onCopy: suspend (text: String) -> Unit = { text ->
            clipboard.setClipEntry(
                clipEntry = ClipData.newPlainText(
                    /* label = */ text,
                    /* text = */ text
                ).toClipEntry()
            )
            // Only show a toast for Android 12 (32) and lower.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(ColorTableSectionPadding)) {
            Row(horizontalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                    ColorBlockPair(
                        text = "Primary",
                        color = MaterialTheme.colorScheme.primary,
                        onCopy = { scope.launch { onCopy(it) } },
                        modifier = Modifier.width(ColorCellWidth)
                    )
                    ColorBlockPair(
                        text = "Primary Container",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onCopy = { scope.launch { onCopy(it) } },
                        modifier = Modifier.width(ColorCellWidth)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                    ColorBlockPair(
                        text = "Secondary",
                        color = MaterialTheme.colorScheme.secondary,
                        onCopy = { scope.launch { onCopy(it) } },
                        modifier = Modifier.width(ColorCellWidth)
                    )
                    ColorBlockPair(
                        text = "Secondary Container",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onCopy = { scope.launch { onCopy(it) } },
                        modifier = Modifier.width(ColorCellWidth)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                    ColorBlockPair(
                        text = "Tertiary",
                        color = MaterialTheme.colorScheme.tertiary,
                        onCopy = { scope.launch { onCopy(it) } },
                        modifier = Modifier.width(ColorCellWidth)
                    )
                    ColorBlockPair(
                        text = "Tertiary Container",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onCopy = { scope.launch { onCopy(it) } },
                        modifier = Modifier.width(ColorCellWidth)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
                ColorBlockPair(
                    text = "Error",
                    color = MaterialTheme.colorScheme.error,
                    onCopy = { scope.launch { onCopy(it) } },
                    modifier = Modifier.width(ColorCellWidth)
                )
                ColorBlockPair(
                    text = "Error Container",
                    color = MaterialTheme.colorScheme.errorContainer,
                    onCopy = { scope.launch { onCopy(it) } },
                    modifier = Modifier.width(ColorCellWidth)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
            ColorBlockWithFixedAccent(
                fixedText = "Primary Fixed",
                fixedColor = MaterialTheme.colorScheme.primaryFixed,
                fixedDimText = "Primary Fixed Dim",
                fixedDimColor = MaterialTheme.colorScheme.primaryFixedDim,
                onFixedText = "On Primary Fixed",
                onFixedColor = MaterialTheme.colorScheme.onPrimaryFixed,
                onFixedVariantText = "On Primary Fixed Variant",
                onFixedVariantColor = MaterialTheme.colorScheme.onPrimaryFixedVariant,
                onCopy = { scope.launch { onCopy(it) } },
                modifier = Modifier.width(ColorCellWidth)
            )

            ColorBlockWithFixedAccent(
                fixedText = "Secondary Fixed",
                fixedColor = MaterialTheme.colorScheme.secondaryFixed,
                fixedDimText = "Secondary Fixed Dim",
                fixedDimColor = MaterialTheme.colorScheme.secondaryFixedDim,
                onFixedText = "On Secondary Fixed",
                onFixedColor = MaterialTheme.colorScheme.onSecondaryFixed,
                onFixedVariantText = "On Secondary Fixed Variant",
                onFixedVariantColor = MaterialTheme.colorScheme.onSecondaryFixedVariant,
                onCopy = { scope.launch { onCopy(it) } },
                modifier = Modifier.width(ColorCellWidth)
            )

            ColorBlockWithFixedAccent(
                fixedText = "Tertiary Fixed",
                fixedColor = MaterialTheme.colorScheme.tertiaryFixed,
                fixedDimText = "Tertiary Fixed Dim",
                fixedDimColor = MaterialTheme.colorScheme.tertiaryFixedDim,
                onFixedText = "On Tertiary Fixed",
                onFixedColor = MaterialTheme.colorScheme.onTertiaryFixed,
                onFixedVariantText = "On Tertiary Fixed Variant",
                onFixedVariantColor = MaterialTheme.colorScheme.onTertiaryFixedVariant,
                onCopy = { scope.launch { onCopy(it) } },
                modifier = Modifier.width(ColorCellWidth)
            )
        }

        SurfaceSection(onCopy = { scope.launch { onCopy(it) } })

        DeprecatedSection(onCopy = { scope.launch { onCopy(it) } })

        ElevatedSurfaceLevelsSection(
            colors = elevatedSurfaceLevels,
            onCopy = { scope.launch { onCopy(it) } },
        )
    }
}


@Composable
private fun SurfaceSection(
    onCopy: (String) -> Unit
) {

    // The surface section has the width of 3 cell blocks without any padding in between
    val surfaceSectionWidth = ((ColorCellWidth * 3) + (ColorTableCellPadding * 2))

    Row(horizontalArrangement = Arrangement.spacedBy(ColorTableSectionPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
            Row(modifier = Modifier.width(surfaceSectionWidth)) {
                ColorBlockBasic(
                    text = "Surface Dim",
                    color = MaterialTheme.colorScheme.surfaceDim,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Surface",
                    color = MaterialTheme.colorScheme.surface,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Surface Bright",
                    color = MaterialTheme.colorScheme.surfaceBright,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
            Row(modifier = Modifier.width(surfaceSectionWidth)) {
                ColorBlockBasic(
                    text = "Surface Container Lowest",
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Surface Container Low",
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Surface Container",
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Surface Container High",
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Surface Container Highest",
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
            Row(modifier = Modifier.width(surfaceSectionWidth)) {
                ColorBlockBasic(
                    text = "On Surface",
                    color = MaterialTheme.colorScheme.onSurface,
                    onColor = MaterialTheme.colorScheme.surface,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "On Surface Variant",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    onColor = MaterialTheme.colorScheme.surface,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Outline",
                    color = MaterialTheme.colorScheme.outline,
                    onColor = MaterialTheme.colorScheme.surface,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )

                ColorBlockBasic(
                    text = "Outline Variant",
                    color = MaterialTheme.colorScheme.outlineVariant,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }

        // The last column with the inverse colors has the same size as the surface section with two
        // rows of big cells and a row of small cells with padding in between
        val inverseColumnHeight =
            (DefaultColorBlockStyle.bigCellHeight * 2) + DefaultColorBlockStyle.smallCellHeight + (ColorTableCellPadding * 2)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(inverseColumnHeight)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(ColorTableCellPadding)
            ) {
                ColorBlockBasic(
                    text = "Inverse Surface",
                    color = MaterialTheme.colorScheme.inverseSurface,
                    onColor = MaterialTheme.colorScheme.surface,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier.width(ColorCellWidth)
                )


                ColorBlockBasic(
                    text = "Inverse On Surface",
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    onColor = MaterialTheme.colorScheme.onSurface,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier.width(ColorCellWidth)
                )

                ColorBlockBasic(
                    text = "Inverse Primary",
                    color = MaterialTheme.colorScheme.inversePrimary,
                    onColor = MaterialTheme.colorScheme.primary,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier.width(ColorCellWidth)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(ColorTableSectionPadding),
                modifier = Modifier.width(ColorCellWidth)
            ) {
                ColorBlockBasic(
                    text = "Scrim",
                    color = MaterialTheme.colorScheme.scrim,
                    onColor = Color.White,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier.weight(1f)
                )

                ColorBlockBasic(
                    text = "Shadow?",
                    color = Color.Black,
                    onColor = Color.White,
                    onCopy = onCopy,
                    style = ForceSmallColorBlockStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Colors available inside MaterialTheme.colorScheme that don't have a dedicated section in the
 * "Color Roles" table
 */
@Composable
private fun DeprecatedSection(
    onCopy: (String) -> Unit
) {
    Column {
        Text(
            text = "> DEPRECATED",
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(ColorTableSectionPadding))

        Row(horizontalArrangement = Arrangement.spacedBy(ColorTableCellPadding)) {
            ColorBlockPair(
                text = "Background",
                color = MaterialTheme.colorScheme.background,
                onCopy = onCopy,
                modifier = Modifier.width(ColorCellWidth)
            )

            ColorBlockBasic(
                text = "Surface variant",
                color = MaterialTheme.colorScheme.surfaceVariant,
                onColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onCopy = onCopy,
                modifier = Modifier.width(ColorCellWidth)
            )

            ColorBlockBasic(
                text = "Surface tint",
                color = MaterialTheme.colorScheme.surfaceTint,
                onColor = MaterialTheme.colorScheme.surface,
                onCopy = onCopy,
                modifier = Modifier.width(ColorCellWidth)
            )
        }
    }
}

/**
 * Legacy elevated surface colors
 */
@Composable
private fun ElevatedSurfaceLevelsSection(
    colors: ElevatedSurfaceLevels,
    onCopy: (String) -> Unit
) {
    Column {
        Text(
            text = "> LEGACY ELEVATED SURFACES",
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(ColorTableSectionPadding))

        // The surface section has the width of 3 cell blocks without any padding in between
        val surfaceSectionWidth = ((ColorCellWidth * 3) + (ColorTableCellPadding * 2))

        Row(modifier = Modifier.width(surfaceSectionWidth)) {
            ColorBlockBasic(
                text = "Surface at +1",
                color = colors.surfaceLevel1,
                onColor = MaterialTheme.colorScheme.onSurface,
                onCopy = onCopy,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            ColorBlockBasic(
                text = "Surface at +2",
                color = colors.surfaceLevel2,
                onColor = MaterialTheme.colorScheme.onSurface,
                onCopy = onCopy,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            ColorBlockBasic(
                text = "Surface at +3",
                color = colors.surfaceLevel3,
                onColor = MaterialTheme.colorScheme.onSurface,
                onCopy = onCopy,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            ColorBlockBasic(
                text = "Surface at +4",
                color = colors.surfaceLevel4,
                onColor = MaterialTheme.colorScheme.onSurface,
                onCopy = onCopy,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            ColorBlockBasic(
                text = "Surface at +5",
                color = colors.surfaceLevel5,
                onColor = MaterialTheme.colorScheme.onSurface,
                onCopy = onCopy,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
        }
    }
}

// Space between cells on the sections
private val ColorTableCellPadding = 4.dp

// Space between sections
private val ColorTableSectionPadding = 16.dp

// Width of a table cell
private val ColorCellWidth = 180.dp

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = "spec:width=2130px,height=2230px,dpi=440,orientation=portrait"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = "spec:width=2130px,height=2230px,dpi=440,orientation=portrait"
)
@Composable
private fun ColorRolesTablePreview() {
    // We don't need a ThemeColorPack for PreviewScreen.
    // Get just the dynamic ColorScheme we need.
    val context = LocalContext.current
    val colorScheme = if (isSystemInDarkTheme()) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }
    DynamicExportTheme(colorScheme = colorScheme) {
        ColorRolesTable()
    }
}