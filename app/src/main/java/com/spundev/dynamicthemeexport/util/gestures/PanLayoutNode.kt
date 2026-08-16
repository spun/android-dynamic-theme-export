package com.spundev.dynamicthemeexport.util.gestures

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

/**
 * Element wrapper for [PanLayoutNode].
 */
internal data class PanLayoutElement(
    val state: BasePanState
) : ModifierNodeElement<PanLayoutNode>() {

    override fun create(): PanLayoutNode = PanLayoutNode(state)

    override fun update(node: PanLayoutNode) {
        node.state = state
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "panLayout"
        properties["state"] = state
    }
}

/**
 * Measure content unconstrained, report sizes and applies pan and zoom changes.
 */
internal class PanLayoutNode(
    var state: BasePanState
) : Modifier.Node(), LayoutModifierNode {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        // Measure content unconstrained
        val placeable = measurable.measure(Constraints())
        state.updateSizes(
            viewport = IntSize(constraints.maxWidth, constraints.maxHeight),
            content = IntSize(placeable.width, placeable.height)
        )
        return layout(constraints.maxWidth, constraints.maxHeight) {
            // Apply pan and zoom values.
            placeable.placeRelativeWithLayer(0, 0) {
                translationX = -state.offset.x * state.scale
                translationY = -state.offset.y * state.scale
                scaleX = state.scale
                scaleY = state.scale
                transformOrigin = TransformOrigin(0f, 0f)
            }
        }
    }
}
