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
        plmScoped {
            val module = TorchModule(localModulePath)
            val inputTensor = Tensor.fromBlob(
                FloatArray(10) { 0.0F },
                longArrayOf(1, 10),
                this,
            )
            val input = IValue.from(inputTensor)
            val output = module.runMethod("inference", input)
            assertTrue { output.isTensor() }
            val outputTensor = output.toTensor()

            assertEquals(10, outputTensor.getDataAsFloatArray().size)
            assertContentEquals(longArrayOf(1, 10), outputTensor.shape())
        }
    }

    @Test
    fun itCanRunForward() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val output = module.forward(
                IValue.from(
                    Tensor.fromBlob(
                        FloatArray(10) { 0.0F },
                        longArrayOf(1, 10),
                        this,
                    )
                )
            )
            val outputTensor = output.toTensor()
            assertEquals(10, outputTensor.getDataAsFloatArray().size)
            assertContentEquals(longArrayOf(1, 10), outputTensor.shape())
        }
    }

    @Test
    fun testIdentityLong() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val data = longArrayOf(3L, 2L, 0L, 0L, 1L, 6L)
            val shape = longArrayOf(2, 3)
            val tensor = Tensor.fromBlob(data, shape, this)
            val output = module.runMethod(
                "identity",
                IValue.from(tensor)
            )
            val outputTensor = output.toTensor()
            assertEquals(data.toList(), outputTensor.getDataAsLongArray().toList())
            assertEquals(shape.toList(), outputTensor.shape().toList())
        }
    }

    @Test
    fun testIdentity() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val data = floatArrayOf(0.86F, 1.36F, 0.51F, 0.45F, 0.37F, 1.84F)
            val shape = longArrayOf(2, 3)
            val tensor = Tensor.fromBlob(data, shape, this)
            val output = module.runMethod(
                "identity",
                IValue.from(tensor)
            )
            val outputTensor = output.toTensor()
            assertEquals(data.toList(), outputTensor.getDataAsFloatArray().toList())
            assertEquals(shape.toList(), outputTensor.shape().toList())
        }
    }

    @Test
    fun testSimilarity() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val output = module.runMethod(
                "similarity",
                IValue.from(
                    Tensor.fromBlob(
                        floatArrayOf(0.86F, 1.36F, 0.51F, 0.45F, 0.37F, 1.84F),
                        longArrayOf(2, 3),
                        this,
                    )
                ),
                IValue.from(
                    Tensor.fromBlob(
                        floatArrayOf(1.02F, 0.17F, 1.99F, 1.02F, 0.82F, 1.33F),
                        longArrayOf(2, 3),
                        this,
                    )
                )
            )
            val outputTensor = output.toTensor()
            val data = outputTensor.getDataAsFloatArray()
            assertEquals(listOf(2L), outputTensor.shape().toList())
            assertEquals(0.56F, data[0], 0.01F)
            assertEquals(0.89F, data[1], 0.01F)
        }
    }

    /*
    @Test
    fun itCanRunMethodWithDictInput() {
        val module = TorchModule(localModulePath)
        val input = IValue.dictStringKeyFrom(
            mapOf(
                "x" to IValue.from(
                    Tensor.fromBlob(
                        FloatArray(10) { 0.0F },
                        longArrayOf(1, 10),
                    )
                )
            )
        )
        val output = module.runMethod("inference_dict", input)
        val outputTensor = output.toTensor()
        assertEquals(10, outputTensor.getDataAsFloatArray().size)
        assertContentEquals(longArrayOf(1, 10), outputTensor.shape())
    }
    */
}