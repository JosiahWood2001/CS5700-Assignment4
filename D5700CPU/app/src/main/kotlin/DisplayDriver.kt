package org.example.app

class DisplayDriver {
    private val displayList = mutableListOf<Display>()
    private fun checkBounds(displayIndex: Int) {
        if (displayIndex >= displayList.size || displayIndex < 0) {
            throw IndexOutOfBoundsException("No display device at index $displayIndex")
        }
    }
    fun draw(displayIndex: Int, x: Int, y: Int, value: ByteArray){
        checkBounds(displayIndex)
        displayList[displayIndex].draw(x, y, value)
    }
    fun getWidth(displayIndex: Int): Int{
        checkBounds(displayIndex)
        return displayList[displayIndex].width
    }
    fun getHeight(displayIndex: Int): Int{
        checkBounds(displayIndex)
        return displayList[displayIndex].height
    }
    fun getBitsPerPixel(displayIndex: Int): Int{
        checkBounds(displayIndex)
        return displayList[displayIndex].bitsPerPixel
    }
    fun clearDisplayBuffer(displayIndex: Int) {
        checkBounds(displayIndex)
        displayList[displayIndex].clearBuffer()
    }
    fun display(displayIndex: Int) {
        checkBounds(displayIndex)
        displayList[displayIndex].display()
    }
    fun displayAll(){
        for (display in displayList){
            display.display()
        }
    }
    fun clearAllBuffers(){
        for (display in displayList){
            display.clearBuffer()
        }
    }
    fun addDisplay(width: Int, height: Int, bitsPerPixel: Int, displayBehavior: DisplayBehavior){
        displayList.add(D5700Display(width, height, bitsPerPixel, displayBehavior))
    }
    fun numberOfDevices(): Int{
        return displayList.size
    }

}