package com.spundev.dynamicthemeexport.ui.export

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.spundev.dynamicthemeexport.util.DisplayCorners

/**
 * The [Shape] and inner bottom padding to apply to a box so its content isn't clipped by the
 * display's bottom corners.
 */
internal data class BoxShapeAndPadding(
    val shape: Shape,
    val innerBottomPadding: Dp,
)

/**
 * Calculates the [Shape] for the code viewer that avoids clipping content under the bottom
 * display corners.
 * This will adjust the bottom corners to fit the screen corners, but only when the bottom inset +
 * padding isn't enough to clear them naturally.
 * @return A [BoxShapeAndPadding] with the values required to create a Box that avoids the device's
 * corners, or `null` if there is no need to add a special shape to avoid them. A `null` value means
 * that the device's corners are not rounded, or that insets + padding already clears the corners.
 */
@Composable
internal fun rememberDisplayCornerAwareShape(
    displayCorners: DisplayCorners,
    contentPadding: PaddingValues,
    windowInsets: WindowInsets,
    baseShape: CornerBasedShape = MaterialTheme.shapes.small
): BoxShapeAndPadding? {
    val density = LocalDensity.current

    // If the sum of bottom inset and bottom padding is higher than the biggest bottom corner
    // radius, we don't need to adjust our shape to prevent the content from getting cut off.
    val paddingPlusInsetClearsRadius by remember(
        displayCorners,
        density,
        contentPadding,
        windowInsets
    ) {
        // Inset values should not be read during composition and the only recommended way is to use
        // the dedicated Modifier that will not cause recomposition when values change.
        // Since we really need to know if the available bottom inset we are going to apply (plus
        // the padding) will clear the device's display corners, we are using derivedStateOf to
        // limit recompositions.
        derivedStateOf {
            // Get the biggest radius from the bottom corners.
            val displayBottomStart = displayCorners.bottomStart
            val displayBottomEnd = displayCorners.bottomEnd
            // Make sure to not use maxOf with Dp.Unspecified
            val startRadius = if (displayBottomStart.isSpecified) displayBottomStart else 0.dp
            val endRadius = if (displayBottomEnd.isSpecified) displayBottomEnd else 0.dp
            val maxDisplayBottomRadius = maxOf(startRadius, endRadius)

            // Also get the bottom inset we are going to apply in Dp.
            val bottomInsetDp = with(density) { windowInsets.getBottom(density).toDp() }

            // Check if the sum of our bottom inset and padding is higher than the biggest corner.
            (bottomInsetDp + contentPadding.calculateBottomPadding()) >= maxDisplayBottomRadius
        }
    }

    // Calculate the shape of the Box.
    return remember(
        displayCorners,
        paddingPlusInsetClearsRadius,
        baseShape
    ) {
        // Check if the spacing we are going to apply to the Box is big enough to clear the corners.
        // - If it is big enough, we don't need to change its shape, since it will never get cut by
        //   any corner.
        // - If it is not big enough, our box might be cut by the corners. To avoid this, we will
        //   make our box the same shape as the device display.
        val displayBottomStart = displayCorners.bottomStart
        val displayBottomEnd = displayCorners.bottomEnd
        if (!paddingPlusInsetClearsRadius && (displayBottomStart.isSpecified || displayBottomEnd.isSpecified)) {
            // Make sure to not create a CornerSize or use maxOf with Dp.Unspecified
            val startRadius = if (displayBottomStart.isSpecified) displayBottomStart else null
            val endRadius = if (displayBottomEnd.isSpecified) displayBottomEnd else null
            // Merge our default shape with the display corners
            val shape = baseShape.copy(
                bottomStart = startRadius?.let { CornerSize(it) } ?: baseShape.bottomStart,
                bottomEnd = endRadius?.let { CornerSize(it) } ?: baseShape.bottomEnd,
            )
            BoxShapeAndPadding(
                shape = shape,
                innerBottomPadding = maxOf(startRadius ?: 0.dp, endRadius ?: 0.dp),
            )
        } else {
            null
        }
    }
}
