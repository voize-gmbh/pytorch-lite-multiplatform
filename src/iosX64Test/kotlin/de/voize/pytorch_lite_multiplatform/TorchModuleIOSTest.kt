package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.FileSystem
import cocoapods.PLMLibTorchWrapper.TorchModule as LibTorchWrapperTorchModule
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

    @Test
    fun testIdentityLong() {
        val module = TorchModule(localModulePath)
        val data = longArrayOf(3L, 2L, 0L, 0L, 1L, 6L)
        val shape = longArrayOf(2, 3)
        val tensor = LongTensor(data, shape)
        val output = module.runMethod(
            "identity",
            listOf(tensor)
        )
        assertEquals(data.toList().map { it.toFloat() }, output.data.toList())
        assertEquals(shape.toList(), output.shape.toList())
    }

    @Test
    fun testIdentity() {
        val module = TorchModule(localModulePath)
        val data = floatArrayOf(0.86F, 1.36F, 0.51F, 0.45F, 0.37F, 1.84F)
        val shape = longArrayOf(2, 3)
        val tensor = FloatTensor(data, shape)
        val output = module.runMethod(
            "identity",
            listOf(tensor)
        )
        assertEquals(data.toList(), output.data.toList())
        assertEquals(shape.toList(), output.shape.toList())
    }

    @Test
    fun testSimilarity() {
        val module = TorchModule(localModulePath)
        val output = module.runMethod(
            "similarity",
            listOf(
                FloatTensor(
                    floatArrayOf(0.86F, 1.36F, 0.51F, 0.45F, 0.37F, 1.84F),
                    longArrayOf(2, 3),
                ),
                FloatTensor(
                    floatArrayOf(1.02F, 0.17F, 1.99F, 1.02F, 0.82F, 1.33F),
                    longArrayOf(2, 3),
                )
            )
        )

        assertEquals(listOf(2L), output.shape.toList())
        assertEquals(0.56F, output.data[0], 0.01F)
        assertEquals(0.89F, output.data[1], 0.01F)
    }
}