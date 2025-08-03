package org.example.app

class Memory(
    val size: Int,
    writeBehavior: WriteBehavior
) {
    var writeBehavior: WriteBehavior = writeBehavior
        private set
    private val storage: ByteArray = ByteArray(size)
    fun write(value: Byte, address: Int) {
        if (address >= size || address < 0) throw IndexOutOfBoundsException("address is out of bounds")
        writeBehavior.write()(storage, value, address)
    }

    fun read(address: Int): Byte {
        if (address < 0 || address >= size) throw IndexOutOfBoundsException("address is out of bounds")
        return storage[address]
    }
    fun makeWritable(){
        writeBehavior=Writable
    }
    fun makeReadOnly(){
        writeBehavior= ReadOnlyThrowError
    }
}