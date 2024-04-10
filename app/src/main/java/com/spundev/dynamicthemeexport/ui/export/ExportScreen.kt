package com.spundev.dynamicthemeexport.ui.export

import android.content.Intent
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.R
import com.spundev.dynamicthemeexport.data.ColorFormat
import com.spundev.dynamicthemeexport.data.ThemeColorPack
import com.spundev.dynamicthemeexport.util.freeScroll.freeScroll
import com.spundev.dynamicthemeexport.util.freeScroll.rememberFreeScrollState

@Composable
fun ExportScreen(
    themeColorPack: ThemeColorPack
) {
    var themeColorPackOutput by remember { mutableStateOf("") }
    var colorFormat: ColorFormat by remember { mutableStateOf(ColorFormat.SRGBInteger) }
    LaunchedEffect(themeColorPack, colorFormat) {
        themeColorPackOutput = themeColorPack.toComposeThemeFile(colorFormat)
    }

    val context = LocalContext.current
    // Copy
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Copy and share icons
        Row(modifier = Modifier.fillMaxWidth()) {
            ColorFormatSelection(
                colorFormat = colorFormat,
                onColorFormatChange = { colorFormat = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Copy
            FilledTonalIconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(themeColorPackOutput))
                    Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_content_copy_24),
                    contentDescription = "Copy to clipboard"
                )
            }
            // Share
            FilledTonalIconButton(onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, themeColorPackOutput)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
            }
        }

        // Text preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .freeScroll(rememberFreeScrollState())
        ) {
            SelectionContainer {
                Text(
                    text = themeColorPackOutput,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily(Typeface.MONOSPACE)),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ColorFormatSelection(
    colorFormat: ColorFormat,
    onColorFormatChange: (ColorFormat) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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
                            Icon(Icons.Outlined.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}