package org.example.app

class CPUFactory(
    private val bits: Int,
    private val clockSpeed: Int,
    private var instructionsHandler: InstructionsHandler? = null
) {

    private var userInputHandler: UserInputHandler? = null
    private var timerSpeed: Int = clockSpeed  // default value, can be changed
    private var memoryDriver: MemoryDriver? = null
    private var displayDriver: DisplayDriver? = null
    private val registerKeys: MutableMap<Char, Int> = mutableMapOf()

    fun setInstructionsHandler(handler: InstructionsHandler): CPUFactory {
        instructionsHandler = handler
        return this
    }

    fun setUserInputHandler(handler: UserInputHandler): CPUFactory {
        userInputHandler = handler
        return this
    }

    fun setTimerSpeed(speed: Int): CPUFactory {
        timerSpeed = speed
        return this
    }

    fun setMemoryDriver(driver: MemoryDriver): CPUFactory {
        memoryDriver = driver
        return this
    }

    fun setDisplayDriver(driver: DisplayDriver): CPUFactory {
        displayDriver = driver
        return this
    }

    fun addRegister(key: Char, size: Int): CPUFactory {
        registerKeys[key]=size
        return this
    }

    fun buildCPU(): CPU {
        // Validate mandatory fields
        val ih = instructionsHandler ?: throw IllegalStateException("InstructionsHandler not set")
        val uih = userInputHandler ?: throw IllegalStateException("UserInputHandler not set")
        val mem = memoryDriver ?: throw IllegalStateException("MemoryDriver not set")
        val disp = displayDriver ?: throw IllegalStateException("DisplayDriver not set")

        return CPU(
            clockSpeed = clockSpeed,
            bits = bits,
            instructionsHandler = ih,
            userInputHandler = uih,
            registerKeys = registerKeys.toMap(),
            timerSpeed = timerSpeed,
            memoryDriver = mem,
            displayDriver = disp
        )
    }
}