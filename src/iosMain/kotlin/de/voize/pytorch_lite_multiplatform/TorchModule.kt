package de.voize.pytorch_lite_multiplatform

import cocoapods.PLMLibTorchWrapper.IValueWrapper
import cocoapods.PLMLibTorchWrapper.TorchModule as LibTorchWrapperTorchModule

actual class TorchModule actual constructor(path: String) {
    private val module = LibTorchWrapperTorchModule(fileAtPath = path)

    actual fun runMethod(
        methodName: String,
        vararg inputs: IValue
    ): IValue {
        val newInputs = inputs.map { it.nativeIValue }
        val nativeIValue = module.runMethod(methodName, newInputs)!!
        return IValue(nativeIValue)
    }

    actual fun forward(vararg inputs: IValue): IValue {
        return runMethod("forward", *inputs)
    }

    actual fun destroy() {
    }

}
