package org.example.app

typealias Instruction = (CPU) -> Unit

object InstructionsSetD5700 : InstructionsHandler {
    override val bytesPerInstruction = 2

    private val instructionsMap: Map<Byte, (ByteArray) -> Instruction> = mapOf(
        0x0.toByte() to ::STORE,
        0x1.toByte() to ::ADD,
        0x2.toByte() to ::SUB,
        0x3.toByte() to ::READ,
        0x4.toByte() to ::WRITE,
        0x5.toByte() to ::JUMP,
        0x6.toByte() to ::READ_KEYBOARD,
        0x7.toByte() to ::SWITCH_MEMORY,
        0x8.toByte() to ::SKIP_EQUAL,
        0x9.toByte() to ::SKIP_NOT_EQUAL,
        0xA.toByte() to ::SET_A,
        0xB.toByte() to ::SET_T,
        0xC.toByte() to ::READ_T,
        0xD.toByte() to ::CONVERT_TO_BASE_10,
        0xE.toByte() to ::CONVERT_BYTE_TO_ASCII,
        0xF.toByte() to ::DRAW,
    )

    override fun executeInstruction(instruction: ByteArray): Instruction {
        val instructionNibble: Byte = (instruction[0].toInt() shr 4).toByte()
        val instructionFunction = instructionsMap[instructionNibble]
            ?: throw IllegalArgumentException("Instruction not handled: $instructionNibble")
        return instructionFunction(instruction)
    }

    private fun STORE(instruction: ByteArray): Instruction = { cpu -> }
    private fun ADD(instruction: ByteArray): Instruction = { cpu -> }
    private fun SUB(instruction: ByteArray): Instruction = { cpu -> }
    private fun READ(instruction: ByteArray): Instruction = { cpu -> }
    private fun WRITE(instruction: ByteArray): Instruction = { cpu -> }
    private fun JUMP(instruction: ByteArray): Instruction = { cpu -> }
    private fun READ_KEYBOARD(instruction: ByteArray): Instruction = { cpu -> }
    private fun SWITCH_MEMORY(instruction: ByteArray): Instruction = { cpu -> }
    private fun SKIP_EQUAL(instruction: ByteArray): Instruction = { cpu -> }
    private fun SKIP_NOT_EQUAL(instruction: ByteArray): Instruction = { cpu -> }
    private fun SET_A(instruction: ByteArray): Instruction = { cpu -> }
    private fun SET_T(instruction: ByteArray): Instruction = { cpu -> }
    private fun READ_T(instruction: ByteArray): Instruction = { cpu -> }
    private fun CONVERT_TO_BASE_10(instruction: ByteArray): Instruction = { cpu -> }
    private fun CONVERT_BYTE_TO_ASCII(instruction: ByteArray): Instruction = { cpu -> }
    private fun DRAW(instruction: ByteArray): Instruction = { cpu -> }
}