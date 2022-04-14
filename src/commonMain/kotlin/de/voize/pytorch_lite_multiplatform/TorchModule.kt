package de.voize.pytorch_lite_multiplatform

class ModelOutput(
    val data: FloatArray,
    val shape: LongArray
)

expect class TorchModule(path: String) {
    fun inference(
        inputIds: LongArray,
        shape: LongArray
    ): ModelOutput

    fun destroy()
}