package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class SimpleTerminalDisplayTest {

    @Test
    fun `display prints correct grid to terminal`() {
        // Arrange
        val width = 3
        val height = 2
        val bytesPerPixel = 1
        val memory = Memory(width * height * bytesPerPixel, Writable)
        val display = SimpleTerminalDisplay()

        // Fill the framebuffer with ASCII characters: A-F
        val chars = "ABCDEF"
        chars.forEachIndexed { index, c ->
            memory.write(c.code.toByte(), index)
        }

        // Capture System.out
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        // Act
        display.display(width, height, bytesPerPixel, memory)

        // Reset System.out
        System.setOut(originalOut)

        // Assert output (preserve newlines carefully)
        val expected ="ABC\r\nDEF\r\n\r\n"

        val actual = outputStream.toString()
        assertEquals(expected, actual)

        // Also print the output to the terminal for demonstration
        println("Captured Display Output:\n$actual")
    }
}