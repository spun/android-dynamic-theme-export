package com.spundev.dynamicthemeexport.util.gestures.freeScroll

import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.spundev.dynamicthemeexport.util.gestures.PanLayoutElement

/**
 * Utility modifier to simplify the use of [scrollable2D].
 */
fun Modifier.freeScroll(
    state: FreeScrollState,
): Modifier = this
    .clipToBounds()
    .scrollable2D(state = state.scrollable2DState)
    .then(PanLayoutElement(state))
