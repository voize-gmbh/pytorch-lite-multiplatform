package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.PathComponent

class ModelOutput(
    val data: FloatArray,
    val shape: LongArray
)

expect class TorchModule(path: PathComponent) {
    fun inference(
        inputIds: LongArray,
        shape: LongArray
    ): ModelOutput

    fun destroy()
}