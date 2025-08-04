package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class CPUFactoryTest {

    private lateinit var factory: CPUFactory
    private val bits = 16
    private val clockSpeed = 1000

    private val mockInstructionsHandler = mock<InstructionsHandler> {}
    private val mockUserInputHandler = mock<UserInputHandler> {}
    private val mockMemoryDriver = mock<MemoryDriver> {}
    private val mockDisplayDriver = mock<DisplayDriver> {}

    @BeforeEach
    fun setup() {
        factory = CPUFactory(bits, clockSpeed)
    }

    @Test
    fun `builder methods return factory for chaining`() {
        val result = factory
            .setInstructionsHandler(mockInstructionsHandler)
            .setUserInputHandler(mockUserInputHandler)
            .setTimerSpeed(500)
            .setMemoryDriver(mockMemoryDriver)
            .setDisplayDriver(mockDisplayDriver)
            .addRegister('P', 2)

        assertSame(factory, result)
    }

    @Test
    fun `buildCPU throws if InstructionsHandler is not set`() {
        factory
            .setUserInputHandler(mockUserInputHandler)
            .setMemoryDriver(mockMemoryDriver)
            .setDisplayDriver(mockDisplayDriver)
            .addRegister('P', 2)

        val ex = assertThrows(IllegalStateException::class.java) {
            factory.buildCPU()
        }
        assertEquals("InstructionsHandler not set", ex.message)
    }

    @Test
    fun `buildCPU throws if UserInputHandler is not set`() {
        factory
            .setInstructionsHandler(mockInstructionsHandler)
            .setMemoryDriver(mockMemoryDriver)
            .setDisplayDriver(mockDisplayDriver)
            .addRegister('P', 2)

        val ex = assertThrows(IllegalStateException::class.java) {
            factory.buildCPU()
        }
        assertEquals("UserInputHandler not set", ex.message)
    }

    @Test
    fun `buildCPU throws if MemoryDriver is not set`() {
        factory
            .setInstructionsHandler(mockInstructionsHandler)
            .setUserInputHandler(mockUserInputHandler)
            .setDisplayDriver(mockDisplayDriver)
            .addRegister('P', 2)

        val ex = assertThrows(IllegalStateException::class.java) {
            factory.buildCPU()
        }
        assertEquals("MemoryDriver not set", ex.message)
    }

    @Test
    fun `buildCPU throws if DisplayDriver is not set`() {
        factory
            .setInstructionsHandler(mockInstructionsHandler)
            .setUserInputHandler(mockUserInputHandler)
            .setMemoryDriver(mockMemoryDriver)
            .addRegister('P', 2)

        val ex = assertThrows(IllegalStateException::class.java) {
            factory.buildCPU()
        }
        assertEquals("DisplayDriver not set", ex.message)
    }

    @Test
    fun `buildCPU returns CPU with correct parameters`() {
        factory
            .setInstructionsHandler(mockInstructionsHandler)
            .setUserInputHandler(mockUserInputHandler)
            .setMemoryDriver(mockMemoryDriver)
            .setDisplayDriver(mockDisplayDriver)
            .addRegister('P', 2)
            .addRegister('T', 1)
            .addRegister('A',2)
            .addRegister('M', 2)

        val cpu = factory.buildCPU()

        assertEquals(clockSpeed, cpu.clockSpeed)
        assertEquals(bits, cpu.bits)
        assertSame(mockInstructionsHandler, cpu.instructionsHandler)
        assertSame(mockUserInputHandler, cpu.userInputHandler)
        assertSame(mockMemoryDriver, cpu.memoryDriver)
        assertSame(mockDisplayDriver, cpu.displayDriver)

        // Registers map should contain 'P' and 'T' keys with correct sizes
        assertEquals(2, cpu.withRegisterBank{ it.readRegister('P').size})
        assertEquals(1, cpu.withRegisterBank{it.readRegister('T').size})
    }
}