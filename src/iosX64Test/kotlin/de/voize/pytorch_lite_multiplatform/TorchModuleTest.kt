package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.FileSystem
import kotlinx.cinterop.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TorchModuleTest {
    private val contentsDir = FileSystem.contentsDirectory.absolutePath

    @Test
    fun itCanLoadLibTorchModule() {
        val localModulePath = contentsDir?.byAppending("dummy_module.ptl")?.component!!
        val module = LibTorchWrapper.TorchModule(fileAtPath = localModulePath)
        assertNotNull(module)

        val output = memScoped {
            val input: CPointer<FloatVar> = FloatArray(10) { 0.0F }.toCValues().ptr
            val shape: CPointer<LongVar> = longArrayOf(1, 10).toCValues().ptr
            module.inferenceFloatInput(input, shape, 2)
        }

        assertEquals(10, output?.data?.size)
        assertEquals(listOf<Long>(1, 10), output?.shape)
    }
}