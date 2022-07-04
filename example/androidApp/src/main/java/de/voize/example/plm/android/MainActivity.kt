package de.voize.example.plm.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.voize.example.plm.Inference
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private fun setupModelFile() {
        val assetFile = assets.open("dummy_module.ptl")
        val dest = filesDir.resolve("dummy_module.ptl").absolutePath
        File(dest).createNewFile()
        val out = FileOutputStream(dest)
        assetFile.copyTo(out)
        out.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupModelFile()
        Inference().run()
    }
}
