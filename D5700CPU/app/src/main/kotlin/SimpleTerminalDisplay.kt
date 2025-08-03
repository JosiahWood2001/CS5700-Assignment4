package org.example.app

object SimpleTerminalDisplay: DisplayBehavior {
    override fun display(width: Int, height: Int, bytesPerPixel: Int, frameBuffer: Memory) {
        for (row in 0 until height){
            for (col in 0 until width){
                val byte: Byte = frameBuffer.read((col+row*width)*bytesPerPixel)
                print(byte.toInt().toChar().toString())
            }
            println()
        }
        println()
    }
}