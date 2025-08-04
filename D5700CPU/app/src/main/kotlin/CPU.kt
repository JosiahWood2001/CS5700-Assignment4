package org.example.app

import jdk.internal.org.jline.keymap.KeyMap.key
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class CPU(
    val clockSpeed: Int,
    val bits: Int,
    val instructionsHandler: InstructionsHandler,
    val userInputHandler: UserInputHandler,
    registerKeys: Map<Char, Int>,
    val timerSpeed: Int,
    val memoryDriver: MemoryDriver,
    val displayDriver: DisplayDriver
    ) {
    private val registers: RegisterBank = RegisterBank()
    val executor = Executors.newSingleThreadScheduledExecutor()
    val currentDisplayTarget = 0
    val bytesPerInstruction: Int = instructionsHandler.bytesPerInstruction
    private var timerFuture: ScheduledFuture<*>? = null
    private var programFuture: ScheduledFuture<*>? = null

    private val timerRunnable = Runnable {
        val currentValue = registers.readRegister('T')
        var value = currentValue.last().toInt() and 0xFF

        if (value > 0) {
            value -= 1
            currentValue[currentValue.lastIndex] = value.toByte()
            registers.writeRegister('T', currentValue)
        }
        if (value == 0) {
            stopTimer()
        }
    }
    private val programRunnable = Runnable {
        executeNextInstruction()
    }
    init {
        for ((key,size) in registerKeys){
            registers.createRegister(key, size)
        }
        val requiredRegisters = listOf('P', 'T', 'A', 'M')
        val missing = requiredRegisters.filterNot { registers.checkRegister(it) }

        if (missing.isNotEmpty()) {
            throw IllegalArgumentException("Missing required registers: ${missing.joinToString(", ")}")
        }
    }
    fun executeProgram(){
        programFuture = executor.scheduleAtFixedRate(
            programRunnable,
            0L,
            (1000L / clockSpeed),
            TimeUnit.MILLISECONDS
        )
    }
    fun startTimer(){
        stopTimer()
        timerFuture = executor.scheduleAtFixedRate(
            timerRunnable,
            0L,
            (1000L / timerSpeed),
            TimeUnit.MILLISECONDS
        )
    }
    fun stopTimer(){
        timerFuture?.cancel(false)
        timerFuture = null
    }
    fun executeNextInstruction(){
        val programCounterBytes = registers.readRegister('P')
        val programCounter = programCounterBytes.fold(0) {acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF)}
        val instruction = ByteArray(bytesPerInstruction) {offset ->
            memoryDriver.read(1, programCounter + offset)
        }
        if (instruction.joinToString(""){"%02X".format(it)}=="0000"){
            terminateProgram()
            return
        }
        val operation = instructionsHandler.generateInstruction(instruction)
        operation(this)
    }
    fun terminateProgram(){
        programFuture?.cancel(false)
        programFuture = null
        executor.shutdownNow() // Optional: Clean up executor
        exitProcess(0)
    }
    fun <T> withRegisterBank(action: (RegisterBank) -> T): T {
        return action(registers)
    }
    fun pauseExecution(){
        programFuture?.cancel(false)
        programFuture = null
    }
    fun resumeExecution(){
        pauseExecution()
        programFuture = executor.scheduleAtFixedRate(
            programRunnable,
            0L,
            (1000L / clockSpeed),
            TimeUnit.MILLISECONDS
        )
    }
    fun getCurrentMemoryTarget(): Int {
        return (registers.readRegister('M')[0].toInt() and 0x0F)
    }
    fun setCurrentMemoryTarget(memoryTarget: Int) {
        val current = registers.readRegister('M')
        val newValue = ByteArray(current.size)
        newValue[0] = memoryTarget.toByte()
        registers.writeRegister('M', newValue)
    }
}