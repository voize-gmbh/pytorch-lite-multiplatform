package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.FileSystem
import cocoapods.LibTorchWrapper.TorchModule as LibTorchWrapperTorchModule
import kotlin.test.*

class TorchModuleIOSTest {
    private val contentsDir = FileSystem.contentsDirectory.absolutePath
    private val localModulePath = contentsDir?.byAppending("dummy_module.ptl")?.component!!

    @Test
    fun itCanLoadRawLibTorchModule() {
        println(localModulePath)
        val module = LibTorchWrapperTorchModule(fileAtPath = localModulePath)
        assertNotNull(module)
    }

    @Test
    fun itCanRunMethod() {
        val module = TorchModule(localModulePath)
        val output = module.runMethod("inference", listOf(FloatTensor(
            FloatArray(10) { 0.0F },
            longArrayOf(1, 10),
        )))
        assertEquals(10, output.data.size)
        assertContentEquals(longArrayOf(1, 10), output.shape)
    }

    @Test
    fun itCanRunMethodWithDictInput() {
        val module = TorchModule(localModulePath)
        val output = module.runMethod(
            "inference_dict",
            mapOf(
                "x" to FloatTensor(
                    FloatArray(10) { 0.0F },
                    longArrayOf(1, 10),
                )
            )
        )
        assertEquals(10, output.data.size)
        assertContentEquals(longArrayOf(1, 10), output.shape)
    }

    @Test
    fun itCanRunForward() {
        val module = TorchModule(localModulePath)
        val output = module.forward(listOf(FloatTensor(
            FloatArray(10) { 0.0F },
            longArrayOf(1, 10),
        )))
        assertEquals(10, output.data.size)
        assertContentEquals(longArrayOf(1, 10), output.shape)
    }
}