package org.example.app

import jdk.internal.org.jline.keymap.KeyMap.key
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

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
    val registers: RegisterBank = RegisterBank()
    val executor = Executors.newSingleThreadScheduledExecutor()
    val currentDisplayTarget = 0
    val currentMemoryTarget = 0
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
            memoryDriver.read(currentMemoryTarget, programCounter + offset)
        }
        val newProgramCounter = programCounter + bytesPerInstruction
        val newPCBytes = ByteArray(programCounterBytes.size) {i->
            ((newProgramCounter shr (8 * (programCounterBytes.size - i - 1))) and 0xFF).toByte()
        }
        registers.writeRegister('P', newPCBytes)
        instructionsHandler.executeInstruction(instruction)
    }
    fun terminateProgram(){
        programFuture?.cancel(false)
        programFuture = null
    }
}