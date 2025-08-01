package org.example.app

interface WriteBehavior {
    fun write(): (ByteArray, Byte, Int) -> Unit
}