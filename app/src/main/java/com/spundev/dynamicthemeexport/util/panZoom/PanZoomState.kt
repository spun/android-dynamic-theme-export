package com.spundev.dynamicthemeexport.util.panZoom

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.Scrollable2DState
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import kotlin.math.max

/**
 * Create and remember a PanZoomState instance.
 */
@Composable
fun rememberPanZoomState(): PanZoomState {
    // Our PanZoomState class might need to override the default fling behavior (see flingBehavior
    // inside PanZoomState to know more). If no override is needed, PanZoomState needs access to the
    // default FlingBehavior, but we can only get it easily from within a composable.
    val defaultFlingBehavior = ScrollableDefaults.flingBehavior()
    return rememberSaveable(saver = PanZoomState.Saver(defaultFlingBehavior)) {
        PanZoomState(defaultFlingBehavior)
    }.also {
        it.defaultFlingBehavior = defaultFlingBehavior
    }
}

@Stable
class PanZoomState(
    internal var defaultFlingBehavior: FlingBehavior
) {
    /**
     * The current zoom level applied to the content.
     */
    var scale by mutableFloatStateOf(1f)
        internal set

    /**
     * Position of the top-left point of the content against the viewport.
     */
    var offset by mutableStateOf(Offset.Zero)
        internal set

    /**
     * Last measured size of the area available to display the content.
     */
    private var viewportSize = IntSize.Zero

    /**
     * Last measured size of the content (unscaled).
     */
    private var contentSize = IntSize.Zero

    /**
     * The lowest zoom value necessary to fully display the content.
     * This value is only updated when [viewportSize] or [contentSize] change via [updateSizes].
     */
    private var minZoom = 1f

    /**
     * Indicates if the last gesture done was a multitouch gesture.
     * This is used to skip the fling animation that would be applied after a zoom gesture.
     * See [flingBehavior] to know why we are doing this.
     */
    internal var gestureWasMultiTouch: Boolean = false

    /**
     * Check if the last gesture done was a multitouch gesture and reset the indicator value.
     */
    private fun consumeGestureWasMultiTouch(): Boolean {
        val was = gestureWasMultiTouch
        gestureWasMultiTouch = false
        return was
    }

    // Zoom-aware fling behavior
    // Fix an issue where, at the end of a zoom gesture, scrollable2D would apply an abrupt fling
    // animation with a velocity way higher than expected.
    // We believe this happens when one of the fingers leaves the screen before the other. his might
    // be interpreted as a fast motion between the center of the pinch and the position of the last
    // finger that leaves the screen.
    // With this custom FlingBehavior, we are just skipping any fling animation that should be
    // applied after a zoom gesture. This is not ideal, but it will fix that "random" launch we were
    // getting.
    internal val flingBehavior: FlingBehavior = object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            if (consumeGestureWasMultiTouch()) return 0f
            return with(defaultFlingBehavior) { performFling(initialVelocity) }
        }
    }

    /**
     * Callback used to cancel ongoing animations managed by other pieces of code.
     * For example: The double-tap to zoom gesture animation is managed by our modifier. If while in
     * the zoom animation the user drags the content, we need to have a way to indicate our modifier
     * that the animation Job should be canceled.
     */
    internal var onGestureInterrupt: (() -> Unit)? = null

    /**
     * Update both [viewportSize] and [contentSize] values.
     * This will also update the [minZoom] value and make sure the [offset] boundaries are applied.
     */
    internal fun updateSizes(viewport: IntSize, content: IntSize) {
        if (viewport == viewportSize && content == contentSize) return

        viewportSize = viewport
        contentSize = content

        minZoom = if (content != IntSize.Zero && viewport != IntSize.Zero) {
            minOf(
                viewport.width / content.width.toFloat(),
                viewport.height / content.height.toFloat()
            )
        } else 1f

        // Re-apply constraints with the new sizes in case they are necessary.
        scale = scale.coerceAtLeast(minZoom)
        offset = clamp(offset, scale)
    }

    /**
     * Apply the [zoomChange] to our [scale] value directly.
     * This can be used for a pinch-to-zoom gesture where [scale] changes in relation to the pointer
     * position.
     * For a fire-and-forget alternative see [animateZoomToggle].
     */
    fun applyZoom(zoomChange: Float, centroid: Offset) {
        val newScale = maxOf(minZoom, scale * zoomChange)
        // Find the content under the centroid and change offset to keep that same content under the
        // ongoing gesture.
        val contentUnderCentroid = offset + centroid / scale
        offset = clamp(contentUnderCentroid - centroid / newScale, newScale)
        scale = newScale
    }

    /**
     * Change current [scale] between a zoomed-in and a zoomed-out position with an animation.
     * Used for double-tap-to-zoom.
     */
    suspend fun animateZoomToggle(
        centroid: Offset,
        animationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow)
    ) {
        // Scale
        val defaultScale = max(1f, minZoom)
        val zoomedInScale = defaultScale * 4f
        val initialScale = scale
        val newScale = if (initialScale <= defaultScale * 1.01f) zoomedInScale else defaultScale

        // Offset
        val initialOffset = offset
        // Since tapOffset is the position of the viewport that receives the double
        // tap, we calculate what part of the content was behind tapOffset.
        val contentPoint = initialOffset + (centroid / initialScale)

        // We want to center the tapped area while we zoom in/out but that might put
        // us past the content boundaries. Calculate the final position we want and
        // then pull it back to a "legal" position.
        val viewportCenter = viewportSize.center.toOffset()
        val desiredTargetOffset = contentPoint - (viewportCenter / newScale)
        val targetOffset = clamp(desiredTargetOffset, newScale)

        // Since we may not end exactly on center, calculate where contentPoint will
        // actually be on screen at the end of the animation.
        val finalScreenPos = (contentPoint - targetOffset) * newScale

        // Start animation.
        // We animate scale directly (rather than a 0..1 progress we can use to lerp the scale and
        // offset) because we noticed some "jank" at the end with some spring animationSpec.
        //
        // As far as we know, a spring animation behaves like a real spring. A spring stretches less
        // and less as it nears its resting position and, in theory, it never exactly reaches the
        // target. Compose makes a judgment call and stops when the animation is "close enough",
        // then snaps to the target value.
        //
        // If we animate a "progress" from 0 to 1, the "close enough" happens by default once it's
        // within 0.01 of 1 in this case. If we multiply this progress through lerp by the distance
        // between initialScale and newScale, a zoom animation from 1 to 4, would transform that
        // 0.01 "close enough" jump into a 0.03 one, and the bigger the zoom range, the bigger that
        // final jump.
        //
        // If instead of animating a progress we animate the scale directly, we won't see that
        // unexpected jump we got at the end of the animation when scale was derived from progress.
        animate(
            initialValue = initialScale,
            targetValue = newScale,
            animationSpec = animationSpec
        ) { currentScale, /* velocity */ _ ->
            // Recover a 0..1 progress position from the scale itself so "screenPos" follows the
            // scale animation.
            val progress = (currentScale - initialScale) / (newScale - initialScale)
            val screenPos = androidx.compose.ui.geometry.lerp(
                centroid,
                finalScreenPos,
                progress
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

    /**
     * Modify [raw] [offset] changes so the content always stays within the boundaries of our
     * viewport.
     */
    private fun clamp(raw: Offset, atScale: Float): Offset {
        if (contentSize == IntSize.Zero || viewportSize == IntSize.Zero) return raw
        val maxX = (contentSize.width - viewportSize.width / atScale).coerceAtLeast(0f)
        val maxY = (contentSize.height - viewportSize.height / atScale).coerceAtLeast(0f)
        return Offset(raw.x.coerceIn(0f, maxX), raw.y.coerceIn(0f, maxY))
    }

    /**
     * Simple consumeScrollDelta callback that always signals the consumption of the whole delta.
     * See [consumedScrollable2DCallback] to know why we intentionally report an incorrect consumed
     * value.
     * Without any overscroll effect, this gives us a behavior similar to a photo viewer app where
     * a diagonal gesture will keep going on the remaining axis even if the other one has already
     * "hit a wall".
     */
    private val simpleScrollable2DCallback: (Offset) -> Offset = { delta ->
        // Don't fight the zoom animation for control of offset
        onGestureInterrupt?.invoke()
        val newOffset = offset - (delta / scale)
        offset = clamp(newOffset, scale)
        // report we consumed everything to avoid overscroll animation
        delta
    }

    /**
     * Alternative implementation of a consumeScrollDelta callback that calculates the delta
     * consumption as intended. Although this should be the correct implementation, it causes a
     * weird effect when an axis reaches its limit, and we signal that we have stopped consuming it.
     * When this happens, instead of still being called with a delta for the other axis, it stops
     * receiving the expected delta values for that ongoing axis.
     * The result is a scrollable composable that stops when it hits a wall, no matter the angle at
     * which it hits.
     * With a stretch overscroll effect this is less noticeable, but you can still tell that
     * something has stopped moving sooner than it should.
     */
    private val consumedScrollable2DCallback: (Offset) -> Offset = { delta ->
        // Don't fight the zoom animation for control of offset
        onGestureInterrupt?.invoke()
        // Store the current offset before applying the new delta
        val initialOffset = offset
        // Calculate the desired new value and make sure it stays within the offset boundaries
        offset = clamp(offset - delta / scale, scale)
        // Report how much was actually consumed
        (initialOffset - offset) * scale
    }

    /**
     * Scrollable2DState that will be used with Modifier.scrollable2D to calculate the correct
     * [offset].
     */
    val scrollable2DState = Scrollable2DState(simpleScrollable2DCallback)

    companion object {
        // Saver for scale and offset
        fun Saver(defaultFlingBehavior: FlingBehavior) = listSaver(
            save = { listOf(it.scale, it.offset.x, it.offset.y) },
            restore = { (scale, offsetX, offsetY) ->
                // scale and offset are restored without clamping against the possibly new minZoom
                // and content bounds.
                // This is safe because PanZoomLayoutNode.measure() will call state.updateSizes() on
                // the next measure pass and makes sure both values are within the limits.
                PanZoomState(defaultFlingBehavior).apply {
                    this.scale = scale
                    this.offset = Offset(offsetX, offsetY)
                }
            }
        )
    }
}
