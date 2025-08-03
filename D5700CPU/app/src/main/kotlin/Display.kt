package org.example.app

interface Display {
    val width: Int
    val height: Int
    val bitsPerPixel: Int
    fun draw(x:Int,y:Int,value:ByteArray)
    fun display()
    fun clearBuffer()
}