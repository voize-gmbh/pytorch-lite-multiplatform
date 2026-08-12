package de.voize.pytorch_lite_multiplatform

/**
 * Process-wide defaults applied to modules loaded without an explicit override.
 *
 * Intended to be configured once during application startup, before any model is loaded, so that a
 * host app can flip loading behaviour from a single place (e.g. a remote feature flag) without
 * having to thread the setting through every model that loads a [TorchModule]. Values are read when
 * a [TorchModule] is constructed, so changing one only affects modules loaded afterwards.
 */
object TorchModuleDefaults {
    /**
     * Default for [TorchModule]'s `useMmap` parameter.
     *
     * Deliberately a plain mutable flag rather than a synchronized one: it is meant to be assigned
     * once at startup and only read afterwards, so the worst case for a late write racing a load is
     * that one model is loaded with the previous value.
     */
    var useMmap: Boolean = false
}

/**
 * A loaded TorchScript module.
 *
 * @param path path to the serialized model.
 * @param useMmap request that the model file be memory-mapped, so that weight storages point into a
 *   read-only mapping of the file instead of being copied onto the heap. Those pages stay clean and
 *   file-backed, so the OS can evict them under memory pressure rather than counting them against
 *   the process's memory footprint -- which is what matters on iOS, where exceeding the limit gets
 *   the process terminated. Defaults to [TorchModuleDefaults.useMmap].
 *
 *   This is a hint, not a guarantee. Only records that were serialized uncompressed and aligned can
 *   be mapped; anything else is copied as usual, so enabling it is always safe but the benefit
 *   depends on the model. Models written by PyTorch itself qualify; a `.ptl` repacked by an ordinary
 *   zip tool may not. Weights that a backend repacks at load time (byte-quantized embedding tables,
 *   XNNPACK-prepacked layers) are materialized on the heap regardless.
 *
 *   Currently honoured on iOS only; other targets accept and ignore it.
 */
expect class TorchModule(path: String, useMmap: Boolean = TorchModuleDefaults.useMmap) {
    fun runMethod(methodName: String, vararg inputs: IValue): IValue

    fun forward(vararg inputs: IValue): IValue

    fun destroy()
}
