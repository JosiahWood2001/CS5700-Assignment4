package org.example.app.tests

import org.example.app.*
import kotlin.test.*

class ProgramInstructionsTest {

    private lateinit var cpu: CPU
    private lateinit var memoryDriver: MemoryDriver
    private lateinit var displayDriver: DisplayDriver

    @BeforeTest
    fun setup() {
        val fileNameHandler = FileNameInput(TerminalInputHandler()) // or mock input if needed
        val hexByteHandler = OneByteInput(TerminalInputHandler())
        memoryDriver = MemoryDriver()
        displayDriver = DisplayDriver()

        val cpuFactory = CPUFactory(8, 500, InstructionsSetD5700)
        for (registerNumber in 0..8) {
            cpuFactory.addRegister(('0'.code + registerNumber).toChar(), 1)
        }
        cpuFactory.addRegister('P', 2)
            .addRegister('T', 1)
            .addRegister('A', 2)
            .addRegister('M', 1)
            .setUserInputHandler(hexByteHandler)
            .setTimerSpeed(60)
            .setMemoryDriver(memoryDriver)
            .setDisplayDriver(displayDriver)

        memoryDriver.createMemoryDevice(4000, Writable) // RAM
        memoryDriver.createMemoryDevice(4000, Writable) // ROM

        LoadProgramFromD5700.loadProgram(memoryDriver, 1, 0, "test.d5700", false)
        memoryDriver.makeReadOnly(1)

        displayDriver.addDisplay(8, 8, 4, SimpleTerminalDisplay)

        cpu = cpuFactory.buildCPU()
    }
    @Test
    fun testInstructionSequence() {
        assertEquals("00",cpu.withRegisterBank { it.readRegister('0').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("12",cpu.withRegisterBank { it.readRegister('0').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("02",cpu.withRegisterBank { it.readRegister('0').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("0F",cpu.withRegisterBank { it.readRegister('1').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("11",cpu.withRegisterBank { it.readRegister('2').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        assertEquals("0C",cpu.withRegisterBank { it.readRegister('1').joinToString(""){"%02X".format(it)} })
        assertEquals("0000", cpu.withRegisterBank { it.readRegister('A').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        assertEquals("0C", cpu.withRegisterBank { it.readRegister('0').joinToString(""){"%02X".format(it)} })
        assertEquals("0010", cpu.withRegisterBank { it.readRegister('P').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("0014", cpu.withRegisterBank { it.readRegister('P').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("12",cpu.withRegisterBank { it.readRegister('0').joinToString(""){"%02X".format(it)} })
        assertEquals(0,cpu.getCurrentMemoryTarget())
        cpu.executeNextInstruction()
        assertEquals(1,cpu.getCurrentMemoryTarget())
        cpu.executeNextInstruction()
        assertEquals(0,cpu.getCurrentMemoryTarget())
        cpu.executeNextInstruction()
        assertEquals("001C",cpu.withRegisterBank { it.readRegister('P').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("0020",cpu.withRegisterBank { it.readRegister('P').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("0022",cpu.withRegisterBank { it.readRegister('P').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        assertEquals("0026",cpu.withRegisterBank { it.readRegister('P').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        assertEquals("002C",cpu.withRegisterBank { it.readRegister('P').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("00",cpu.withRegisterBank { it.readRegister('2').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("0123",cpu.withRegisterBank { it.readRegister('A').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        cpu.stopTimer()
        assertEquals("32",cpu.withRegisterBank { it.readRegister('T').joinToString(""){"%02X".format(it)} })
        cpu.startTimer()
        Thread.sleep(1100L / cpu.timerSpeed)
        cpu.stopTimer()
        assertNotEquals("32",cpu.withRegisterBank { it.readRegister('T').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        assertEquals("02",cpu.withRegisterBank { it.readRegister('T').joinToString(""){"%02X".format(it)} })
        Thread.sleep(1100L / cpu.timerSpeed)
        cpu.executeNextInstruction()
        assertNotEquals("03",cpu.withRegisterBank { it.readRegister('4').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        assertEquals("2",cpu.memoryDriver.read(0,1).toInt().toString())
        assertEquals("5",cpu.memoryDriver.read(0,2).toInt().toString())
        assertEquals("5",cpu.memoryDriver.read(0,3).toInt().toString())
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
        assertEquals("33",cpu.withRegisterBank { it.readRegister('6').joinToString(""){"%02X".format(it)} })
        assertEquals("43",cpu.withRegisterBank { it.readRegister('4').joinToString(""){"%02X".format(it)} })
        cpu.executeNextInstruction()
        cpu.executeNextInstruction()
    }
}