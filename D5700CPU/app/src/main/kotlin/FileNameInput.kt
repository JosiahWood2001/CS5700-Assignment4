package org.example.app

class FileNameInput(wrapped: UserInputHandler) : InputHandlerDecorator(wrapped) {
    override fun getInput(): String {
        println("Please enter the program file name (e.g. addition.d5700 or addition.out):")
        return wrapped.getInput().trim()
    }
}