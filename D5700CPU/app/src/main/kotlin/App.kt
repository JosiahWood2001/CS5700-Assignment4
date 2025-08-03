package org.example.app
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val fileNameHandler = FileNameInput(TerminalInputHandler())
    val hexByteHandler = OneByteInput(TerminalInputHandler())
    val memoryDriver = MemoryDriver()
    val displayDriver = DisplayDriver()
    //create CPU
    val cpuFactory = CPUFactory(8,500,InstructionsSetD5700)
    for (registerNumber in 0..8){
        cpuFactory.addRegister(registerNumber.toChar(),1)
    }
    cpuFactory.addRegister('P',2)
        .addRegister('T',1)
        .addRegister('A',2)
        .addRegister('M',1)
        .setUserInputHandler(hexByteHandler)
        .setTimerSpeed(60)
        .setMemoryDriver(memoryDriver)
        .setDisplayDriver(displayDriver)

    //create Memory Storage and load program into memory
    memoryDriver.createMemoryDevice(4000, Writable) //RAM
    memoryDriver.createMemoryDevice(4000, Writable) //ROM
    LoadProgramFromD5700.loadProgram(memoryDriver,1,0,fileNameHandler.getInput(),false)
    memoryDriver.makeReadOnly(1)
    //create display for user
    displayDriver.addDisplay(8,8,4, SimpleTerminalDisplay)

    //create CPU and begin execution
    val cpu = cpuFactory.buildCPU()
    cpu.executeProgram()
}