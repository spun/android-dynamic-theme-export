package com.spundev.dynamicthemeexport.util.freeScroll

import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.spundev.dynamicthemeexport.util.PanLayoutElement

/**
 * Utility modifier that combines all necessary steps to use a [Modifier.scrollable2D].
 */
fun Modifier.freeScroll(
    state: FreeScrollState,
): Modifier = this
    .clipToBounds()
    .scrollable2D(state = state.scrollable2DState)
    .then(PanLayoutElement(state))
