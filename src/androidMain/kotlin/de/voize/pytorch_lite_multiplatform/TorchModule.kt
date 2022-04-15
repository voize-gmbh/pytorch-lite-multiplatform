package de.voize.pytorch_lite_multiplatform

import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Tensor

actual class TorchModule actual constructor(path: String) {
    private val module = LiteModuleLoader.load(path)

    actual fun inference(
        data: FloatArray,
        shape: LongArray
    ): ModelOutput {
        val inputIdsTensor = Tensor.fromBlob(data, shape)
        val outputTensor = module.forward(IValue.from(inputIdsTensor)).toTensor()
        return ModelOutput(outputTensor.dataAsFloatArray, outputTensor.shape())
    }

    actual fun destroy() {
        module.destroy()
    }
}