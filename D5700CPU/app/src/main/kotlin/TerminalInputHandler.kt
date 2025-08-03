package org.example.app

class TerminalInputHandler : UserInputHandler {
    override fun getInput(): String {
        return readLine() ?: ""
    }
}