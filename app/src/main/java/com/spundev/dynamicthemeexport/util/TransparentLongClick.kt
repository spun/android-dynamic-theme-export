package com.spundev.dynamicthemeexport.util

import android.view.MotionEvent
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny

/**
 * Add long-click listener that doesn't consume pointer events.
 * This is an alternative to Modifier.combinedClickable so our ancestor gesture detectors (the
 * panZoom modifier) can receive unconsumed events.
 */
fun Modifier.transparentLongClick(
    label: String? = null,
    onLongClick: () -> Unit,
): Modifier = this.then(
    TransparentLongClickElement(
        onLongClickLabel = label,
        onLongClick = onLongClick
    )
)

/**
 * Element wrapper for [TransparentLongClickNode].
 */
private data class TransparentLongClickElement(
    val onLongClickLabel: String?,
    val onLongClick: () -> Unit,
) : ModifierNodeElement<TransparentLongClickNode>() {

    override fun create(): TransparentLongClickNode = TransparentLongClickNode(
        onLongClickLabel = onLongClickLabel,
        onLongClick = onLongClick
    )

    override fun update(node: TransparentLongClickNode) {
        node.onLongClickLabel = onLongClickLabel
        node.onLongClick = onLongClick
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "transparentLongClick"
        properties["onLongClickLabel"] = onLongClickLabel
    }
}

/**
 * Detect long-click actions.
 */
private class TransparentLongClickNode(
    var onLongClickLabel: String?,
    var onLongClick: () -> Unit,
) : DelegatingNode(), SemanticsModifierNode {

    private val interactionSource = MutableInteractionSource()

    // Use ripple().create() to build the node wired to our interactionSource.
    private val indicationNode = delegate(ripple().create(interactionSource))

    // Long-click listener
    private val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTransparentLongPress(
                interactionSource = interactionSource,
                onLongPress = { onLongClick() }
            )
        }
    )

    // Add semantics (we should investigate if this is the correct way to do it).
    override fun SemanticsPropertyReceiver.applySemantics() {
        onLongClick(onLongClickLabel) { onLongClick(); true }
    }
}

/**
 * Similar to [androidx.compose.foundation.gestures.detectTapGestures] with just long-press related
 * changes that doesn't consume pointer events.
 */
private suspend fun PointerInputScope.detectTransparentLongPress(
    interactionSource: MutableInteractionSource,
    onLongPress: (Offset) -> Unit
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val press = PressInteraction.Press(down.position)
        interactionSource.tryEmit(press)

        when (waitForLongPress()) {
            is LongPressResult.Success -> {
                interactionSource.tryEmit(PressInteraction.Release(press))
                onLongPress(down.position)
                // At this point we're past the double-tap window. We could consume further events
                // so they don't reach other ancestor gesture detectors. For example, no pan after
                // long press.
                // Uncomment if we want to enable the behavior we described.
                /* do {
                    val event = awaitPointerEvent()
                    event.changes.fastForEach { it.consume() }
                } while (event.changes.fastAny { it.pressed }) */
            }

            is LongPressResult.Released -> {
                interactionSource.tryEmit(PressInteraction.Release(press))
            }

            is LongPressResult.Canceled -> {
                interactionSource.tryEmit(PressInteraction.Cancel(press))
            }
        }
    }
}

/**
 * Copy of [androidx.compose.foundation.gestures.waitForLongPress] we can use in our Modifier.
 */
private suspend fun AwaitPointerEventScope.waitForLongPress(
    pass: PointerEventPass = PointerEventPass.Main
): LongPressResult {
    var result: LongPressResult = LongPressResult.Canceled
    try {
        withTimeout(viewConfiguration.longPressTimeoutMillis) {
            while (true) {
                val event = awaitPointerEvent(pass)
                if (event.changes.fastAll { it.changedToUp() }) {
                    // All pointers are up
                    result = LongPressResult.Released(event.changes[0])
                    break
                }

                if (event.isDeepPress) {
                    result = LongPressResult.Success
                    break
                }

                if (
                    event.changes.fastAny {
                        it.isConsumed || it.isOutOfBounds(size, extendedTouchPadding)
                    }
                ) {
                    result = LongPressResult.Canceled
                    break
                }

                // Check for cancel by position consumption. We can look on the Final pass of the
                // existing pointer event because it comes after the pass we checked above.
                val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                if (consumeCheck.changes.fastAny { it.isConsumed }) {
                    result = LongPressResult.Canceled
                    break
                }
            }
        }
    } catch (_: PointerEventTimeoutCancellationException) {
        return LongPressResult.Success
    }
    return result
}

private sealed class LongPressResult {
    /** Long press was triggered */
    object Success : LongPressResult()

    /** All pointers were released without long press being triggered */
    class Released(val finalUpChange: PointerInputChange) : LongPressResult()

    /** The gesture was canceled */
    object Canceled : LongPressResult()
}

private val PointerEvent.isDeepPress: Boolean
    get() = classification == MotionEvent.CLASSIFICATION_DEEP_PRESS
