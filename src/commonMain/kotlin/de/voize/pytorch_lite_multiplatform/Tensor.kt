package de.voize.pytorch_lite_multiplatform

expect abstract class Tensor()

expect class LongTensor(data: LongArray, shape: LongArray) : Tensor

expect class FloatTensor(data: FloatArray, shape: LongArray) : Tensor