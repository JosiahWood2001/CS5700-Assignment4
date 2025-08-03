package org.example.app

import org.example.utils.Printer
import java.awt.print.Printable

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val fileNameHandler = FileNameInput(TerminalInputHandler())
    val hexByteHandler = OneByteInput(TerminalInputHandler())
    // initialize D5700 memory and load program
    val memoryDriver = MemoryDriver()
    memoryDriver.createMemoryDevice(4000, Writable) //RAM
    memoryDriver.createMemoryDevice(4000, Writable) //ROM
    LoadProgramFromD5700.loadProgram(memoryDriver,1,0,fileNameHandler.getInput(),false)
    memoryDriver.makeReadOnly(1)


}