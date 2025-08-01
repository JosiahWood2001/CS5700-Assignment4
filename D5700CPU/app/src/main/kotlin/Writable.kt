package org.example.app

object Writable : WriteBehavior {
    override fun write(): (ByteArray, Byte, Int) -> Unit {
        return {
            storage, value, address -> storage[address]=value
        }
    }
}