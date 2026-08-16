package com.spundev.dynamicthemeexport.ui.export

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.R
import com.spundev.dynamicthemeexport.data.ColorFormat
import com.spundev.dynamicthemeexport.data.ColorFormatSaver
import com.spundev.dynamicthemeexport.data.ThemeColorPack
import com.spundev.dynamicthemeexport.data.ThemeExport
import com.spundev.dynamicthemeexport.ui.theme.DynamicExportTheme
import com.spundev.dynamicthemeexport.util.DisplayCorners
import com.spundev.dynamicthemeexport.util.gestures.freeScroll.freeScroll
import com.spundev.dynamicthemeexport.util.gestures.freeScroll.rememberFreeScrollState
import com.spundev.dynamicthemeexport.util.rememberDisplayCorners
import kotlinx.coroutines.launch

@Composable
fun ExportScreen(
    themeColorPack: ThemeColorPack,
    isDarkTheme: Boolean,
) {
    var themeExportOutput by remember { mutableStateOf(ThemeExport.Empty) }
    var colorFormat: ColorFormat by rememberSaveable(stateSaver = ColorFormatSaver) {
        mutableStateOf(ColorFormat.SRGBInteger)
    }
    LaunchedEffect(themeColorPack, colorFormat, isDarkTheme) {
        themeExportOutput = themeColorPack.toComposeThemeExport(colorFormat, isDarkTheme)
    }

    // Copy to clipboard
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    val displayCorners = rememberDisplayCorners()
    ExportScreenContent(
        colorFormat = colorFormat,
        themeExport = themeExportOutput,
        onColorFormatChange = { colorFormat = it },
        onCopy = {
            scope.launch {
                clipboard.setClipEntry(
                    clipEntry = ClipData.newPlainText(
                        /* label = */ themeExportOutput.code,
                        /* text = */ themeExportOutput.code
                    ).toClipEntry()
                )
                // Only show a toast for Android 12 (32) and lower.
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
                }
            }
        },
        onShare = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, themeExportOutput.code)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        },
        displayCorners = displayCorners
    )
}

@Composable
private fun ExportScreenContent(
    colorFormat: ColorFormat,
    themeExport: ThemeExport,
    onColorFormatChange: (ColorFormat) -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    displayCorners: DisplayCorners,
    windowInsets: WindowInsets = WindowInsets.safeDrawing,
) {
    val contentPadding = 8.dp
    val horizontalInsets = windowInsets.only(WindowInsetsSides.Horizontal)
    Column(
        modifier = Modifier
            .windowInsetsPadding(horizontalInsets)
            .padding(
                top = contentPadding,
                start = contentPadding,
                end = contentPadding,
            )
    ) {
        ExportOptionsBar(
            colorFormat = colorFormat,
            onColorFormatChange = onColorFormatChange,
            onCopy = onCopy,
            onShare = onShare,
            modifier = Modifier
        )
        ExportCodeViewer(
            themeExport = themeExport,
            displayCorners = displayCorners,
            // Let ExportCodeViewer handle the bottom value of our contentPadding
            contentPadding = PaddingValues(bottom = contentPadding),
            windowInsets = windowInsets.only(WindowInsetsSides.Bottom),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ExportOptionsBar(
    colorFormat: ColorFormat,
    onColorFormatChange: (ColorFormat) -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Copy and share icons
    Row(modifier = modifier.fillMaxWidth()) {
        ColorFormatSelection(
            colorFormat = colorFormat,
            onColorFormatChange = onColorFormatChange
        )

        Spacer(modifier = Modifier.weight(1f))

        // Copy
        FilledTonalIconButton(onClick = onCopy) {
            Icon(
                painter = painterResource(R.drawable.ic_content_copy_24),
                contentDescription = "Copy to clipboard"
            )
        }
        // Share
        FilledTonalIconButton(onClick = onShare) {
            Icon(
                painter = painterResource(R.drawable.ic_share_24),
                contentDescription = "Share"
            )
        }
    }
}

@Composable
private fun ColorFormatSelection(
    colorFormat: ColorFormat,
    onColorFormatChange: (ColorFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        TextButton(onClick = { expanded = true }) {
            Text(text = "Change format")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val options = listOf(
                "0xFF0000FF" to ColorFormat.SRGBInteger,
                "red = 1f, green = 1f, blue = 1f" to ColorFormat.FloatComponents,
                "red = 0xFF, green = 0xFF, blue = 0xFF" to ColorFormat.IntegerComponentsHex,
                "red = 255, green = 255, blue = 255" to ColorFormat.IntegerComponents,
            )

            options.forEach { (description, format) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Color($description)",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onColorFormatChange(format)
                        expanded = false
                    },
                    leadingIcon = {
                        if (format == colorFormat) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ExportCodeViewer(
    themeExport: ThemeExport,
    displayCorners: DisplayCorners,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
) {
    val fallbackShape = MaterialTheme.shapes.small
    val displayAwareShape = rememberDisplayCornerAwareShape(
        displayCorners = displayCorners,
        contentPadding = contentPadding,
        windowInsets = windowInsets,
        baseShape = fallbackShape
    )

    // Make sure we always have a shape for our code viewer
    val codeViewerShape = displayAwareShape ?: BoxShapeAndPadding(
        shape = fallbackShape,
        innerBottomPadding = 8.dp
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Bottom))
            .clip(codeViewerShape.shape)
            .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, codeViewerShape.shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .freeScroll(rememberFreeScrollState())
    ) {
        SelectionContainer {
            Text(
                text = themeExport.code,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 8.dp,
                    end = 8.dp,
                    bottom = codeViewerShape.innerBottomPadding
                )
            )
        }
        // Use same style from our code Text composable to make sure the position is correct
        Text(
            text = themeExport.gutter,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = codeViewerShape.innerBottomPadding
            )
        )
    }
}


@Preview
@Composable
private fun ExportScreenContentWithBottomInsetBiggerThanCornerRadiusPreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    val topStart = 0.dp
    val topEnd = 0.dp
    val bottomEnd = 48.dp
    val bottomStart = 48.dp
    val displayCorners = DisplayCorners(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
    )
    val previewShape = RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
    )

    val windowInsets = WindowInsets(bottom = 96.dp)

    // We don't need a ThemeColorPack for PreviewScreen.
    // Get just the dynamic ColorScheme we need.
    val context = LocalContext.current
    val colorScheme = if (isSystemInDarkTheme()) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    DynamicExportTheme(colorScheme = colorScheme) {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .clip(previewShape)
                .background(Color.White)
        ) {
            ExportScreenContent(
                colorFormat = ColorFormat.SRGBInteger,
                themeExport = ThemeExport(
                    code = AnnotatedString(text.repeat(6)),
                    gutter = AnnotatedString("")
                ),
                onColorFormatChange = { },
                onCopy = { },
                onShare = { },
                displayCorners = displayCorners,
                windowInsets = windowInsets,
            )

            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(windowInsets)
                    .background(Color.Gray)
            )
        }
    }
}

@Preview
@Composable
private fun ExportScreenContentWithBottomInsetSmallerThanCornerRadiusPreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    val topStart = 0.dp
    val topEnd = 0.dp
    val bottomEnd = 48.dp
    val bottomStart = 48.dp
    val displayCorners = DisplayCorners(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
    )
    val previewShape = RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
    )

    val windowInsets = WindowInsets(bottom = 12.dp)

    // We don't need a ThemeColorPack for PreviewScreen.
    // Get just the dynamic ColorScheme we need.
    val context = LocalContext.current
    val colorScheme = if (isSystemInDarkTheme()) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    DynamicExportTheme(colorScheme = colorScheme) {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .clip(previewShape)
                .background(Color.White)
        ) {
            ExportScreenContent(
                colorFormat = ColorFormat.SRGBInteger,
                themeExport = ThemeExport(
                    code = AnnotatedString(text.repeat(6)),
                    gutter = AnnotatedString("")
                ), onColorFormatChange = { },
                onCopy = { },
                onShare = { },
                displayCorners = displayCorners,
                windowInsets = windowInsets
            )

            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(windowInsets)
                    .background(Color.Gray)
            )
        }
    }
}

@Preview
@Composable
private fun ExportScreenContentWithBottomInsetSmallerThanCornerRadiusAndWeirdShapePreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    val topStart = 0.dp
    val topEnd = 0.dp
    val bottomEnd = 0.dp
    val bottomStart = 92.dp
    val displayCorners = DisplayCorners(
        topStart = topStart,
        topEnd = topEnd,
        /* Use Unspecified instead of 0 to see if everything works */
        bottomEnd = Dp.Unspecified,
        bottomStart = bottomStart,
    )
    val previewShape = RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
    )

    val windowInsets = WindowInsets(bottom = 12.dp)

    // We don't need a ThemeColorPack for PreviewScreen.
    // Get just the dynamic ColorScheme we need.
    val context = LocalContext.current
    val colorScheme = if (isSystemInDarkTheme()) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    DynamicExportTheme(colorScheme = colorScheme) {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .clip(previewShape)
                .background(Color.White)
        ) {
            ExportScreenContent(
                colorFormat = ColorFormat.SRGBInteger,
                themeExport = ThemeExport(
                    code = AnnotatedString(text.repeat(6)),
                    gutter = AnnotatedString("")
                ), onColorFormatChange = { },
                onCopy = { },
                onShare = { },
                displayCorners = displayCorners,
                windowInsets = windowInsets
            )

            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(windowInsets)
                    .background(Color.Gray)
            )
        }
    }
}

@Preview
@Composable
private fun ExportScreenContentWithUnspecifiedCornerRadiusPreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    val displayCorners = DisplayCorners.Unspecified
    val windowInsets = WindowInsets(bottom = 48.dp)

    // We don't need a ThemeColorPack for PreviewScreen.
    // Get just the dynamic ColorScheme we need.
    val context = LocalContext.current
    val colorScheme = if (isSystemInDarkTheme()) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    DynamicExportTheme(colorScheme = colorScheme) {
        Box(modifier = Modifier.background(Color.White)) {
            ExportScreenContent(
                colorFormat = ColorFormat.SRGBInteger,
                themeExport = ThemeExport(
                    code = AnnotatedString(text.repeat(6)),
                    gutter = AnnotatedString("")
                ), onColorFormatChange = { },
                onCopy = { },
                onShare = { },
                displayCorners = displayCorners,
                windowInsets = windowInsets
            )

            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(windowInsets)
                    .background(Color.Gray)
            )
        }
    }
}
