package com.spundev.dynamicthemeexport.data

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorFormatTest {

    @Test
    fun formatter_SRGBIntegerTest() {
        assertEquals("Color(0xFFFF0000)", ColorFormat.SRGBInteger.formatter(Color.Red))
        assertEquals("Color(0xFF00FF00)", ColorFormat.SRGBInteger.formatter(Color.Green))
        assertEquals("Color(0xFF0000FF)", ColorFormat.SRGBInteger.formatter(Color.Blue))
        assertEquals("Color(0xFF123456)", ColorFormat.SRGBInteger.formatter(Color(0xFF123456)))
    }

    @Test
    fun formatter_FloatComponentsTest() {
        assertEquals(
            "Color(red = 1.0f, green = 0.0f, blue = 0.0f)",
            ColorFormat.FloatComponents.formatter(Color.Red)
        )
        assertEquals(
            "Color(red = 0.0f, green = 1.0f, blue = 0.0f)",
            ColorFormat.FloatComponents.formatter(Color.Green)
        )
        assertEquals(
            "Color(red = 0.0f, green = 0.0f, blue = 1.0f)",
            ColorFormat.FloatComponents.formatter(Color.Blue)
        )
        val customColor = Color(red = 0.12f, green = 0.34f, blue = 0.56f)
        assertEquals(
            "Color(red = ${customColor.red}f, green = ${customColor.green}f, blue = ${customColor.blue}f)",
            ColorFormat.FloatComponents.formatter(customColor)
        )
    }

    @Test
    fun formatter_IntegerComponentsHexTest() {
        assertEquals(
            "Color(red = 0xFF, green = 0x00, blue = 0x00)",
            ColorFormat.IntegerComponentsHex.formatter(Color.Red)
        )
        assertEquals(
            "Color(red = 0x00, green = 0xFF, blue = 0x00)",
            ColorFormat.IntegerComponentsHex.formatter(Color.Green)
        )
        assertEquals(
            "Color(red = 0x00, green = 0x00, blue = 0xFF)",
            ColorFormat.IntegerComponentsHex.formatter(Color.Blue)
        )
        assertEquals(
            "Color(red = 0x12, green = 0x34, blue = 0x56)",
            ColorFormat.IntegerComponentsHex.formatter(Color(red = 0x12, green = 0x34, blue = 0x56))
        )
    }

    @Test
    fun formatter_IntegerComponentsTest() {
        assertEquals(
            "Color(red = 255, green = 0, blue = 0)",
            ColorFormat.IntegerComponents.formatter(Color.Red)
        )
        assertEquals(
            "Color(red = 0, green = 255, blue = 0)",
            ColorFormat.IntegerComponents.formatter(Color.Green)
        )
        assertEquals(
            "Color(red = 0, green = 0, blue = 255)",
            ColorFormat.IntegerComponents.formatter(Color.Blue)
        )
        assertEquals(
            "Color(red = 12, green = 34, blue = 56)",
            ColorFormat.IntegerComponents.formatter(Color(red = 12, green = 34, blue = 56))
        )
    }
}
