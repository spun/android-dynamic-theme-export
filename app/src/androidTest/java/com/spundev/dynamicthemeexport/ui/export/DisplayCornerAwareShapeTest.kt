package com.spundev.dynamicthemeexport.ui.export

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spundev.dynamicthemeexport.util.DisplayCorners
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class DisplayCornerAwareShapeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenPaddingClearsRadius_returnsNull() {
        // Prepare parameters
        val displayCorners = DisplayCorners(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomEnd = 48.dp,
            bottomStart = 48.dp,
        )
        val windowInsets = WindowInsets()
        val contentPadding = PaddingValues(bottom = 56.dp)
        val baseShape = RoundedCornerShape(8.dp)

        var result: BoxShapeAndPadding? = null
        composeTestRule.setContent {
            result = rememberDisplayCornerAwareShape(
                displayCorners = displayCorners,
                contentPadding = contentPadding,
                windowInsets = windowInsets,
                baseShape = baseShape
            )
        }

        composeTestRule.waitForIdle()
        assertNull(result)
    }

    @Test
    fun whenInsetClearsRadius_returnsNull() {
        // Prepare parameters
        val displayCorners = DisplayCorners(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomEnd = 48.dp,
            bottomStart = 48.dp,
        )
        val windowInsets = WindowInsets(bottom = 56.dp)
        val contentPadding = PaddingValues.Zero
        val baseShape = RoundedCornerShape(8.dp)

        var result: BoxShapeAndPadding? = null
        composeTestRule.setContent {
            result = rememberDisplayCornerAwareShape(
                displayCorners = displayCorners,
                contentPadding = contentPadding,
                windowInsets = windowInsets,
                baseShape = baseShape
            )
        }

        composeTestRule.waitForIdle()
        assertNull(result)
    }

    @Test
    fun whenInsetPlusPaddingClearsRadius_returnsNull() {
        // Prepare parameters
        val displayCorners = DisplayCorners(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomEnd = 48.dp,
            bottomStart = 48.dp,
        )
        val windowInsets = WindowInsets(bottom = 24.dp)
        val contentPadding = PaddingValues(bottom = 24.dp)
        val baseShape = RoundedCornerShape(8.dp)

        var result: BoxShapeAndPadding? = null
        composeTestRule.setContent {
            result = rememberDisplayCornerAwareShape(
                displayCorners = displayCorners,
                contentPadding = contentPadding,
                windowInsets = windowInsets,
                baseShape = baseShape
            )
        }

        composeTestRule.waitForIdle()
        assertNull(result)
    }

    @Test
    fun whenPaddingDoesNotClearRadius_returnsShape() {
        // Prepare parameters
        val displayCorners = DisplayCorners(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomEnd = 48.dp,
            bottomStart = 48.dp,
        )
        val windowInsets = WindowInsets(bottom = 24.dp)
        val contentPadding = PaddingValues(all = 8.dp)
        val baseShape = RoundedCornerShape(8.dp)

        var result: BoxShapeAndPadding? = null
        composeTestRule.setContent {
            result = rememberDisplayCornerAwareShape(
                displayCorners = displayCorners,
                contentPadding = contentPadding,
                windowInsets = windowInsets,
                baseShape = baseShape
            )
        }

        composeTestRule.waitForIdle()
        assertNotNull(result)
        assertEquals(
            RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomEnd = 48.dp,
                bottomStart = 48.dp
            ),
            result?.shape
        )
    }

    @Test
    fun whenOnlyOneCornerSpecified_returnsShapeForCorner() {
        // Prepare parameters
        val displayCorners = DisplayCorners(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomEnd = Dp.Unspecified,
            bottomStart = 48.dp,
        )
        val windowInsets = WindowInsets(bottom = 24.dp)
        val contentPadding = PaddingValues(all = 8.dp)
        val baseShape = RoundedCornerShape(8.dp)

        var result: BoxShapeAndPadding? = null
        composeTestRule.setContent {
            result = rememberDisplayCornerAwareShape(
                displayCorners = displayCorners,
                contentPadding = contentPadding,
                windowInsets = windowInsets,
                baseShape = baseShape
            )
        }

        composeTestRule.waitForIdle()
        assertNotNull(result)
        assertEquals(
            RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                // This was Unspecified, get radius from base shape
                bottomEnd = 8.dp,
                bottomStart = 48.dp
            ),
            result?.shape
        )
    }

    @Test
    fun whenNoBottomCornersSpecified_returnsNull() {
        // Prepare parameters
        val displayCorners = DisplayCorners(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomEnd = Dp.Unspecified,
            bottomStart = Dp.Unspecified,
        )
        val windowInsets = WindowInsets(bottom = 24.dp)
        val contentPadding = PaddingValues(all = 8.dp)
        val baseShape = RoundedCornerShape(8.dp)

        var result: BoxShapeAndPadding? = null
        composeTestRule.setContent {
            result = rememberDisplayCornerAwareShape(
                displayCorners = displayCorners,
                contentPadding = contentPadding,
                windowInsets = windowInsets,
                baseShape = baseShape
            )
        }

        composeTestRule.waitForIdle()
        assertNull(result)
    }

    @Test
    fun whenNoCornersSpecified_returnsNull() {
        // Prepare parameters
        val displayCorners = DisplayCorners.Unspecified
        val windowInsets = WindowInsets(bottom = 24.dp)
        val contentPadding = PaddingValues(all = 8.dp)
        val baseShape = RoundedCornerShape(8.dp)

        var result: BoxShapeAndPadding? = null
        composeTestRule.setContent {
            result = rememberDisplayCornerAwareShape(
                displayCorners = displayCorners,
                contentPadding = contentPadding,
                windowInsets = windowInsets,
                baseShape = baseShape
            )
        }

        composeTestRule.waitForIdle()
        assertNull(result)
    }
}
