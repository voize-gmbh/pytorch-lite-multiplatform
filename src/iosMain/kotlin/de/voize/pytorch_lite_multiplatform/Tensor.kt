package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*
import cocoapods.LibTorchWrapper.Tensor as LibTorchWrapperTensor

actual abstract class Tensor {
    abstract fun getTensor(nativePlacement: NativePlacement): LibTorchWrapperTensor
}

actual class LongTensor actual constructor(
    private val data: LongArray,
    private val shape: LongArray
) : Tensor() {
    override fun getTensor(nativePlacement: NativePlacement): LibTorchWrapperTensor {
        return with(nativePlacement) {
            val cData = allocArray<LongVar>(data.size)
            val cShape = allocArray<LongVar>(shape.size)
            data.forEachIndexed { index, value -> cData[index] = value }
            shape.forEachIndexed { index, value -> cShape[index] = value }
            LibTorchWrapperTensor(longData = cData, shape = cShape, shapeLength = shape.size.toULong())
        }
    }
}

actual class FloatTensor actual constructor(
    private val data: FloatArray,
    private val shape: LongArray
) : Tensor() {
    override fun getTensor(nativePlacement: NativePlacement): LibTorchWrapperTensor {
        return with(nativePlacement) {
            val cData = allocArray<FloatVar>(data.size)
            val cShape = allocArray<LongVar>(shape.size)
            data.forEachIndexed { index, value -> cData[index] = value }
            shape.forEachIndexed { index, value -> cShape[index] = value }
            LibTorchWrapperTensor(floatData = cData, shape = cShape, shapeLength = shape.size.toULong())
        }
    }
}