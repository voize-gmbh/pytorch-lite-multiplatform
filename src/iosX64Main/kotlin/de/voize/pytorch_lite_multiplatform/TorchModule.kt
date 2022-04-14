package de.voize.pytorch_lite_multiplatform

actual class TorchModule actual constructor(path: String) {
    actual fun inference(
        inputIds: LongArray,
        shape: LongArray
    ): ModelOutput {
        TODO("Not yet implemented")
    }

    actual fun destroy() {
    }

}