package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.FileSystem
import kotlinx.cinterop.*
import kotlin.test.*

class TorchModuleTest {
    private val contentsDir = FileSystem.contentsDirectory.absolutePath
    private val localModulePath = contentsDir?.byAppending("dummy_module.ptl")?.component!!

    @Test
    fun itCanLoadRawLibTorchModule() {
        val module = LibTorchWrapper.TorchModule(fileAtPath = localModulePath)
        assertNotNull(module)
    }

    @Test
    fun itCanRunInference() {
        val module = TorchModule(localModulePath)

        val tensor = FloatTensor(
            FloatArray(10) { 0.0F },
            longArrayOf(1, 10),
        )

        val output = module.forward(listOf(tensor))

        assertEquals(10, output.data.size)
        assertContentEquals(longArrayOf(1, 10), output.shape)
    }
}