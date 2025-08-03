package org.example.app

import kotlin.math.ceil

class D5700Display(
    override val width: Int,
    override val height: Int,
    override val bitsPerPixel: Int,
    val displayBehavior: DisplayBehavior
) : Display {
    private val bytesPerPixel: Int = ceil(bitsPerPixel.toFloat() / 8).toInt()
    private val frameBuffer: Memory = Memory(width * height * bytesPerPixel, Writable)
    private val clearDefault: Byte = 0x00
    override fun draw(x: Int, y: Int, value: ByteArray) {
        for (i in 0 until value.size) {
            frameBuffer.write(value[i], (x + y * width) * bytesPerPixel + i)
        }
        display()
    }

    override fun display() {
        displayBehavior.display(width, height, bytesPerPixel, frameBuffer)
    }

    override fun clearBuffer() {
        for (i in 0 until frameBuffer.size) {
            frameBuffer.write(clearDefault, i)
        }
    }

}