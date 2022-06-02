package de.voize.pytorch_lite_multiplatform

class ModelOutput(
    val data: FloatArray,
    val shape: LongArray
)

expect class TorchModule(path: String) {
    fun runMethod(methodName: String, inputs: List<Tensor>): ModelOutput

    fun runMethod(methodName: String, inputs: Map<String, Tensor>): ModelOutput

    fun forward(inputs: List<Tensor>): ModelOutput

    fun forward(inputs: Map<String, Tensor>): ModelOutput

    fun destroy()
}