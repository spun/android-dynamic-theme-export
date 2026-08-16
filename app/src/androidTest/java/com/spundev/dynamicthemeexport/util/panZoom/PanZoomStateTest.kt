package com.spundev.dynamicthemeexport.util.panZoom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.StateRestorationTester
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.util.gestures.panZoom.PanZoomState
import com.spundev.dynamicthemeexport.util.gestures.panZoom.panZoom
import com.spundev.dynamicthemeexport.util.gestures.panZoom.rememberPanZoomState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test [PanZoomState] saver.
 * For more tests besides the saver, see [PanZoomStateTest] in the test folder (unit tests).
 */
class PanZoomStateTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun panZoomState_survivesStateRestoration() = runComposeUiTest {
        val restorationTester = StateRestorationTester(this)
        lateinit var state: PanZoomState
        restorationTester.setContent {
            state = rememberPanZoomState()
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .panZoom(state)
                    .testTag("viewport")
            ) {
                Box(modifier = Modifier.size(600.dp))
            }
        }

        // Check initial values
        runOnIdle {
            // Content is bigger than viewport, the default 1f scale should be the one in use.
            assertEquals(1f, state.scale)
            assertEquals(Offset.Zero, state.offset)
        }

        // Change scale and offset to new values
        runOnIdle { state.applyZoom(zoomChange = 2f, centroid = Offset(50f, 50f)) }
        onNodeWithTag("viewport").performTouchInput {
            // Change offset to a non-default value with a swipe.
            // Drag from center toward the top-left so offset changes increase.
            swipe(
                start = center,
                end = center - Offset(100.dp.toPx(), 100.dp.toPx()),
            )
        }

        // Check that our new values are not the default ones from before.
        runOnIdle {
            assertTrue(state.scale > 1f)
            assertTrue(state.offset.x > 0f)
            assertTrue(state.offset.y > 0f)
        }

        // Store current values
        val stateBeforeRestoration = state
        val scaleBeforeRestoration = state.scale
        val offsetBeforeRestoration = state.offset

        // Emulate a save and restore cycle of the current composition.
        restorationTester.emulateSaveAndRestore()

        // Check if the values were restored correctly.
        runOnIdle {
            assertNotSame(stateBeforeRestoration, state)
            assertEquals(scaleBeforeRestoration, state.scale)
            assertEquals(offsetBeforeRestoration, state.offset)
        }
    }
}
