package com.spundev.dynamicthemeexport.util.freeScroll

import androidx.compose.foundation.gestures.Scrollable2DState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.roundToInt

@Composable
fun rememberFreeScrollState(): FreeScrollState {
    return remember { FreeScrollState() }
}

@Stable
class FreeScrollState() {
    /**
     * Current scroll position
     */
    var offset by mutableStateOf(Offset.Zero)
        internal set

    /**
     * Maximum bound for [offset], or [IntOffset.Max] if still unknown
     */
    var maxOffset: IntOffset by mutableStateOf(IntOffset.Max)
        internal set

    /**
     * Simple consumeScrollDelta callback that always signals the consumption of the whole delta.
     * See [consumedScrollable2DCallback] to know why we intentionally report an incorrect consumed
     * value.
     * Without any overscroll effect, this gives us a behavior similar to a photo viewer app where
     * a diagonal gesture will keep going on the remaining axis even if the other one has already
     * "hit a wall".
     */
    private val simpleScrollable2DCallback: (Offset) -> Offset = { delta ->
        val newX = (offset.x - delta.x).roundToInt().fastCoerceIn(0, maxOffset.x)
        val newY = (offset.y - delta.y).roundToInt().fastCoerceIn(0, maxOffset.y)
        offset = Offset(newX.toFloat(), newY.toFloat())
        delta
    }

    /**
     * Alternative implementation of a consumeScrollDelta callback that calculates the delta
     * consumption as intended. Although this should be the correct implementation, it causes a
     * weird effect when an axis reaches its limit and we signal that we have stopped consuming it.
     * When this happens, instead of still being called with a delta for the other axis, it stops
     * receiving the expected delta values for that ongoing axis.
     * The result is a scrollable composable that stops when it hits a wall, no matter the angle at
     * which it hits.
     * With a stretch overscroll effect this is less noticeable, but you can still tell that
     * something has stopped moving sooner than it should.
     */
    private val consumedScrollable2DCallback: (Offset) -> Offset = { delta ->
        // Store the current offset before applying the new delta
        val oldOffset = offset
        // Calculate the desired new value (before using coerce to limit it to bounds)
        val desiredOffset = offset - delta
        // Coerce values so they stay within allowed bounds
        val coercedX = desiredOffset.x.coerceIn(0f, maxOffset.x.toFloat())
        val coercedY = desiredOffset.y.coerceIn(0f, maxOffset.y.toFloat())
        // Update the offset to the new coerced one
        offset = Offset(coercedX, coercedY)
        // Compute actual consumed delta
        val consumed = Offset(coercedX - oldOffset.x, coercedY - oldOffset.y)
        consumed
    }

    /**
     * Scrollable2DState that will be used with Modifier.scrollable2D to calculate the correct
     * [offset].
     */
    val scrollable2DState = Scrollable2DState(simpleScrollable2DCallback)
}
