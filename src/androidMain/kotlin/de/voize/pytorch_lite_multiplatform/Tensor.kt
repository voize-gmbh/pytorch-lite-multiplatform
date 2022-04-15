package de.voize.pytorch_lite_multiplatform

import org.pytorch.Tensor as TorchTensor

actual abstract class Tensor {
    abstract fun getTensor(): TorchTensor
}

actual class LongTensor actual constructor(data: LongArray, shape: LongArray) : Tensor() {
    private val tensor = TorchTensor.fromBlob(data, shape)

    override fun getTensor(): TorchTensor {
        return tensor
    }
}

actual class FloatTensor actual constructor(data: FloatArray, shape: LongArray) : Tensor() {
    private val tensor = TorchTensor.fromBlob(data, shape)

    override fun getTensor(): TorchTensor {
        return tensor
    }
}