package org.example.app

typealias Instruction = (CPU)->Unit

abstract class BaseInstruction(protected val instruction: ByteArray) {
    fun toLambda(): Instruction =  {cpu: CPU ->
        decode()
        operate(cpu)
        postProcess(cpu)
    }
    protected open fun decode() {}
    protected abstract fun operate(cpu: CPU)
    protected open fun postProcess(cpu: CPU) {
        val programCounterBytes: ByteArray = cpu.withRegisterBank {
            it.readRegister('P')
        }
        val programCounter = programCounterBytes.fold(0) {acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF)}
        val newProgramCounter = programCounter + cpu.bytesPerInstruction
        val newPCBytes = ByteArray(programCounterBytes.size) {i->
            ((newProgramCounter shr (8 * (programCounterBytes.size - i - 1))) and 0xFF).toByte()
        }
        cpu.withRegisterBank{
            it.writeRegister('P', newPCBytes)
        }
    }
}

class STORE(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX = 0
    private lateinit var bb: ByteArray

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F
        bb = byteArrayOf(instruction[1])
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank{
            it.writeRegister(('0'.code + rX).toChar(), bb)
        }
    }
}
class ADD(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX = 0
    private var rY = 0
    private var rZ = 0

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F              // low nibble of byte 0
        rY = (instruction[1].toInt() shr 4) and 0x0F       // high nibble of byte 1
        rZ = instruction[1].toInt() and 0x0F              // low nibble of byte 1
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val x = it.readRegister(('0'.code + rX).toChar())[0].toInt() and 0xFF
            val y = it.readRegister(('0'.code + rY).toChar())[0].toInt() and 0xFF
            val result = (x + y) and 0xFF
            it.writeRegister(('0'.code + rZ).toChar(), byteArrayOf(result.toByte()))
        }
    }
}
class SUB(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX = 0; private var rY = 0; private var rZ = 0

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F            // Low nibble of byte 0
        rY = (instruction[1].toInt() shr 4) and 0x0F     // High nibble of byte 1
        rZ = instruction[1].toInt() and 0x0F             // Low nibble of byte 1
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val x = it.readRegister(('0'.code + rX).toChar())[0].toInt() and 0xFF
            val y = it.readRegister(('0'.code + rY).toChar())[0].toInt() and 0xFF
            val result = (x-y) and 0xFF
            it.writeRegister(('0'.code + rZ).toChar(), byteArrayOf(result.toByte()))
        }
    }
}
class READ(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX = 0

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F  // Low nibble of first byte
    }

    override fun operate(cpu: CPU) {
        val addressBytes = cpu.withRegisterBank { it.readRegister('A') }
        val address = addressBytes.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }

        val value = cpu.memoryDriver.read(cpu.getCurrentMemoryTarget(), address)

        cpu.withRegisterBank {
            it.writeRegister(('0'.code + rX).toChar(), byteArrayOf(value))
        }
    }
}
class WRITE(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX = 0

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F  // Low nibble of first byte
    }

    override fun operate(cpu: CPU) {
        // Get the value from register rX
        val value = cpu.withRegisterBank {
            it.readRegister(('0'.code + rX).toChar())
        }

        // Get the address from register 'A'
        val addressBytes = cpu.withRegisterBank {
            it.readRegister('A')
        }
        val address = addressBytes.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }

        cpu.memoryDriver.write(cpu.getCurrentMemoryTarget(), value[0], address)
    }
}
class JUMP(instruction: ByteArray) : BaseInstruction(instruction) {
    private var addr = 0

    override fun decode() {
        // aaa is 12 bits: low nibble of first byte + full second byte
        addr = ((instruction[0].toInt() and 0x0F) shl 8) or (instruction[1].toInt() and 0xFF)
    }

    override fun operate(cpu: CPU) {
        if (addr % cpu.bytesPerInstruction != 0) {
            error("Jump address must be divisible by 2")
        }
        // Convert addr to bytes (big endian) to write to program counter 'P'
        val bytes = ByteArray(cpu.withRegisterBank{
            it.registerSize('P')
        }) { i -> ((addr shr (8 * (1 - i))) and 0xFF).toByte() }
        cpu.withRegisterBank {
            it.writeRegister('P', bytes)
        }
    }

    override fun postProcess(cpu: CPU) {
        // Skip incrementing the program counter after this instruction
    }
}
class READ_KEYBOARD(instruction: ByteArray) : BaseInstruction(instruction) {
    private var targetRegister = 0

    override fun decode() {
        targetRegister = instruction[0].toInt() and 0x0F
    }

    override fun operate(cpu: CPU) {
        // Wait for a single byte of hex input from user
        cpu.pauseExecution()
        val inputByte: Byte = cpu.userInputHandler.getInput().toInt(16).toByte()

        cpu.withRegisterBank {
            it.writeRegister(('0'.code + targetRegister).toChar(), byteArrayOf(inputByte))
        }
        cpu.resumeExecution()
    }
}
class SWITCH_MEMORY(instruction: ByteArray) : BaseInstruction(instruction) {
    override fun decode() {
        // No decoding needed for this instruction
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val current = it.readRegister('M')[0].toInt()
            val newBank = (current xor 0x01) and 0xFF
            it.writeRegister('M', byteArrayOf(newBank.toByte()))
        }
    }
}
class SKIP_EQUAL(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX: Int = 0
    private var rY: Int = 0

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F
        rY = (instruction[1].toInt() shr 4) and 0x0F
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val valX = it.readRegister(('0'.code + rX).toChar())
            val valY = it.readRegister(('0'.code + rY).toChar())

            if (valX.contentEquals(valY)) {
                val pcBytes = it.readRegister('P')
                var pc = pcBytes.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }
                pc += cpu.bytesPerInstruction

                val newPCBytes = ByteArray(pcBytes.size) { i ->
                    ((pc shr (8 * (pcBytes.size - i - 1))) and 0xFF).toByte()
                }

                it.writeRegister('P', newPCBytes)
            }
        }
    }
}
class SKIP_NOT_EQUAL(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX: Int = 0
    private var rY: Int = 0

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F
        rY = (instruction[1].toInt() shr 4) and 0x0F
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank { regs ->
            val valX = regs.readRegister(('0'.code + rX).toChar())
            val valY = regs.readRegister(('0'.code + rY).toChar())

            if (!valX.contentEquals(valY)) {
                // Skip next instruction by incrementing program counter
                val pcBytes = regs.readRegister('P')
                var pc = pcBytes.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }
                pc += cpu.bytesPerInstruction

                val newPCBytes = ByteArray(pcBytes.size) { i ->
                    ((pc shr (8 * (pcBytes.size - i - 1))) and 0xFF).toByte()
                }

                regs.writeRegister('P', newPCBytes)
            }
        }
    }
}
class SET_A(instruction: ByteArray) : BaseInstruction(instruction) {
    private var address: Int = 0

    override fun decode() {
        // Decode 12-bit address from bytes 1 and 2
        val high = instruction[0].toInt() and 0x0F
        val low = instruction[1].toInt() and 0xFF
        address = (high shl 8) or low
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val aSize = it.readRegister('A').size
            val addressBytes = ByteArray(aSize) { i ->
                ((address shr (8 * (aSize - i - 1))) and 0xFF).toByte()
            }
            it.writeRegister('A', addressBytes)
        }
    }
}
class SET_T(instruction: ByteArray) : BaseInstruction(instruction) {
    private var value: Byte = 0

    override fun decode() {
        // Extract byte value from the second byte
        value = (((instruction[0].toInt() and 0x0F) shl 4) or ((instruction[1].toInt() and 0xF0) ushr 4)).toByte()
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank { regs ->
            regs.writeRegister('T', byteArrayOf(value))
        }
        cpu.startTimer()
    }
}
class READ_T(instruction: ByteArray) : BaseInstruction(instruction) {
    private var destRegister: Char = '0'

    override fun decode() {
        val rX = (instruction[0].toInt() and 0x0F)
        destRegister = rX.digitToChar(16).uppercaseChar()
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank { regs ->
            val tValue = regs.readRegister('T')
            regs.writeRegister(destRegister, tValue)
        }
    }
}
class CONVERT_TO_BASE_10(instruction: ByteArray) : BaseInstruction(instruction) {
    private var srcRegister: Char = '0'

    override fun decode() {
        val rX = (instruction[0].toInt() and 0x0F)
        srcRegister = ('0'.code + rX).toChar()
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val value = it.readRegister(srcRegister)[0].toInt() and 0xFF  // Ensure unsigned byte

            val hundreds = (value / 100) % 10
            val tens = (value / 10) % 10
            val ones = value % 10
            val addrBytes = it.readRegister('A')
            val addr = addrBytes.fold(0) {acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF)}
            cpu.memoryDriver.write(cpu.getCurrentMemoryTarget(),hundreds.toByte(),addr)
            cpu.memoryDriver.write(cpu.getCurrentMemoryTarget(),tens.toByte(),addr+1)
            cpu.memoryDriver.write(cpu.getCurrentMemoryTarget(),ones.toByte(),addr+2)
        }
    }
}
class CONVERT_BYTE_TO_ASCII(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX: Int = 0
    private var rY: Int = 0

    override fun decode() {
        rX = (instruction[0].toInt() and 0x0F)
        rY = (instruction[1].toInt() shr 4) and 0x0F
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val value = it.readRegister(('0'.code + rX).toChar())[0].toInt() and 0xFF

            if (value > 0x0F)
                throw IllegalStateException("Invalid value $value in r$rX: must be between 0x00 and 0x0F")
            val ascii = if (value <= 9)
                ('0'.code + value).toByte()
            else
                ('A'.code + (value - 10)).toByte()

            it.writeRegister(('0'.code + rY).toChar(), byteArrayOf(ascii))
        }
    }
}
class DRAW(instruction: ByteArray) : BaseInstruction(instruction) {
    private var rX: Int = 0
    private var rY: Int = 0
    private var rZ: Int = 0

    override fun decode() {
        rX = instruction[0].toInt() and 0x0F
        rY = (instruction[1].toInt() shr 4) and 0x0F
        rZ = instruction[1].toInt() and 0x0F
    }

    override fun operate(cpu: CPU) {
        cpu.withRegisterBank {
            val asciiByte = it.readRegister(('0'.code + rX).toChar())[0]

            if (asciiByte.toInt() and 0xFF > 0x7F) {
                throw IllegalStateException("Invalid ASCII value in r$rX: ${asciiByte.toInt()} (must be ≤ 127)")
            }
            cpu.displayDriver.draw(cpu.currentDisplayTarget, rZ, rY, byteArrayOf(asciiByte))
        }
    }
}
object InstructionsSetD5700 : InstructionsHandler {
    override val bytesPerInstruction = 2

    private val instructionsMap: Map<Byte, (ByteArray) -> BaseInstruction> = mapOf(
        0x0.toByte() to ::STORE,
        0x1.toByte() to ::ADD,
        0x2.toByte() to ::SUB,
        0x3.toByte() to ::READ,
        0x4.toByte() to ::WRITE,
        0x5.toByte() to ::JUMP,
        0x6.toByte() to ::READ_KEYBOARD,
        0x7.toByte() to ::SWITCH_MEMORY,
        0x8.toByte() to ::SKIP_EQUAL,
        0x9.toByte() to ::SKIP_NOT_EQUAL,
        0xA.toByte() to ::SET_A,
        0xB.toByte() to ::SET_T,
        0xC.toByte() to ::READ_T,
        0xD.toByte() to ::CONVERT_TO_BASE_10,
        0xE.toByte() to ::CONVERT_BYTE_TO_ASCII,
        0xF.toByte() to ::DRAW
    )

    override fun generateInstruction(instruction: ByteArray): (CPU)->Unit {
        val instructionNibble: Byte = ((instruction[0].toInt() shr 4) and 0x0F).toByte()
        val instructionFunction = instructionsMap[instructionNibble]
            ?: throw IllegalArgumentException("Instruction not handled: $instructionNibble")
        return instructionFunction(instruction).toLambda()
    }
}