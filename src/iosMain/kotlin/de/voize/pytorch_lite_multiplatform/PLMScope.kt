package de.voize.pytorch_lite_multiplatform

import kotlinx.cinterop.*

actual class PLMScope(val nativePlacement: NativePlacement)

actual fun <R> plmScoped(block: PLMScope.()->R): R {
    return memScoped {
        val scope = PLMScope(this)
        scope.block()
    }
}
