package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.PathComponent

actual class TorchModule actual constructor(path: PathComponent) {
    private val module = LibTorchWrapper.TorchModule(fileAtPath = path.toString())

    actual fun inference(
        inputIds: LongArray,
        typeIds: LongArray,
        shape: LongArray
    ): ModelOutput {
        return ModelOutput(floatArrayOf(), longArrayOf())
    }
}