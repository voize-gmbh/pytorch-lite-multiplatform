package de.voize.pytorch_lite_multiplatform

import org.pytorch.IValue
import org.pytorch.LiteModuleLoader

actual class TorchModule actual constructor(path: String) {
    private val module = LiteModuleLoader.load(path)

    actual fun forward(inputs: List<Tensor>): ModelOutput {
        val iValues = inputs.map { IValue.from(it.getTensor()) }.toTypedArray()
        val outputTensor = module.forward(*iValues).toTensor()
        return ModelOutput(outputTensor.dataAsFloatArray, outputTensor.shape())
    }

    actual fun destroy() {
        module.destroy()
    }
}