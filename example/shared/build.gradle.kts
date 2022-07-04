plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

version = "1.0"

kotlin {
    android()
    ios()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "shared"
        }

        pod("PLMLibTorchWrapper") {
            version = "0.4.0"
        }

        useLibraries()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("de.voize:pytorch-lite-multiplatform:0.4.0")
                implementation("com.suparnatural.kotlin:fs:1.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosMain by getting
        val iosTest by getting
    }
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.DefFileTask>("generateDefPLMLibTorchWrapper").configure {
    doLast {
        outputFile.writeText("""
            language = Objective-C
            headers = LibTorchWrapper.h
        """.trimIndent())
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 32
    }
}