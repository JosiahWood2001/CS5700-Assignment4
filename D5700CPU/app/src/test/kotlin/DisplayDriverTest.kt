package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class DisplayDriverTest {
    private lateinit var driver: DisplayDriver
    private lateinit var mockDisplayBehavior: DisplayBehavior

    @BeforeEach
    fun setUp() {
        driver = DisplayDriver()
        mockDisplayBehavior = mock()
        driver.addDisplay(2, 2, 8, mockDisplayBehavior)
    }

    @Test
    fun `numberOfDevices should return correct count`() {
        assertEquals(1, driver.numberOfDevices())
        driver.addDisplay(4, 4, 8, mockDisplayBehavior)
        assertEquals(2, driver.numberOfDevices())
    }

    @Test
    fun `draw should call draw on internal display`() {
        val data = byteArrayOf(0x0A)
        driver.draw(0, 1, 1, data)
        // Verify through behavior used inside the display (frameBuffer written)
        verify(mockDisplayBehavior).display(any(), any(), any(), any())
    }

    @Test
    fun `getWidth should return correct width`() {
        assertEquals(2, driver.getWidth(0))
    }

    @Test
    fun `getHeight should return correct height`() {
        assertEquals(2, driver.getHeight(0))
    }

    @Test
    fun `getBitsPerPixel should return correct value`() {
        assertEquals(8, driver.getBitsPerPixel(0))
    }

    @Test
    fun `clearDisplayBuffer should call clearBuffer on the display`() {
        // There's no direct way to verify without exposing more,
        // so just call to ensure it doesn't throw
        assertDoesNotThrow {
            driver.clearDisplayBuffer(0)
        }
    }

    @Test
    fun `display should call displayBehavior display method`() {
        driver.display(0)
        verify(mockDisplayBehavior, atLeastOnce()).display(any(), any(), any(), any())
    }

    @Test
    fun `displayAll should call display on all devices`() {
        driver.addDisplay(2, 2, 8, mockDisplayBehavior)
        driver.displayAll()
        verify(mockDisplayBehavior, times(2)).display(any(), any(), any(), any())
    }

    @Test
    fun `clearAllBuffers should not throw`() {
        assertDoesNotThrow {
            driver.clearAllBuffers()
        }
    }

    @Test
    fun `accessing invalid index should throw IndexOutOfBoundsException`() {
        assertThrows<IndexOutOfBoundsException> {
            driver.getWidth(99)
        }
        assertThrows<IndexOutOfBoundsException> {
            driver.getHeight(-1)
        }
        assertThrows<IndexOutOfBoundsException> {
            driver.draw(5, 0, 0, byteArrayOf(1))
        }
        assertThrows<IndexOutOfBoundsException> {
            driver.display(999)
        }
        assertThrows<IndexOutOfBoundsException> {
            driver.clearDisplayBuffer(2)
        }
    }
}