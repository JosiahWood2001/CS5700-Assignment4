package org.example.app

class OneByteInput(wrapped: UserInputHandler) : InputHandlerDecorator(wrapped) {
    override fun getInput(): String {
        while (true) {
            val input = wrapped.getInput().trim()
            if (input.length == 2 && input.all { it.isDigit() || it.uppercaseChar() in 'A'..'F' }) {
                return input
            }
            println("Please enter exactly 2 hex characters (0-9, A-F). Try again:")
        }
    }
}