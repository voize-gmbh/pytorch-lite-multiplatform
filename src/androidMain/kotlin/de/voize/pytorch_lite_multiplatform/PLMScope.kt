package de.voize.pytorch_lite_multiplatform

actual class PLMScope

actual fun <R> plmScoped(block: PLMScope.()->R): R {
    return PLMScope().block()
}