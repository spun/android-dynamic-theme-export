package com.spundev.dynamicthemeexport.util.freeScroll

import androidx.compose.foundation.checkScrollableContainerConstraints
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt

/**
 * Utility modifier that combines all necessary steps to use a [Modifier.scrollable2D].
 */
fun Modifier.freeScroll(
    state: FreeScrollState,
): Modifier {
    // We could have used Modifier.layout instead of creating a new Modifier.Node, but we can use
    // this as an example of how to use Modifier.Node.
    return this
        .clipToBounds()
        .scrollable2D(state.scrollable2DState) then FreeScrollElement(state)
}

private data class FreeScrollElement(
    val state: FreeScrollState
) : ModifierNodeElement<FreeScrollNode>() {
    override fun create(): FreeScrollNode = FreeScrollNode(state)

    override fun update(node: FreeScrollNode) {
        node.state = state
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "freeScroll"
        properties["state"] = state
    }
}

/**
 * Modifier that applies [FreeScrollState.offset] to the layout
 * This is really similar to [androidx.compose.foundation.ScrollNode] from Scroll.kt
 */
private class FreeScrollNode(
    var state: FreeScrollState
) : LayoutModifierNode, Modifier.Node() {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        // This checks if the container was measured with the infinity constraints in the direction
        // of scrolling and gives a detailed explanation. Instead of writing our own check, we are
        // reusing the one from [ScrollNode]. This is why it needs an Orientation and why we call it
        // twice.
        checkScrollableContainerConstraints(constraints, Orientation.Horizontal)
        checkScrollableContainerConstraints(constraints, Orientation.Vertical)

        val childConstraints = constraints.copy(
            maxHeight = Constraints.Infinity,
            maxWidth = Constraints.Infinity
        )
        val placeable = measurable.measure(childConstraints)
        val width = placeable.width.coerceAtMost(constraints.maxWidth)
        val height = placeable.height.coerceAtMost(constraints.maxHeight)
        val scrollHeight = placeable.height - height
        val scrollWidth = placeable.width - width
        state.maxOffset = IntOffset(scrollWidth, scrollHeight)
        return layout(width, height) {
            val scrollX = state.offset.x.fastRoundToInt().fastCoerceIn(0, scrollWidth)
            val scrollY = state.offset.y.fastRoundToInt().fastCoerceIn(0, scrollHeight)
            val xOffset = -scrollX
            val yOffset = -scrollY

            // Tagging as direct manipulation, such that consumers of this offset can decide whether
            // to exclude this offset on their coordinates calculation. Such as whether an
            // `approachLayout` will animate it or directly apply the offset without animation.
            withMotionFrameOfReferencePlacement {
                placeable.placeRelativeWithLayer(xOffset, yOffset)
            }
        }
    }
}
