package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*
import cocoapods.PLMLibTorchWrapper.TorchModule as LibTorchWrapperTorchModule

actual class TorchModule actual constructor(path: String) {
    private val module = LibTorchWrapperTorchModule(fileAtPath = path)

    actual fun runMethod(methodName: String, inputs: List<Tensor>): ModelOutput {
        val output = memScoped {
            module.runMethod(methodName, inputs.map { it.getTensor(this) })
        } ?: throw IllegalArgumentException("Model output can not be null")

        return ModelOutput(
            (output.data as List<Float>).toFloatArray(),
            (output.shape as List<Long>).toLongArray(),
        )
    }

    actual fun runMethod(methodName: String, inputs: Map<String, Tensor>): ModelOutput {
        val output = memScoped {
            module.runMethodMap(methodName, inputs.mapValues { it.value.getTensor(this) })
        } ?: throw IllegalArgumentException("Model output can not be null")

        return ModelOutput(
            (output.data as List<Float>).toFloatArray(),
            (output.shape as List<Long>).toLongArray(),
        )
    }

    actual fun forward(inputs: List<Tensor>): ModelOutput {
        return runMethod("forward", inputs)
    }

    actual fun forward(inputs: Map<String, Tensor>): ModelOutput {
        return runMethod("forward", inputs)
    }

    actual fun destroy() {

    }
}
