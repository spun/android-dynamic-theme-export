package com.spundev.dynamicthemeexport.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.RoundedCornerCompat
import androidx.core.view.RoundedCornerCompat.POSITION_BOTTOM_LEFT
import androidx.core.view.RoundedCornerCompat.POSITION_BOTTOM_RIGHT
import androidx.core.view.RoundedCornerCompat.POSITION_TOP_LEFT
import androidx.core.view.RoundedCornerCompat.POSITION_TOP_RIGHT
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Create a DisplayCorners state.
 *
 * NOTE: Custom implementation until b/401464090 is fixed.
 */
@Composable
fun rememberDisplayCorners(): DisplayCorners {
    val view = LocalView.current
    val density: Density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // NOTE: We need WindowInsetsCompat to query the display corner radius. The "RootWindowInsets"
    //  we get from ViewCompat is a nullable value that can also change over time.
    //  WindowInsetsCompat has a setOnApplyWindowInsetsListener that would be perfect for this, but
    //  apparently it would replace any listener already set on this view. This might only be a
    //  problem if more than one rememberDisplayCorners is used at the same time, but we don't
    //  really know if it could cause unexpected issues outside this function.
    //  Since the worst that can happen is that we get Unspecified values in our DisplayCorners, we'd
    //  rather risk getting wrong corner values than cause issues elsewhere.

    // Trigger recomposition on physical screen changes (rotation, foldables, etc.)
    // These are the events we can think of where corner values could change.
    val configuration = LocalConfiguration.current

    // Use WindowInsets from Compose as a way to make sure the root insets have been dispatched by
    // the time we read them. This top inset gives us a value that changes once real insets arrive,
    // so we use it as an "insets are ready" flag.
    val topInset = WindowInsets.systemBars.getTop(density)

    return remember(view, density, layoutDirection, configuration, topInset) {
        val insets = ViewCompat.getRootWindowInsets(view)
        insets?.getDisplayCorners(density, layoutDirection) ?: DisplayCorners.Unspecified
    }
}

/**
 * State holder for the display corner radius state.
 */
data class DisplayCorners(
    val topStart: Dp,
    val topEnd: Dp,
    val bottomEnd: Dp,
    val bottomStart: Dp,
) {
    companion object {
        val Unspecified = DisplayCorners(
            topStart = Dp.Unspecified,
            topEnd = Dp.Unspecified,
            bottomEnd = Dp.Unspecified,
            bottomStart = Dp.Unspecified
        )

        val Zero = DisplayCorners(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        )
    }
}

/**
 * Get the corner radius [Dp] for a [position] using [WindowInsetsCompat]
 */
private fun WindowInsetsCompat.getCornerRadiusDp(
    @RoundedCornerCompat.Position position: Int,
    density: Density
): Dp? {
    val radiusPx = getRoundedCorner(position)?.radius
    return with(density) { radiusPx?.toDp() }
}

/**
 * Get a [DisplayCorners] by querying all [RoundedCornerCompat.Position].
 *
 * NOTE: Left and right positions are converted to start and end using the current [LayoutDirection].
 */
private fun WindowInsetsCompat.getDisplayCorners(
    density: Density,
    layoutDirection: LayoutDirection
): DisplayCorners {
    val topLeft = getCornerRadiusDp(POSITION_TOP_LEFT, density) ?: Dp.Unspecified
    val topRight = getCornerRadiusDp(POSITION_TOP_RIGHT, density) ?: Dp.Unspecified
    val bottomRight = getCornerRadiusDp(POSITION_BOTTOM_RIGHT, density) ?: Dp.Unspecified
    val bottomLeft = getCornerRadiusDp(POSITION_BOTTOM_LEFT, density) ?: Dp.Unspecified

    return if (layoutDirection == LayoutDirection.Ltr) {
        DisplayCorners(
            topStart = topLeft,
            topEnd = topRight,
            bottomEnd = bottomRight,
            bottomStart = bottomLeft,
        )
    } else {
        DisplayCorners(
            topStart = topRight,
            topEnd = topLeft,
            bottomEnd = bottomLeft,
            bottomStart = bottomRight,
        )
    }
}
