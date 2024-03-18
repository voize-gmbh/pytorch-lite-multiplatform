package de.voize.pytorch_lite_multiplatform

import com.suparnatural.core.fs.FileSystem
import kotlin.test.*
import cocoapods.PLMLibTorchWrapper.TorchModule as LibTorchWrapperTorchModule

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
    fun testTensorWrapperLong() {
        plmScoped {
            val data = longArrayOf(3L, 2L, 0L, 0L, 1L, 6L)
            val shape = longArrayOf(2, 3)
            val tensor = Tensor.fromBlob(data, shape, this)
            assertEquals(data.toList(), tensor.getDataAsLongArray().toList())
            assertEquals(shape.toList(), tensor.shape().toList())
        }
    }

    @Test
    fun testIValueWrapperList() {
        plmScoped {
            val a = IValue.from(1L)
            val b = IValue.from(2L)
            val wrapped = IValue.listFrom(a, b)
            val asList = wrapped.toList().map { it.toLong() }
            assertEquals(asList.first(), 1L)
            assertEquals(asList.last(), 2L)
        }
    }

    @Test
    fun testIValueWrapperTensors() {
        plmScoped {
            val a = longArrayOf(3L, 2L, 0L, 0L, 1L, 6L)
            val aShape = longArrayOf(2, 3)
            val b = longArrayOf(3L, 2L, 0L)
            val bShape = longArrayOf(3)
            val l = IValue.listFrom(
                Tensor.fromBlob(a, aShape, this),
                Tensor.fromBlob(b, bShape, this)
            )
            val asList = l.toList().map { it.toTensor().getDataAsLongArray() }
            assertEquals(asList.first().toList(), a.toList())
            assertEquals(asList.last().toList(), b.toList())
        }
    }

    @Test
    fun testIValueNull() {
        plmScoped {
            val a = IValue.optionalNull()
            val b = IValue.from(0.0)
            assertTrue(a.isNull())
            assertTrue(!b.isNull())
        }
    }

    @Test
    fun testIValueString() {
        plmScoped {
            val greeting = "hello world from pytorch-lite-multiplatform with▁unicode"
            val a = IValue.from(greeting)
            assertEquals(greeting, a.toStr())
        }
    }

    @Test
    fun testIValueStringIdentity() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val a = IValue.from("test")
            val output = module.runMethod("identity_string", a)
            assertEquals("test", output.toStr())
        }
    }

    @Test
    fun testIValueDictionaries() {
        plmScoped {
            val greeting = "hello world from pytorch-lite-multiplatform"
            val a = IValue.dictStringKeyFrom(mapOf(greeting to IValue.from(1L)))
            assertEquals(1L, a.toDictStringKey()[greeting]?.toLong())
            val b = IValue.dictLongKeyFrom(mapOf(0L to IValue.from(2L)))
            assertEquals(2L, b.toDictLongKey()[0L]?.toLong())
        }
    }

    @Test
    fun testIValueDictionariesIdentity() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val input = IValue.dictLongKeyFrom(
                mapOf(
                    0L to IValue.from(7L),
                )
            )
            val output = module.runMethod(
                "identity_dict_long",
                input
            )
            assertEquals(7L, output.toDictLongKey()[0L]?.toLong())
        }
    }

    /*
    @Test
    fun testIValueWrapperListWrongTypes() {
        plmScoped {
            val a = IValue.from(1L)
            val b = IValue.from(0.0)
            assertFailsWith<IllegalArgumentException> {
                IValue.listFrom(a, b)
            }
        }
    }
     */

    @Test
    fun testIValueWrapperTuple() {
        plmScoped {
            val a = IValue.from(1L)
            val b = IValue.from(0.123)
            val wrapped = IValue.tupleFrom(a, b)
            val asList = wrapped.toTuple()
            assertEquals(asList.first().toLong(), 1L)
            assertEquals(asList.last().toDouble(), 0.123)
        }
    }

    @Test
    fun testIdentityLong() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val input = IValue.from(0L)
            val output = module.runMethod(
                "identity_long",
                input
            )
            assertEquals(0L, output.toLong())
        }
    }

    @Test
    fun testIdentityBool() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val input = IValue.from(false)
            val output = module.runMethod(
                "identity_bool",
                input
            )
            assertEquals(false, output.toBool())

            val input2 = IValue.from(true)
            val output2 = module.runMethod(
                "identity_bool",
                input2
            )
            assertEquals(true, output2.toBool())
        }
    }

    @Test
    fun testIdentityBoolList() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val input = IValue.listFrom(IValue.from(true), IValue.from(false))
            val output = module.runMethod(
                "identity_bool_list",
                input
            )
            assertEquals(listOf(true, false), output.toBoolList())
        }
    }

    @Test
    fun testIdentityBoolList2() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val input = IValue.listFrom(true, false, scope = this)
            val output = module.runMethod(
                "identity_bool_list",
                input
            )
            assertEquals(listOf(true, false), output.toBoolList())
        }
    }

    @Test
    fun testIdentityTensor() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val data = floatArrayOf(0.86F, 1.36F, 0.51F, 0.45F, 0.37F, 1.84F)
            val shape = longArrayOf(2, 3)
            val tensor = Tensor.fromBlob(data, shape, this)
            val output = module.runMethod(
                "identity_tensor",
                IValue.from(tensor)
            )
            val outputTensor = output.toTensor()
            assertEquals(data.toList(), outputTensor.getDataAsFloatArray().toList())
            assertEquals(shape.toList(), outputTensor.shape().toList())
        }
    }

    @Test
    fun testIdentityNamedTuple() {
        plmScoped {
            val module = TorchModule(localModulePath)

            val a = IValue.from(1L)
            val b = IValue.from(2L)
            val tuple = IValue.tupleFrom(a, b)

            val output = module.runMethod(
                "identity_named_tuple",
                tuple,
            )

            val (outputA, outputB) = output.toTuple()
            assertEquals(outputA.toLong(), a.toLong())
            assertEquals(outputB.toLong(), b.toLong())
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

    @Test
    fun itCanRunMethodWithStringDictInput() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val input = IValue.dictStringKeyFrom(
                mapOf(
                    "x" to IValue.from(
                        Tensor.fromBlob(
                            FloatArray(10) { 0.0F },
                            longArrayOf(1, 10),
                            this,
                        )
                    )
                )
            )
            val output = module.runMethod("inference_dict_string", input)
            val outputTensor = output.toTensor()
            assertEquals(10, outputTensor.getDataAsFloatArray().size)
            assertContentEquals(longArrayOf(1, 10), outputTensor.shape())
        }
    }

    @Test
    fun itCanRunMethodWithLongDictInput() {
        plmScoped {
            val module = TorchModule(localModulePath)
            val input = IValue.dictLongKeyFrom(
                mapOf(
                    0L to IValue.from(
                        Tensor.fromBlob(
                            FloatArray(10) { 0.0F },
                            longArrayOf(1, 10),
                            this,
                        )
                    )
                )
            )
            val output = module.runMethod("inference_dict_long", input)
            val outputTensor = output.toTensor()
            assertEquals(10, outputTensor.getDataAsFloatArray().size)
            assertContentEquals(longArrayOf(1, 10), outputTensor.shape())
        }
    }
}