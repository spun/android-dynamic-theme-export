package com.spundev.dynamicthemeexport.panZoom

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.spundev.dynamicthemeexport.util.panZoom.PanZoomState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PanZoomStateTest {

    @Test
    fun applyZoom_increasesScaleAndUsesCentroid() {
        val state = getInitializedState()
        val centroid = Offset(500f, 500f)
        val contentUnderCentroidBefore = state.offset + centroid / state.scale
        // Change zoom
        state.applyZoom(zoomChange = 2f, centroid = centroid)
        // Assert both are the same
        val contentUnderCentroidAfter = state.offset + centroid / state.scale
        assertEquals(contentUnderCentroidBefore, contentUnderCentroidAfter)
    }

    @Test
    fun applyZoom_neverGoesBelowMinZoom() {
        val state = getInitializedState(
            viewport = IntSize(1000, 1000),
            content = IntSize(500, 500)
        )
        // minZoom should be 2f and, since it is bigger than the default 1f, it should also be the
        // current scale.
        assertEquals(2f, state.scale)
        // Try to zoom-out
        state.applyZoom(zoomChange = 0.01f, centroid = Offset.Zero)
        assertEquals(2f, state.scale)
    }

    @Test
    fun offset_isClampedAfterUpdateSizes() {
        val viewPortSizeSize = IntSize(2000, 2000)
        val initialContentSize = viewPortSizeSize * 3
        // Since content is (viewport * 3) we can set the initial offset to the equivalent of two
        // viewports  without any issues.
        val initialOffset = Offset(
            x = (viewPortSizeSize.width * 2).toFloat(),
            y = (viewPortSizeSize.height * 2).toFloat()
        )
        val state = getInitializedState(
            viewport = viewPortSizeSize,
            content = initialContentSize
        ).apply { offset = initialOffset }

        // no-op applyZoom call  to trigger clamp in case our initialOffset calculation was wrong,
        // and it wasn't a valid offset
        state.applyZoom(zoomChange = 1f, centroid = Offset.Zero)
        assertEquals(initialOffset, state.offset)

        // Shrink content from 3x to 2x from viewport and confirm offset gets pulled back in
        val updatedContentSize = viewPortSizeSize * 2
        state.updateSizes(
            viewport = viewPortSizeSize,
            content = updatedContentSize
        )
        assertEquals(
            Offset(
                x = viewPortSizeSize.width.toFloat(),
                y = viewPortSizeSize.height.toFloat()
            ),
            state.offset
        )
    }

    @Test
    fun scale_isClampedAfterUpdateSizes() {
        val viewPortSizeSize = IntSize(2000, 2000)
        val initialContentSize = viewPortSizeSize * 2
        val state = getInitializedState(
            viewport = viewPortSizeSize,
            content = initialContentSize
        )

        // Since initial content is twice our viewport, minZoom should be 0.5f and the default of 1f
        // is a valid scale.
        assertEquals(1f, state.scale)

        // Shrink content size to half the viewport.
        val updatedContentSize = viewPortSizeSize / 2
        state.updateSizes(
            viewport = viewPortSizeSize,
            content = updatedContentSize
        )
        // The new minZoom should now be 2f, the scale of 1f is now incorrect and should be clamped.
        assertEquals(2f, state.scale)
    }

    @Test
    fun scale_isKeptAfterUpdateSizesIfValid() {
        val viewPortSizeSize = IntSize(2000, 2000)
        // Smaller that viewport
        val initialContentSize = viewPortSizeSize / 2
        val state = getInitializedState(
            viewport = viewPortSizeSize,
            content = initialContentSize
        )
        // Since initial content is half the size of viewport, minZoom should be 2f.
        assertEquals(2f, state.scale)

        // Increase content size to fit the viewport.
        val updatedContentSize = viewPortSizeSize
        state.updateSizes(
            viewport = viewPortSizeSize,
            content = updatedContentSize
        )
        // Even if the new minZoom should now be 1f, the 2f scale is a valid value and is kept.
        assertEquals(2f, state.scale)
    }

    // ----- SAVER -----
    // We don't know if this is a good way to test a saver.
    // We also have an instrumentation test that checks the correct behavior or the real
    // rememeberSaveable + Saver inside our rememberPanZoomState.

    @Test
    fun save_capturesScaleAndOffset() {
        val flingBehavior = TestFlingBehavior()
        val saver = PanZoomState.Saver(flingBehavior)
        val state = PanZoomState(defaultFlingBehavior = flingBehavior).apply {
            updateSizes(
                viewport = IntSize(300, 300),
                content = IntSize(600, 600)
            )
        }

        // See the Saver interface to understand why we need to do this to save
        val saved = with(testSaverScope) { with(saver) { save(state) } }

        assertNotNull(saved)
        val savedList = saved as List<*>
        assertEquals(state.scale, savedList[0])
        assertEquals(state.offset.x, savedList[1])
        assertEquals(state.offset.y, savedList[2])
    }

    @Test
    fun restore_recreatesScaleAndOffset() {
        val flingBehavior = TestFlingBehavior()
        val saver = PanZoomState.Saver(flingBehavior)

        val savedValue = listOf(2.5f, 42f, 84f)
        val restored = saver.restore(savedValue)

        assertNotNull(restored)
        assertEquals(2.5f, restored!!.scale)
        assertEquals(Offset(42f, 84f), restored.offset)
    }

    @Test
    fun saveThenRestore_givesSameValues() {
        val flingBehavior = TestFlingBehavior()
        val saver = PanZoomState.Saver(flingBehavior)
        val original = PanZoomState(flingBehavior).apply {
            updateSizes(
                viewport = IntSize(300, 300),
                content = IntSize(900, 900)
            )
        }
        original.applyZoom(zoomChange = 3f, centroid = Offset(150f, 150f))

        val saved = with(testSaverScope) { with(saver) { save(original) } }
        val restored = saver.restore(saved!!)

        assertNotNull(restored)
        assertEquals(original.scale, restored!!.scale)
        assertEquals(original.offset, restored.offset)
    }
}

// Create a PanZoomState with default sizes and flingBehavior
private fun getInitializedState(
    viewport: IntSize = IntSize(1000, 1000),
    content: IntSize = IntSize(2000, 2000)
) = PanZoomState(
    defaultFlingBehavior = TestFlingBehavior()
).apply { updateSizes(viewport, content) }

// No-op FlingBehavior for unit tests.
private class TestFlingBehavior : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        // Consume all velocity, do nothing. Tests using this don't exercise fling.
        return 0f
    }
}

// Minimal SaverScope for tests where canBeSaved is always true.
private val testSaverScope = SaverScope { true }
