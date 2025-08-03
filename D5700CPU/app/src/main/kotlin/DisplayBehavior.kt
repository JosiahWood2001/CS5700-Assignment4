package org.example.app

interface DisplayBehavior {
    fun display(width: Int, height: Int, bytesPerPixel: Int, frameBuffer: Memory)
}