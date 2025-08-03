package org.example.app

import kotlin.UnsupportedOperationException

object ReadOnlyThrowError : WriteBehavior {
    override fun write(): (ByteArray, Byte, Int) -> Unit {
        return {
            _, _, _ -> throw UnsupportedOperationException("This memory is read-only.") as Throwable
        }
    }
}