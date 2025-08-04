package org.example.app

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals

class CPUTest {
    private lateinit var memoryDriver: MemoryDriver
    private lateinit var instructionsHandler: InstructionsHandler
    private lateinit var cpu: CPU
    private lateinit var registers: RegisterBank

    private val bytesPerInstruction = 2

    @BeforeEach
    fun setUp() {
        // Mock memoryDriver
        memoryDriver = mock()

        // Mock instructionsHandler
        instructionsHandler = mock {
            on { bytesPerInstruction } doReturn bytesPerInstruction
        }

        // We'll create CPU with mocked dependencies
        val registerKeys = mapOf('P' to 2, 'T' to 1, 'A' to 1, 'M' to 1)

        cpu = CPU(
            clockSpeed = 1,
            bits = 16,
            instructionsHandler = instructionsHandler,
            userInputHandler = mock(),  // Just mock, not used here
            registerKeys = registerKeys,
            timerSpeed = 1,
            memoryDriver = memoryDriver,
            displayDriver = mock()
        )
    }
}