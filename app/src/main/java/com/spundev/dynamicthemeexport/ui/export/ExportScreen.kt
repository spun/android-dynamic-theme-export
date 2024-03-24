package com.spundev.dynamicthemeexport.ui.export

import android.content.Intent
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.R
import com.spundev.dynamicthemeexport.data.ThemeColorPack
import com.spundev.dynamicthemeexport.util.freeScroll.freeScroll
import com.spundev.dynamicthemeexport.util.freeScroll.rememberFreeScrollState

@Composable
fun ExportScreen(
    themeColorPack: ThemeColorPack
) {
    var themeColorPackOutput by remember { mutableStateOf("") }
    LaunchedEffect(themeColorPack) {
        themeColorPackOutput = themeColorPack.toComposeThemeFile()
    }

    val context = LocalContext.current
    // Share
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, themeColorPackOutput)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    // Copy
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Copy and share icons
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
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
            FilledTonalIconButton(onClick = { context.startActivity(shareIntent) }) {
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
            Text(
                text = themeColorPackOutput,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily(Typeface.MONOSPACE)),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}