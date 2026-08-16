package com.spundev.dynamicthemeexport.util.panZoom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PanZoomModifierTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun doubleTap_togglesZoom() {
        lateinit var state: PanZoomState
        composeRule.setContent {
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
        // Zoom-in from 1f.
        composeRule.onNodeWithTag("viewport").performTouchInput { doubleClick() }
        composeRule.runOnIdle { assertTrue(state.scale > 1f) }
        // Back to 1f from the zoomed-in position.
        composeRule.onNodeWithTag("viewport").performTouchInput { doubleClick() }
        composeRule.runOnIdle { assertEquals(1f, state.scale, 0.01f) }
    }

    @Test
    fun doubleTap_onEmptyAreaOfViewport_togglesZoom() {
        lateinit var state: PanZoomState
        composeRule.setContent {
            state = rememberPanZoomState()
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .panZoom(state)
                    .testTag("viewport")
            ) {
                // Content is smaller than the viewport, and it is placed in a corner.
                // Our doubleClick will be done in the bottom-right of the viewport, where no
                // content is displayed.
                // NOTE: Make sure a side is the same or a higher value than the one given to the
                //  viewport to avoid the situation where minZoom is > 1f and panZoom increases the
                //  default scale to remove the unnecessary empty space.
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 300.dp)
                        .align(Alignment.TopStart)
                        .testTag("content")
                )
            }
        }

        composeRule.onNodeWithTag("viewport").performTouchInput {
            // Tap near the bottom-right corner of the 300.dp viewport
            doubleClick(position = Offset(250.dp.toPx(), 250.dp.toPx()))
        }
        composeRule.runOnIdle { assertTrue(state.scale > 1f) }
    }

    @Test
    fun pinchZoom_increasesScale() {
        lateinit var state: PanZoomState
        composeRule.setContent {
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

        composeRule.onNodeWithTag("viewport").performTouchInput {
            pinch(
                start0 = center - Offset(50f, 0f),
                end0 = center - Offset(150f, 0f),
                start1 = center + Offset(50f, 0f),
                end1 = center + Offset(150f, 0f)
            )
        }
        composeRule.runOnIdle { assertTrue(state.scale > 1f) }
    }

    @Test
    fun pinchZoom_onEmptyAreaOfViewport_increasesScale() {
        lateinit var state: PanZoomState
        composeRule.setContent {
            state = rememberPanZoomState()
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .panZoom(state)
                    .testTag("viewport")
            ) {
                // Content is smaller than the viewport, and it is placed in a corner.
                // Our pinch will be done in the bottom-right of the viewport, where no content is
                // displayed.
                // NOTE: Make sure a side is the same or a higher value than the one given to the
                //  viewport to avoid the situation where minZoom is > 1f and panZoom increases the
                //  default scale to remove the unnecessary empty space.
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 300.dp)
                        .align(Alignment.TopStart)
                        .testTag("content")
                )
            }
        }

        composeRule.onNodeWithTag("viewport").performTouchInput {
            // Pinch right area of the 300.dp viewport
            val pinchCenter = Offset(250.dp.toPx(), 150.dp.toPx())
            val offset = 50.dp.toPx()
            pinch(
                start0 = pinchCenter - Offset(0f, offset),
                end0 = pinchCenter - Offset(0f, offset * 2),
                start1 = pinchCenter + Offset(0f, offset),
                end1 = pinchCenter + Offset(0f, offset * 2)
            )
        }
        composeRule.runOnIdle { assertTrue(state.scale > 1f) }
    }

    @Test
    fun drag_updatesOffsets() {
        lateinit var state: PanZoomState
        composeRule.setContent {
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

        composeRule.onNodeWithTag("viewport").performTouchInput {
            // Drag from center toward the top-left so offset changes increase.
            swipe(
                start = center,
                end = center - Offset(100.dp.toPx(), 100.dp.toPx()),
                durationMillis = 200
            )
        }
        composeRule.runOnIdle {
            assertTrue(state.offset.x > 0f)
            assertTrue(state.offset.y > 0f)
        }
    }

    @Test
    fun drag_pastContentBounds_offsetStaysClamped() {
        lateinit var state: PanZoomState
        composeRule.setContent {
            state = rememberPanZoomState()
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .testTag("panZoomContainer")
                    .panZoom(state)
            ) {
                // slightly bigger than viewport
                Box(modifier = Modifier.size(400.dp))
            }
        }

        composeRule.onNodeWithTag("panZoomContainer").performTouchInput {
            // Long swipe to exceed available space.
            swipe(
                start = center,
                end = center - Offset(1000.dp.toPx(), 1000.dp.toPx()),
                durationMillis = 200
            )
        }

        composeRule.runOnIdle {
            // At scale 1f max offset per axis should be 100.dp
            val maxOffsetPx = with(composeRule.density) { 100.dp.toPx() }
            assertTrue(state.offset.x == maxOffsetPx)
            assertTrue(state.offset.y == maxOffsetPx)
        }
    }

    @Test
    fun fastSwipe_continuesAfterRelease() {
        lateinit var state: PanZoomState
        composeRule.setContent {
            state = rememberPanZoomState()
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .panZoom(state)
                    .testTag("viewport")
            ) {
                // way bigger than viewport
                Box(modifier = Modifier.size(3000.dp))
            }
        }

        composeRule.onNodeWithTag("viewport").performTouchInput {
            swipe(
                start = center,
                end = center - Offset(150.dp.toPx(), 0f),
                durationMillis = 50
            )
        }

        // Read immediately so we can get the offset after the gesture.
        val offsetRightAfterRelease = state.offset
        // Let the fling animation run to completion.
        composeRule.runOnIdle {
            val offsetAfterFling = state.offset
            assertTrue(offsetAfterFling.x > offsetRightAfterRelease.x)
        }
    }

    @Test
    fun pinchGesture_doesNotTriggerFlingAnimation() {
        lateinit var state: PanZoomState
        composeRule.setContent {
            state = rememberPanZoomState()
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .panZoom(state)
                    .testTag("viewport")
            ) {
                Box(modifier = Modifier.size(1000.dp))
            }
        }

        composeRule.onNodeWithTag("viewport").performTouchInput {
            // Move both fingers in the same direction (panning with two fingers).
            // NOTE: If both fingers stay the same distance between each other the whole time, our
            //  detectZoomGesture will see no zoon change and will never call the onZoom callback.
            //  To test the fling animation after a zoom gesture, we also need to do a small pinch
            //  (enough to past our touchSlop check) while moving both fingers is the same direction.
            pinch(
                // Top finger moves 150.dp left, and increases center distance from 50.dp up to 100.dp.
                start0 = center + Offset(0f, -50.dp.toPx()),
                end0 = center + Offset(-150.dp.toPx(), -100.dp.toPx()),
                // Bottom finger moves 150.dp left, and increase center distance from 50.dp down to 100.dp.
                start1 = center + Offset(0f, 50.dp.toPx()),
                end1 = center + Offset(-150.dp.toPx(), 100.dp.toPx()),
                durationMillis = 100
            )
        }
        // Read immediately so we can get the offset after the gesture.
        val offsetRightAfterRelease = state.offset
        // Let any unwanted fling animation run to completion.
        composeRule.runOnIdle {
            assertEquals(offsetRightAfterRelease, state.offset)
        }
    }
}
