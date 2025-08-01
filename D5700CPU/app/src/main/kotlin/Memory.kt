package org.example.app

class Memory(size: Int, writeBehavior: WriteBehavior) {
    val size = size
        private set
    private val storage: ByteArray = ByteArray(size)
    val writeBehavior: WriteBehavior = writeBehavior
        private set
    fun write(value: Byte, address: Int){
        if(address>size || address<0) throw IndexOutOfBoundsException("address is out of bounds")
        writeBehavior.write()(storage, value, address)
    }
    fun read(address: Int): Byte{
        if(address<0) throw IndexOutOfBoundsException("address is out of bounds")
        return storage[address]
    }
}