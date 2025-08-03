package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RegisterBankTest {

    @Test
    fun `test create and access register`() {
        val bank = RegisterBank()
        bank.createRegister('A', 1)
        assertDoesNotThrow { bank.setBit('A', 0, true) }
        assertTrue(bank.getBit('A', 0))
    }

    @Test
    fun `test duplicate register creation throws`() {
        val bank = RegisterBank()
        bank.createRegister('A', 1)
        assertThrows<IllegalArgumentException> {
            bank.createRegister('A', 1)
        }
    }

    @Test
    fun `test accessing non-existent register throws`() {
        val bank = RegisterBank()
        assertThrows<IllegalArgumentException> {
            bank.getBit('X', 0)
        }
    }

    @Test
    fun `test setting invalid bit index throws`() {
        val bank = RegisterBank()
        bank.createRegister('R', 1)
        assertThrows<IllegalArgumentException> {
            bank.setBit('R', 10, true)
        }
    }

    @Test
    fun `test read and write register values`() {
        val bank = RegisterBank()
        bank.createRegister('Z', 1)
        val bytes = byteArrayOf(0b00001111)
        bank.writeRegister('Z', bytes)
        val copy = bank.readRegister('Z')
        assertArrayEquals(bytes, copy)
        copy[0] = 0
        assertNotEquals(0, bank.readRegister('Z')[0], "Internal state should be encapsulated")
    }

    @Test
    fun `test writing incorrect size throws`() {
        val bank = RegisterBank()
        bank.createRegister('B', 2)
        assertThrows<IllegalArgumentException> {
            bank.writeRegister('B', byteArrayOf(0x01))
        }
    }
}