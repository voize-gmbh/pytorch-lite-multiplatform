package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*

actual class TorchModule actual constructor(path: String) {
    private val module = LibTorchWrapper.TorchModule(fileAtPath = path)

    actual fun inference(
        data: FloatArray,
        shape: LongArray
    ): ModelOutput {
        memScoped {
            val cData = allocArray<FloatVar>(data.size)
            val cShape = allocArray<LongVar>(shape.size)
            data.forEachIndexed { index, value -> cData[index] = value }
            shape.forEachIndexed { index, value -> cShape[index] = value }
            val output = module.inferenceFloatInput(cData, cShape, shape.size.toULong())

            return output?.let {
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