package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*

actual class TorchModule actual constructor(path: String) {
    private val module = LibTorchWrapper.TorchModule(fileAtPath = path)

    actual fun forward(inputs: List<Tensor>): ModelOutput {
        return memScoped {
            val output = module.forward(inputs.map { it.getTensor() })

            output?.let {
                ModelOutput(
                    (it.data as List<Float>).toFloatArray(),
                    (it.shape as List<Long>).toLongArray(),
                )
            } ?: throw IllegalArgumentException("Model output can not be null")
        }
    }

    actual fun forward(inputs: Map<String, Tensor>): ModelOutput {
        return memScoped {
            val output = module.forwardMap(inputs.mapValues { it.value.getTensor() })

            output?.let {
                ModelOutput(
                    (it.data as List<Float>).toFloatArray(),
                    (it.shape as List<Long>).toLongArray(),
                )
            } ?: throw IllegalArgumentException("Model output can not be null")
        }
    }

    actual fun destroy() {

    }
}
