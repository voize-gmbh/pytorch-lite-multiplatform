package de.voize.pytorch_lite_multiplatform

import de.voize.pytorch.LiteModuleLoader

// useMmap is accepted but ignored: LiteModuleLoader does not expose the load
// options needed to reach the mmap path, so Android always copies the weights.
actual class TorchModule actual constructor(path: String, useMmap: Boolean) {
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