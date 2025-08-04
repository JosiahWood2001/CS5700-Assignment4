package org.example.app

interface InstructionsHandler {
    fun generateInstruction(instruction: ByteArray): (CPU)->Unit
    val bytesPerInstruction: Int
}