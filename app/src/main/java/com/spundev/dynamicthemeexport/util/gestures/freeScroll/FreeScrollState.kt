package com.spundev.dynamicthemeexport.util.gestures.freeScroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import com.spundev.dynamicthemeexport.util.gestures.BasePanState

/**
 * Create and remember a [FreeScrollState] instance.
 */
@Composable
fun rememberFreeScrollState(): FreeScrollState {
    return rememberSaveable(saver = FreeScrollState.Saver) { FreeScrollState() }
}

@Stable
class FreeScrollState : BasePanState() {
    companion object {
        val Saver = listSaver(
            save = { listOf(it.offset.x, it.offset.y) },
            restore = { (x, y) ->
                // offset is restored without clamping against the possibly new content bounds.
                // This is safe because PanLayoutNode.measure() will call state.updateSizes() on
                // the next measure pass and makes sure the value is within the limits.
                FreeScrollState().apply { offset = Offset(x, y) }
            }
        )
    }
}
