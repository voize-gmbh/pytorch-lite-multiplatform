package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*
import cocoapods.LibTorchWrapper.TorchModule as LibTorchWrapperTorchModule

actual class TorchModule actual constructor(path: String) {
    private val module = LibTorchWrapperTorchModule(fileAtPath = path)

    actual fun runMethod(methodName: String, inputs: List<Tensor>): ModelOutput {
        return memScoped {
            val output = module.runMethod(methodName, inputs.map { it.getTensor() })

            output?.let {
                ModelOutput(
                    (it.data as List<Float>).toFloatArray(),
                    (it.shape as List<Long>).toLongArray(),
                )
            } ?: throw IllegalArgumentException("Model output can not be null")
        }
    }

    actual fun runMethod(methodName: String, inputs: Map<String, Tensor>): ModelOutput {
        return memScoped {
            val output = module.runMethodMap(methodName, inputs.mapValues { it.value.getTensor() })

            output?.let {
                ModelOutput(
                    (it.data as List<Float>).toFloatArray(),
                    (it.shape as List<Long>).toLongArray(),
                )
            } ?: throw IllegalArgumentException("Model output can not be null")
        }
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
