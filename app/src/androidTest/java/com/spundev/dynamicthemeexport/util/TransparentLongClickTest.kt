package com.spundev.dynamicthemeexport.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.util.panZoom.PanZoomState
import com.spundev.dynamicthemeexport.util.panZoom.panZoom
import com.spundev.dynamicthemeexport.util.panZoom.rememberPanZoomState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TransparentLongClickTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun longPress_firesCallback() {
        var longPressCount = 0
        composeRule.setContent {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .transparentLongClick { longPressCount++ }
                    .testTag("block")
            )
        }

        composeRule.onNodeWithTag("block").performTouchInput { longClick() }
        composeRule.runOnIdle { assertEquals(1, longPressCount) }
    }

    @Test
    fun simpleClick_doesNotFireLongPress() {
        var longPressCount = 0
        composeRule.setContent {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .transparentLongClick { longPressCount++ }
                    .testTag("block")
            )
        }

        composeRule.onNodeWithTag("block").performTouchInput { click() }
        composeRule.runOnIdle { assertEquals(0, longPressCount) }
    }

    /**
     * NOTE: The original purpose for transparentLongClick was to leave the pointer events
     * unconsumed so our panZoom modifier could receive them and use them for the gestures.
     * With a simple combinedClickable, gestures like double-tap-to-zoom stopped working. So we are
     * going to test that the gesture still works when content has a Modifier.transparentLongClick.
     */
    @Test
    fun doubleTap_stillTriggersPanZoomGesture() {
        lateinit var state: PanZoomState
        composeRule.setContent {
            state = rememberPanZoomState()
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .panZoom(state)
            ) {
                Box(
                    modifier = Modifier
                        .size(600.dp)
                        .transparentLongClick { /* no-op */ }
                        .testTag("content")
                )
            }
        }

        composeRule.onNodeWithTag("content").performTouchInput { doubleClick() }
        composeRule.runOnIdle { assertTrue(state.scale > 1f) }
    }

    /**
     * NOTE: Since our content will have multiple blocks with Modifier.transparentLongClick in a
     * place where pich-to-zoom are expected. We want to know what happens if the user holds two
     * fingers against two different blocks with Modifier.transparentLongClick.
     * We want that only one trigger is received, but we don't really know what to expect.
     */
    @Test
    fun twoLongPressesAtTheSameTime_onlyFiresOnce() {
        var block1Count = 0
        var block2Count = 0
        composeRule.setContent {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .testTag("parent")
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .transparentLongClick { block1Count++ }
                        .testTag("block1")
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .transparentLongClick { block2Count++ }
                        .testTag("block2")
                )
            }
        }

        // Get block centers from root
        val block1Center =
            composeRule.onNodeWithTag("block1").fetchSemanticsNode().boundsInRoot.center
        val block2Center =
            composeRule.onNodeWithTag("block2").fetchSemanticsNode().boundsInRoot.center

        // Use two pointers manually
        composeRule.onRoot().performTouchInput {
            // Press at the exact time
            down(pointerId = 1, position = block1Center)
            down(pointerId = 2, position = block2Center)
            // Advance time past the longPress threshold
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100L)
            // Lift at the same time
            up(pointerId = 1)
            up(pointerId = 2)
        }
        composeRule.runOnIdle { assertTrue((block1Count + block2Count) == 1) }
    }
}
