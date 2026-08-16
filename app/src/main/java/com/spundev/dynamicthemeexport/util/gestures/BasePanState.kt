package com.spundev.dynamicthemeexport.util.gestures

import androidx.compose.foundation.gestures.Scrollable2DState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

@Stable
abstract class BasePanState {

    /**
     * Position of the top-left point of the content against the viewport.
     */
    var offset by mutableStateOf(Offset.Zero)
        internal set

    /**
     * Last measured size of the area available to display the content.
     */
    protected var viewportSize = IntSize.Zero

    /**
     * Last measured size of the content (unscaled).
     */
    protected var contentSize = IntSize.Zero

    /**
     * The current zoom level applied to the content.
     */
    internal open val scale: Float = 1f

    /**
     * Callback used to cancel ongoing animations managed by other pieces of code.
     * For example: The double-tap to zoom gesture animation managed by panZoom modifier.
     * If while in the zoom animation the user drags the content, we need to have a way to indicate
     * the modifier that the animation Job should be canceled.
     */
    internal var onGestureInterrupt: (() -> Unit)? = null

    /**
     * Update both [viewportSize] and [contentSize] values.
     * This will also make sure the [offset] boundaries are applied.
     */
    internal fun updateSizes(viewport: IntSize, content: IntSize) {
        if (viewport == viewportSize && content == contentSize) return
        onSizesChanged(viewport, content)
        viewportSize = viewport
        contentSize = content
        // Re-apply constraints with the new sizes in case they are necessary.
        offset = clamp(offset, scale)
    }

    /**
     * Called whenever [viewportSize] or [contentSize] change.
     * This can be used as a hook for subclasses that need to recompute size-dependent values.
     */
    protected open fun onSizesChanged(viewport: IntSize, content: IntSize) {}

    /**
     * Modify [raw] [offset] changes so the content always stays within the boundaries of our
     * viewport.
     */
    protected fun clamp(raw: Offset, atScale: Float): Offset {
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
     * NOTE: To check this with the overscroll effect, we would need to update our panZoom modifier
     *  to receive an OverscrollEffect and use it in scrollable2D and also in the overscroll
     *  modifier we would need to add to our Modifier.panZoom() implementation.
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
}
