package org.example.app

import java.io.IOException

object ReadOnlyThrowError : WriteBehavior {
    override fun write(): (ByteArray, Byte, Int) -> Unit {
        return {
            _, _, _ -> throw IOException("Cannot write to memory, Read Only Memory") as Throwable
        }
    }
}