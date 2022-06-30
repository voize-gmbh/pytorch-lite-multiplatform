package de.voize.pytorch_lite_multiplatform

expect class Tensor {
    fun getDataAsIntArray(): IntArray
    fun getDataAsFloatArray(): FloatArray
    fun getDataAsLongArray(): LongArray
    fun getDataAsDoubleArray(): DoubleArray
    fun shape(): LongArray
    fun numel(): Long

    companion object {
        fun fromBlob(data: IntArray, shape: LongArray, scope: PLMScope): Tensor
        fun fromBlob(data: FloatArray, shape: LongArray, scope: PLMScope): Tensor
        fun fromBlob(data: LongArray, shape: LongArray, scope: PLMScope): Tensor
        fun fromBlob(data: DoubleArray, shape: LongArray, scope: PLMScope): Tensor
    }
}

fun Tensor.toString() = "Tensor(shape=${shape().toList()})"