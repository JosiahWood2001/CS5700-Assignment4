package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MemoryDriverTest {

    class DummyWriteBehavior : WriteBehavior {
        override fun write(): (ByteArray, Byte, Int) -> Unit {
            return { memory, value, address ->
                memory[address] = value
            }
        }
    }

    @Test
    fun `createMemoryDevice returns correct index`() {
        val driver = MemoryDriver()
        val index = driver.createMemoryDevice(10, DummyWriteBehavior())
        assertEquals(0, index)
    }

    @Test
    fun `getMemorySize returns correct size`() {
        val driver = MemoryDriver()
        val index = driver.createMemoryDevice(16, DummyWriteBehavior())
        assertEquals(16, driver.getMemorySize(index))
    }

    @Test
    fun `getMemorySize returns 0 for out of bounds index`() {
        val driver = MemoryDriver()
        assertEquals(0, driver.getMemorySize(99))
    }

    @Test
    fun `write and read store and retrieve correct byte`() {
        val driver = MemoryDriver()
        val index = driver.createMemoryDevice(8, DummyWriteBehavior())

        driver.write(index, 0x3C.toByte(), 4)
        val value = driver.read(index, 4)
        assertEquals(0x3C.toByte(), value)
    }

    @Test
    fun `write throws for invalid memory index`() {
        val driver = MemoryDriver()

        val ex = assertThrows<IndexOutOfBoundsException> {
            driver.write(0, 0x3C.toByte(), 0)
        }
        assertEquals("No memory device at index 0", ex.message)
    }

    @Test
    fun `read throws for invalid memory index`() {
        val driver = MemoryDriver()

        val ex = assertThrows<IndexOutOfBoundsException> {
            driver.read(0, 0)
        }
        assertEquals("No memory device at index 0", ex.message)
    }
    @Test
    fun `numberOfDevices returns correct count after creating devices`() {
        val driver = MemoryDriver()

        // Initially, no devices
        assertEquals(0, driver.numberOfDevices())

        // Add a couple of memory devices
        driver.createMemoryDevice(10, ReadOnlyThrowError)
        driver.createMemoryDevice(20, ReadOnlyThrowError)

        assertEquals(2, driver.numberOfDevices())
    }
}