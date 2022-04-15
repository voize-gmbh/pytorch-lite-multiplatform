package de.voize.pytorch_lite_multiplatform

class ModelOutput(
    val data: FloatArray,
    val shape: LongArray
)

expect class TorchModule(path: String) {
    fun forward(inputs: List<Tensor>): ModelOutput

    fun destroy()
}