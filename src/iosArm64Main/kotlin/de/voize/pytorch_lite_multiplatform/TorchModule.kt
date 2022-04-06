package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.PathComponent
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.set

actual class TorchModule actual constructor(path: PathComponent) {
    private val module = LibTorchWrapper.TorchModule(fileAtPath = path.toString())

    actual fun inference(
        inputIds: LongArray,
        shape: LongArray
    ): ModelOutput {
        memScoped {
            val cInputIds = allocArray<LongVar>(inputIds.size)
            val cShape = allocArray<LongVar>(shape.size)
            inputIds.forEachIndexed { index, value -> cInputIds[index] = value }
            shape.forEachIndexed { index, value -> cShape[index] = value }
            val output = module.inferenceLongInput(cInputIds, cShape, shape.size.toULong())

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