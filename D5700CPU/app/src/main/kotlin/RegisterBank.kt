package org.example.app

class RegisterBank {
    private val registers = mutableMapOf<Char, Register>()
    private fun validateRegister(key: Char){
        if (key !in registers) {
            throw IllegalArgumentException("Key '$key' does not exist")
        }
    }
    private fun checkIndexRange(key: Char, index: Int){
        validateRegister(key)
        if(index<0|| index>=registers[key]!!.size){
            throw IllegalArgumentException("'$key' does not have index $index")
        }
    }
    fun createRegister(key: Char, size: Int) {
        if (registers.containsKey(key)) {
            throw IllegalArgumentException("Register '$key' already exists")
        }
        registers[key] = Register(size)
    }
    fun getBit(key: Char, index: Int): Boolean {
        checkIndexRange(key, index)
        return registers[key]!!.getBit(index)
    }
    fun setBit(key: Char, index: Int, value: Boolean) {
        checkIndexRange(key, index)
        registers[key]!!.setBit(value, index)
    }

    fun readRegister(key: Char): ByteArray {
        validateRegister(key)
        return registers[key]!!.getRegister()
    }

    fun writeRegister(key: Char, value: ByteArray) {
        validateRegister(key)
        if (value.size!=registers[key]!!.size){
            throw IllegalArgumentException("value has incorrect size")
        }
        registers[key]!!.setRegister(value)
    }
    fun checkRegister(key: Char): Boolean {
        if (key !in registers){
            return false
        }
        return true
    }
}