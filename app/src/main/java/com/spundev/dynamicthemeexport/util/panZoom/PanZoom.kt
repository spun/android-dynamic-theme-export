package com.spundev.dynamicthemeexport.util.panZoom

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.util.fastAny
import com.spundev.dynamicthemeexport.util.PanLayoutElement
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Add pan and zoom gesture handling.
 */
fun Modifier.panZoom(state: PanZoomState): Modifier = this
    .clipToBounds()
    .scrollable2D(
        state = state.scrollable2DState,
        flingBehavior = state.flingBehavior
    )
    // Split gestures from layout so the gestures are not only read when done on top of the content.
    .then(PanZoomGestureElement(state))
    .then(PanLayoutElement(state))

/**
 * Element wrapper for [PanZoomGestureNode].
 */
private data class PanZoomGestureElement(
    val state: PanZoomState
) : ModifierNodeElement<PanZoomGestureNode>() {

    override fun create(): PanZoomGestureNode = PanZoomGestureNode(state)

    override fun update(node: PanZoomGestureNode) {
        node.updateState(state)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "panZoomGesture"
        properties["state"] = state
    }
}

/**
 * Detect zoom gestures (pinch-to-zoom and double-tap-to-zoom) and applies them to the state.
 */
private class PanZoomGestureNode(
    state: PanZoomState
) : DelegatingNode() {

    var state: PanZoomState = state
        private set

    // Track the double-tap zoom animation so we can cancel it if a new gesture starts.
    private var scaleAnimationJob: Job? = null

    // Pinch to zoom
    private val transformInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectZoomGesture(
                onGestureStart = {
                    // Reset multitouch flag
                    state.gestureWasMultiTouch = false
                },
                onZoom = { centroid, zoomChange ->
                    // Mark this as a multitouch-driven change so the fling animation after the
                    // gesture is skipped. See state.flingBehavior for more info.
                    state.gestureWasMultiTouch = true
                    // Don't fight the zoom animation for control of offset
                    scaleAnimationJob?.cancel()
                    state.applyZoom(zoomChange = zoomChange, centroid = centroid)
                }
            )
        }
    )

    // Double tap to zoom
    private val tapInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTapGestures(
                onDoubleTap = { centroid ->
                    // Cancel any previous zoom animation before starting a new one
                    scaleAnimationJob?.cancel()
                    scaleAnimationJob = coroutineScope.launch {
                        state.animateZoomToggle(centroid = centroid)
                    }
                }
            )
        }
    )

    override fun onAttach() {
        // Add callback so state can interrupt an in-progress double-tap zoom animation if needed.
        state.onGestureInterrupt = { scaleAnimationJob?.cancel() }
    }

    override fun onDetach() {
        // Remove onGestureInterrupt callback when detached.
        state.onGestureInterrupt = null
    }

    fun updateState(newState: PanZoomState) {
        if (newState === state) return
        state.onGestureInterrupt = null // "detach" from the old state
        state = newState
        state.onGestureInterrupt = { scaleAnimationJob?.cancel() } // "attach" to the new one
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
        var zoom = 1f
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        awaitFirstDown(requireUnconsumed = false)
        onGestureStart()
        do {
            val event = awaitPointerEvent()
            val isMultiTouch = event.changes.size > 1
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
