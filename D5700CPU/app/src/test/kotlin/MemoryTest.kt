package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class MemoryTest {

    @Test
    fun `read returns correct value after write`() {
        val writeBehavior = mock<WriteBehavior> {
            on { write() } doReturn { array, value, address ->
                array[address] = value
            }
        }

        val memory = Memory(10, writeBehavior)
        memory.write(0x2A, 5)
        assertEquals(0x2A.toByte(), memory.read(5))
    }

    @Test
    fun `write throws if address is above memory size`() {
        val writeBehavior = mock<WriteBehavior>()
        val memory = Memory(5, writeBehavior)

        val exception = assertThrows<IndexOutOfBoundsException> {
            memory.write(0x10, 5) // 5 is invalid: max valid is 4
        }

        assertTrue(exception.message!!.contains("out of bounds"))
    }

    @Test
    fun `write throws if address is below 0`() {
        val writeBehavior = mock<WriteBehavior>()
        val memory = Memory(5, writeBehavior)

        val exception = assertThrows<IndexOutOfBoundsException> {
            memory.write(0x10, -1)
        }

        assertTrue(exception.message!!.contains("out of bounds"))
    }

    @Test
    fun `read throws if address is below 0`() {
        val writeBehavior = mock<WriteBehavior>()
        val memory = Memory(5, writeBehavior)

        val exception = assertThrows<IndexOutOfBoundsException> {
            memory.read(-1)
        }

        assertTrue(exception.message!!.contains("out of bounds"))
    }

    @Test
    fun `write delegates to WriteBehavior`() {
        val mockWrite: (ByteArray, Byte, Int) -> Unit = mock()
        val writeBehavior = mock<WriteBehavior> {
            on { write() } doReturn mockWrite
        }

        val memory = Memory(8, writeBehavior)
        memory.write(0x33, 4)

        verify(mockWrite).invoke(any(), eq(0x33.toByte()), eq(4))
    }
    @Test
    fun `write on ReadOnlyThrowError throws exception`() {
        val memory = Memory(8, ReadOnlyThrowError)

        val exception = assertThrows<UnsupportedOperationException> {
            memory.write(0x42, 0)
        }

        assertEquals("This memory is read-only.", exception.message)
    }
}