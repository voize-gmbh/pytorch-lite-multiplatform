package de.voize.pytorch_lite_multiplatform

import de.voize.pytorch.MemoryFormat
import de.voize.pytorch.Tensor as NativeTensor

actual class Tensor internal constructor(internal val nativeTensor: NativeTensor) {
    actual fun getDataAsIntArray() = nativeTensor.dataAsIntArray
    actual fun getDataAsFloatArray() = nativeTensor.dataAsFloatArray
    actual fun getDataAsLongArray() = nativeTensor.dataAsLongArray
    actual fun getDataAsDoubleArray() = nativeTensor.dataAsDoubleArray
    actual fun shape() = nativeTensor.shape()
    actual fun numel() = nativeTensor.numel()

    actual companion object {
        actual fun fromBlob(data: IntArray, shape: LongArray, scope: PLMScope): Tensor {
            return Tensor(NativeTensor.fromBlob(data, shape, MemoryFormat.CONTIGUOUS))
        }

        actual fun fromBlob(data: FloatArray, shape: LongArray, scope: PLMScope): Tensor {
            return Tensor(NativeTensor.fromBlob(data, shape, MemoryFormat.CONTIGUOUS))
        }

        actual fun fromBlob(data: LongArray, shape: LongArray, scope: PLMScope): Tensor {
            return Tensor(NativeTensor.fromBlob(data, shape, MemoryFormat.CONTIGUOUS))
        }

        actual fun fromBlob(data: DoubleArray, shape: LongArray, scope: PLMScope): Tensor {
            return Tensor(NativeTensor.fromBlob(data, shape, MemoryFormat.CONTIGUOUS))
        }
    }
}
