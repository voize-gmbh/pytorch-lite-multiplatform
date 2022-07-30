package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*
import cocoapods.PLMLibTorchWrapper.Tensor as NativeTensor

actual class Tensor internal constructor(val nativeTensor: NativeTensor) {
    actual fun getDataAsIntArray(): IntArray {
        val data = nativeTensor.getDataAsIntArray() as List<Int>
        return data.toIntArray()
    }

    actual fun getDataAsFloatArray(): FloatArray {
        val data = nativeTensor.getDataAsFloatArray() as List<Float>
        return data.toFloatArray()
    }

    actual fun getDataAsLongArray(): LongArray {
        val data = nativeTensor.getDataAsLongArray() as List<Long>
        return data.toLongArray()
    }

    actual fun getDataAsDoubleArray(): DoubleArray {
        val data = nativeTensor.getDataAsDoubleArray() as List<Double>
        return data.toDoubleArray()
    }

    actual fun shape(): LongArray {
        return (nativeTensor.shape() as List<Long>).toLongArray()
    }

    actual fun numel(): Long {
        return this.shape().fold(1) { acc, s -> acc * s }
    }

    actual companion object {
        actual fun fromBlob(
            data: IntArray,
            shape: LongArray,
            scope: PLMScope,
        ): Tensor = with(scope.nativePlacement) {
            val nativeTensor = NativeTensor(
                intData = allocArray(data.size) { value = data[it] },
                shape = allocArray(shape.size) { value = shape[it] },
                shapeLength = shape.size.toULong()
            )
            Tensor(nativeTensor)
        }

        actual fun fromBlob(
            data: FloatArray,
            shape: LongArray,
            scope: PLMScope,
        ): Tensor = with(scope.nativePlacement) {
            val nativeTensor = NativeTensor(
                floatData = allocArray(data.size) { value = data[it] },
                shape = allocArray(shape.size) { value = shape[it] },
                shapeLength = shape.size.toULong()
            )
            Tensor(nativeTensor)
        }

        actual fun fromBlob(
            data: LongArray,
            shape: LongArray,
            scope: PLMScope,
        ): Tensor = with(scope.nativePlacement) {
            val nativeTensor = NativeTensor(
                longData = allocArray(data.size) { value = data[it] },
                shape = allocArray(shape.size) { value = shape[it] },
                shapeLength = shape.size.toULong()
            )
            Tensor(nativeTensor)
        }

        actual fun fromBlob(
            data: DoubleArray,
            shape: LongArray,
            scope: PLMScope,
        ): Tensor = with(scope.nativePlacement) {
            val nativeTensor = NativeTensor(
                doubleData = allocArray(data.size) { value = data[it] },
                shape = allocArray(shape.size) { value = shape[it] },
                shapeLength = shape.size.toULong()
            )
            Tensor(nativeTensor)
        }
    }
}