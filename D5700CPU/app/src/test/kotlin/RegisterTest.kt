package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RegisterTest {

    @Test
    fun `test set and get bit`() {
        val reg = Register(1) // 8 bits
        reg.setBit(true, 0)
        assertTrue(reg.getBit(0))
        reg.setBit(false, 0)
        assertFalse(reg.getBit(0))

        reg.setBit(true, 7)
        assertTrue(reg.getBit(7))
        reg.setBit(false, 7)
        assertFalse(reg.getBit(7))
    }

    @Test
    fun `test out-of-bounds bit index throws`() {
        val reg = Register(1) // 8 bits
        assertThrows<IllegalArgumentException> {
            reg.setBit(true, 8)
        }
        assertThrows<IllegalArgumentException> {
            reg.getBit(-1)
        }
    }

    @Test
    fun `test getRegister returns copy`() {
        val reg = Register(1)
        reg.setBit(true, 0)
        val copy = reg.getRegister()
        copy[0] = 0
        assertTrue(reg.getBit(0), "Original register should not be modified by external mutation")
    }

    @Test
    fun `test setRegister copies value`() {
        val reg = Register(1)
        val input = byteArrayOf(0b00000001)
        reg.setRegister(input)
        input[0] = 0
        assertTrue(reg.getBit(0), "Register should have copied the input")
    }

    @Test
    fun `test setRegister with wrong size throws`() {
        val reg = Register(2)
        assertThrows<IllegalArgumentException> {
            reg.setRegister(byteArrayOf(0x01))
        }
    }
}