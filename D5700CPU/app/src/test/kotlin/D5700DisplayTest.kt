package org.example.app

import kotlin.test.*

class D5700DisplayTest {
    private class TestDisplayBehavior : DisplayBehavior {
        var wasCalled = false
        lateinit var lastFrameBuffer: Memory
        var lastWidth = -1
        var lastHeight = -1
        var lastBytesPerPixel = -1

        override fun display(width: Int, height: Int, bytesPerPixel: Int, frameBuffer: Memory) {
            wasCalled = true
            lastWidth = width
            lastHeight = height
            lastBytesPerPixel = bytesPerPixel
            lastFrameBuffer = frameBuffer
        }
    }

    @Test
    fun testDrawWritesToCorrectMemoryLocation() {
        val display = D5700Display(4, 4, 8, TestDisplayBehavior())
        val testValue = byteArrayOf('A'.code.toByte())
        display.draw(1, 1, testValue)
        val address = (1 + 1 * display.width) * 1 // bytesPerPixel = 1 for 8 bpp
        val memory = display.javaClass.getDeclaredField("frameBuffer")
        memory.isAccessible = true
        val mem = memory.get(display) as Memory
        assertEquals('A'.code.toByte(), mem.read(address))
    }

    @Test
    fun testClearBufferFillsMemoryWithDefaultByte() {
        val display = D5700Display(2, 2, 8, TestDisplayBehavior())
        val clearByte = 0x00.toByte()

        display.draw(0, 0, byteArrayOf('X'.code.toByte()))
        display.clearBuffer()

        val memoryField = display.javaClass.getDeclaredField("frameBuffer")
        memoryField.isAccessible = true
        val mem = memoryField.get(display) as Memory

        for (i in 0 until mem.size) {
            assertEquals(clearByte, mem.read(i), "Byte at address $i is not clear byte")
        }
    }

    @Test
    fun testDisplayCallsBehaviorWithCorrectParams() {
        val behavior = TestDisplayBehavior()
        val display = D5700Display(5, 3, 16, behavior)

        display.display()

        assertTrue(behavior.wasCalled, "Display behavior was not called")
        assertEquals(5, behavior.lastWidth)
        assertEquals(3, behavior.lastHeight)
        assertEquals(2, behavior.lastBytesPerPixel) // 16 bits per pixel = 2 bytes
    }
}