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

        registers = cpu.registers
    }

    @Test
    fun `executeNextInstruction reads instruction and increments PC`() {
        // Arrange
        // Set initial PC to 0x0004 (2 bytes)
        registers.writeRegister('P', byteArrayOf(0x00, 0x04))

        // Stub memory reads at addresses 4 and 5
        whenever(memoryDriver.read(any(), eq(4))).thenReturn(0x10.toByte())
        whenever(memoryDriver.read(any(), eq(5))).thenReturn(0x20.toByte())

        // Act
        cpu.executeNextInstruction()

        // Assert
        // Verify instructionsHandler.executeInstruction called with correct bytes
        val expectedInstruction = byteArrayOf(0x10, 0x20)
        verify(instructionsHandler).executeInstruction(argThat { contentEquals(expectedInstruction) })

        // Verify PC incremented by bytesPerInstruction (2), so new PC = 6
        val pcBytes = registers.readRegister('P')
        val pcValue = pcBytes.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }
        assertEquals(6, pcValue)
    }
}