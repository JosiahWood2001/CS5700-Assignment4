package org.example.app

class Register(val size: Int) {
    private var value: ByteArray = ByteArray(size)

    fun getBit(bitIndex: Int): Boolean {
        require(bitIndex in 0 until (size * 8)) { "Bit index out of range" }
        val byteIndex = bitIndex / 8
        val bitOffset = bitIndex % 8
        return (value[byteIndex].toInt() shr bitOffset and 1) == 1
    }

    fun setBit(bitValue: Boolean, bitIndex: Int) {
        require(bitIndex in 0 until (size * 8)) { "Bit index out of range" }
        val byteIndex = bitIndex / 8
        val bitOffset = bitIndex % 8
        val mask = (1 shl bitOffset).toByte()

        value[byteIndex] = if (bitValue) {
            (value[byteIndex].toInt() or mask.toInt()).toByte()
        } else {
            (value[byteIndex].toInt() and mask.toInt().inv()).toByte()
        }
    }

    fun getRegister(): ByteArray {
        return value.copyOf() // Return a safe copy
    }

    fun setRegister(register: ByteArray) {
        require(register.size == size) { "Register size mismatch" }
        value = register.copyOf() // Store a copy to preserve encapsulation
    }
}