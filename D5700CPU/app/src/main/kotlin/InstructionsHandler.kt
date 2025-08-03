package org.example.app

interface InstructionsHandler {
    fun executeInstruction(instruction: ByteArray): (CPU)->Unit
    val bytesPerInstruction: Int
}