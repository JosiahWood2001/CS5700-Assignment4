package org.example.app

abstract class InputHandlerDecorator(
    protected val wrapped: UserInputHandler
) : UserInputHandler {
    override fun getInput(): String = wrapped.getInput()
}