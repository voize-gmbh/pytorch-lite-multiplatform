package de.voize.pytorch_lite_multiplatform

import org.pytorch.LiteModuleLoader

actual class TorchModule actual constructor(path: String) {
    private val module = LiteModuleLoader.load(path)

    actual fun runMethod(methodName: String, vararg inputs: IValue): IValue {
        return IValue(module.runMethod(methodName, *inputs.map { it.nativeIValue }.toTypedArray()))
    }

    actual fun forward(vararg inputs: IValue): IValue {
        return IValue(module.forward(*inputs.map { it.nativeIValue }.toTypedArray()))
    }

    actual fun destroy() {
        module.destroy()
    }
}