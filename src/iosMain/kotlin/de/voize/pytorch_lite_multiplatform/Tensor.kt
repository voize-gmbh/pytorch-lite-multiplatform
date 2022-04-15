package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*

actual abstract class Tensor {
    abstract fun getTensor(): LibTorchWrapper.Tensor
}

actual class LongTensor actual constructor(
    private val data: LongArray,
    private val shape: LongArray
) : Tensor() {
    override fun getTensor(): LibTorchWrapper.Tensor {
        return memScoped {
            val cData = allocArray<LongVar>(data.size)
            val cShape = allocArray<LongVar>(shape.size)
            data.forEachIndexed { index, value -> cData[index] = value }
            shape.forEachIndexed { index, value -> cShape[index] = value }
            LibTorchWrapper.Tensor(longData = cData, shape = cShape, shapeLength = shape.size.toULong())
        }
    }
}

actual class FloatTensor actual constructor(
    private val data: FloatArray,
    private val shape: LongArray
) : Tensor() {
    override fun getTensor(): LibTorchWrapper.Tensor {
        return memScoped {
            val cData = allocArray<FloatVar>(data.size)
            val cShape = allocArray<LongVar>(shape.size)
            data.forEachIndexed { index, value -> cData[index] = value }
            shape.forEachIndexed { index, value -> cShape[index] = value }
            LibTorchWrapper.Tensor(floatData = cData, shape = cShape, shapeLength = shape.size.toULong())
        }
    }
}