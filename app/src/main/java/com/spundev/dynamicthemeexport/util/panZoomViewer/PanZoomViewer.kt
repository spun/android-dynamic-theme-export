package com.spundev.dynamicthemeexport.util.panZoomViewer

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

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

private fun Offset.coerceIn(lower: Offset, upper: Offset): Offset = Offset(
    x = x.coerceIn(lower.x, upper.x),
    y = y.coerceIn(lower.y, upper.y)
)

@Composable
fun PanZoomViewer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    val coroutineScope = rememberCoroutineScope()

    var zoomAnimationJob: Job? by remember { mutableStateOf(null) }

    val minZoom = remember(viewportSize, size) {
        if (viewportSize != IntSize.Zero && size != IntSize.Zero) {
            minOf(
                viewportSize.width.toFloat() / size.width.toFloat(),
                viewportSize.height.toFloat() / size.height.toFloat()
            )
        } else 1f
    }

    fun maxOffset(): Offset {
        val overflowX = size.width * scale - viewportSize.width
        val overflowY = size.height * scale - viewportSize.height
        return Offset(
            x = if (overflowX <= 0f) 0f else overflowX / scale,
            y = if (overflowY <= 0f) 0f else overflowY / scale
        )
    }

    // Alt to maxOffset that uses the newScale value instead of the real one
    // TODO: merge both
    fun maxOffset(newScale: Float): Offset {
        val overflowX = size.width * newScale - viewportSize.width
        val overflowY = size.height * newScale - viewportSize.height
        return Offset(
            x = if (overflowX <= 0f) 0f else overflowX / newScale,
            y = if (overflowY <= 0f) 0f else overflowY / newScale
        )
    }

    val scrollable2DState = rememberScrollable2DState {
        // Don't fight the zoom animation for control of offset
        zoomAnimationJob?.cancel()
        val newOffset = offset - (it / scale)
        offset = newOffset.coerceIn(Offset.Zero, maxOffset())
        it
    }

    LaunchedEffect(viewportSize, size, minZoom /* not really needed */) {
        if (viewportSize != IntSize.Zero && size != IntSize.Zero) {
            scale = scale.coerceAtLeast(minZoom)
            offset = offset.coerceIn(Offset.Zero, maxOffset())
        }
    }

    val gestureWasMultiTouch = remember { mutableStateOf(false) }
    val baseFlingBehavior = ScrollableDefaults.flingBehavior()
    val zoomAwareFlingBehavior = remember(baseFlingBehavior) {
        object : FlingBehavior {
            // Fix an issue where, at the end of a zoom gesture, scrollable2D would apply an abrupt
            // fling animation with a velocity way higher than expected.
            // We believe this happens when one of the fingers leaves the screen before the other.
            // This might be interpreted as a fast motion between the center of the pinch and the
            // position of the last finger that leaves the screen.
            // With this custom FlingBehavior, we are just skipping any fling animation that should
            // be applied after a zoom gesture. This is not ideal, but it will fix that "random"
            // launch we were getting.
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                if (gestureWasMultiTouch.value) {
                    gestureWasMultiTouch.value = false
                    return 0f
                }
                return with(baseFlingBehavior) { performFling(initialVelocity) }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { viewportSize = it }
            // Pan
            .scrollable2D(scrollable2DState, flingBehavior = zoomAwareFlingBehavior)
            // Pinch to zoom
            .pointerInput(minZoom) {
                detectZoomGesture(
                    onGestureStart = { gestureWasMultiTouch.value = false },
                ) { centroid, zoomChange ->
                    gestureWasMultiTouch.value = true
                    // Don't fight the zoom animation for control of offset
                    zoomAnimationJob?.cancel()
                    val newScale = maxOf(minZoom, scale * zoomChange)
                    offset = (offset + centroid / scale - centroid / newScale)
                        .coerceIn(Offset.Zero, maxOffset())
                    scale = newScale
                }
            }
            // Double tap to zoom
            .pointerInput(minZoom) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        // Scale
                        val initialScale = scale
                        val newScale = if (initialScale <= 1f) 4f else max(1f, minZoom)

                        // Offset
                        val initialOffset = offset
                        // Since tapOffset is the position of the viewport that receives the double
                        // tap, we calculate what part of the content was behind tapOffset.
                        val contentPoint = initialOffset + (tapOffset / initialScale)

                        // We want to center the tapped area while we zoom in/out but that might put
                        // us past the content boundaries. Calculate the final position we want and
                        // then pull it back to a "legal" position.
                        val viewportCenter = viewportSize.center.toOffset()
                        val desiredTargetOffset = contentPoint - (viewportCenter / newScale)
                        val targetOffset = desiredTargetOffset.coerceIn(
                            lower = Offset.Zero,
                            upper = maxOffset(newScale)
                        )

                        // Since we may not end exactly on center, calculate where contentPoint will
                        // actually be on screen at the end of the animation.
                        val finalScreenPos = (contentPoint - targetOffset) * newScale

                        // Cancel any previous zoom animation before starting a new one
                        zoomAnimationJob?.cancel()
                        zoomAnimationJob = coroutineScope.launch {
                            animate(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = tween(10000)//SpringSpec(stiffness = Spring.StiffnessLow)
                            ) { value, /* velocity */ _ ->
                                val currentScale = lerp(initialScale, newScale, value)
                                // Move the tapped point from where the user tapped to its final
                                // spot.
                                // We don't lerp offset directly, that makes the tapped point drift
                                // in an arc since offset and scale would change linearly but
                                // independently. Instead, we calculate where contentPoint should be
                                // on screen at this point of the animation.
                                val screenPos = androidx.compose.ui.geometry.lerp(
                                    tapOffset,
                                    finalScreenPos,
                                    value
                                )
                                // We know where we want the tapped point to appear on screen at
                                // this point of the animation, and how zoomed in we are.
                                // Work backwards to find the offset.
                                val currentOffset = contentPoint - screenPos / currentScale
                                // Apply values
                                scale = currentScale
                                offset = currentOffset
                            }
                        }
                    }
                )
            }
            .clipToBounds(),
    ) {
        Box(
            content = content,
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart, unbounded = true)
                .onSizeChanged { size = it }
                .graphicsLayer {
                    translationX = -offset.x * scale
                    translationY = -offset.y * scale
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        )
        // TODO: Remove. Viewport center
        //  Spacer(
        //      Modifier
        //          .size(12.dp)
        //          .background(Color.Red)
        //          .align(Alignment.Center)
        //  )
    }
}

/**
 * Similar to [androidx.compose.foundation.gestures.detectTransformGestures] with just zoom related
 * changes.
 */
private suspend fun PointerInputScope.detectZoomGesture(
    onGestureStart: () -> Unit = {},
    onZoom: (centroid: Offset, zoomChange: Float) -> Unit
) {
    awaitEachGesture {
        var isMultiTouch: Boolean
        var zoom = 1f
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        awaitFirstDown(requireUnconsumed = false)
        onGestureStart()
        do {
            val event = awaitPointerEvent()
            isMultiTouch = event.changes.size > 1
            val canceled = event.changes.fastAny { it.isConsumed }
            if (!canceled && isMultiTouch) {
                val zoomChange = event.calculateZoom()

                if (!pastTouchSlop) {
                    zoom *= zoomChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize

                    if (zoomMotion > touchSlop) {
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    if (zoomChange != 1f) {
                        onZoom(centroid, zoomChange)
                    }

                    // Skip consume call to allow pan-while-zooming gesture
                    /* event.changes.fastForEach {
                        if (it.positionChanged()) {
                            it.consume()
                        }
                    } */
                }
            }
        } while (!canceled && event.changes.fastAny { it.pressed })
    }
}