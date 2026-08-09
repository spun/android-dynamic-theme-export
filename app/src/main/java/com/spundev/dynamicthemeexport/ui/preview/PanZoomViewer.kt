package com.spundev.dynamicthemeexport.ui.preview

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

private const val TAG = "PanZoomViewer"

// Source:
//  - video: Gestures in Jetpack Compose (https://youtu.be/1tkVjBxdGrk)
//  - gist: https://gist.github.com/JolandaVerhoef/41bbacadead2ba3ce8014d67014efbdd
fun calculateDoubleTapOffset(
    zoom: Float,
    size: IntSize,
    tapOffset: Offset
): Offset {
    val newOffset = Offset(tapOffset.x, tapOffset.y)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}

@Composable
fun PanZoomViewer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val scrollable2DState = rememberScrollable2DState {
        offset -= it / scale
        it
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .scrollable2D(scrollable2DState)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        scale = if (scale == 1f) 4f else 1f
                        offset = calculateDoubleTapOffset(scale, size, tapOffset)
                    }
                )
            }
            .clipToBounds()
            .graphicsLayer {
                translationX = -offset.x * scale
                translationY = -offset.y * scale
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0f, 0f)
            },
        content = content,
    )
}
