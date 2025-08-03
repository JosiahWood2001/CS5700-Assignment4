package org.example.app

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import java.io.File
import kotlin.io.path.createTempDirectory

class LoadProgramFromD5700Test {

    private lateinit var memoryDriver: MemoryDriver
    private val memoryIndex = 0
    private val startingIndex = 0

    @BeforeEach
    fun setup() {
        memoryDriver = mock()
    }


    @Test
    fun `compiles d5700 file and caches output`() {
        val fileName = "testprog.d5700"

        // Ensure resource exists
        val classLoader = LoadProgramFromD5700::class.java.classLoader
        val resource = classLoader.getResource("roms/$fileName")
        assertNotNull(resource)

        // Clear temp file before running test
        val tempOutFile = File("${System.getProperty("java.io.tmpdir")}/testprog.out")
        if (tempOutFile.exists()) tempOutFile.delete()

        // Act: force compilation
        LoadProgramFromD5700.loadProgram(memoryDriver, memoryIndex, startingIndex, fileName, forceCompile = true)

        // Assert: output file cached
        assertTrue(tempOutFile.exists())
        val cachedBytes = tempOutFile.readBytes()

        // Confirm writes to memoryDriver for cached bytes
        for (i in cachedBytes.indices) {
            verify(memoryDriver).write(memoryIndex, cachedBytes[i], startingIndex + i)
        }
    }

    @Test
    fun `throws error if resource not found`() {
        val fileName = "nonexistent.d5700"
        val exception = assertThrows<IllegalArgumentException> {
            LoadProgramFromD5700.loadProgram(memoryDriver, memoryIndex, startingIndex, fileName, forceCompile = false)
        }
        assertTrue(exception.message!!.contains("not found"))
    }

    @Test
    fun `throws error on unsupported file extension`() {
        val fileName = "file.unsupported"
        val exception = assertThrows<IllegalArgumentException> {
            LoadProgramFromD5700.loadProgram(memoryDriver, memoryIndex, startingIndex, fileName, forceCompile = false)
        }
        assertTrue(exception.message!!.contains("Unsupported file extension"))
    }

    @Test
    fun `throws error on invalid instruction length during compilation`() {
        val fileName = "invalidlength.d5700"

        val classLoader = LoadProgramFromD5700::class.java.classLoader
        assertNotNull(classLoader.getResource("roms/$fileName"))

        val exception = assertThrows<IllegalArgumentException> {
            LoadProgramFromD5700.loadProgram(memoryDriver, memoryIndex, startingIndex, fileName, forceCompile = true)
        }
        assertTrue(exception.message!!.contains("Invalid instruction length"))
    }
}