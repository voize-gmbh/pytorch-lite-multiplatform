package de.voize.pytorch_lite_multiplatform

expect class TorchModule(path: String) {
    fun runMethod(methodName: String, vararg inputs: IValue): IValue

    fun forward(vararg inputs: IValue): IValue

    fun destroy()
}