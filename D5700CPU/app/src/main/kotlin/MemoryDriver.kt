package org.example.app

class MemoryDriver {
    private val memoryList = mutableListOf<Memory>()
    private fun checkBounds(displayIndex: Int) {}
    fun getMemorySize(memoryIndex: Int): Int{
        checkBounds(memoryIndex)
        return memoryList[memoryIndex].size
    }
    fun write(memoryIndex: Int, value: Byte, address: Int){
        checkBounds(memoryIndex)
        memoryList[memoryIndex].write(value, address)
    }
    fun read(memoryIndex: Int, address: Int): Byte{
        checkBounds(memoryIndex)
        return memoryList[memoryIndex].read(address)
    }
    fun createMemoryDevice(size: Int, writeBehavior: WriteBehavior): Int{
        memoryList.add(Memory(size, writeBehavior))
        return memoryList.size-1
    }
    fun numberOfDevices(): Int{
        return memoryList.size
    }

}