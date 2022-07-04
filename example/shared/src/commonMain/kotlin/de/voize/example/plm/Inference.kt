package de.voize.example.plm

import com.suparnatural.core.fs.FileSystem
import de.voize.pytorch_lite_multiplatform.FloatTensor
import de.voize.pytorch_lite_multiplatform.TorchModule

class Inference {
    private val contentsDir = FileSystem.contentsDirectory.absolutePath
    private val localModulePath = contentsDir?.byAppending("dummy_module.ptl")?.component!!

    fun run() {
        println("Loading module from file $localModulePath")

        if (!FileSystem.exists(localModulePath)) {
            throw Exception("File not found")
        }

        val module = TorchModule(localModulePath)
        val tensor = FloatTensor(FloatArray(10) { 1.0F }, longArrayOf(1, 10))

        println("Running inference")
        val output = module.forward(listOf(tensor))

        println("Inference output shape: ${output.shape.toList()}")
    }
}