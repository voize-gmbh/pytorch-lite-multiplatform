import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.8.0"
    kotlin("native.cocoapods") version "1.8.0"
    id("com.android.library")
    id("com.adarshr.test-logger") version "3.1.0"
    id("convention.publication")
}

group = "de.voize"
version = "0.5.1"

repositories {
    google()
    mavenCentral()
}

kotlin {
    android {
        publishLibraryVariants("release")
    }

    ios {
        val libTorchPodDir = project.file("build/cocoapods/synthetic/IOS/Pods/LibTorch-Lite")
        val libTorchLibsDir = libTorchPodDir.resolve("install/lib")
        val libs = listOf(
            "c10", "torch", "torch_cpu", "XNNPACK", "clog",
            "cpuinfo", "eigen_blas", "pthreadpool", "pytorch_qnnpack"
        )

        binaries.all {
            linkerOpts(
                "-L${libTorchLibsDir.absolutePath}",
                *libs.map { "-l$it" }.toTypedArray(),
                "-force_load", libTorchLibsDir.resolve("libtorch.a").absolutePath,
                "-force_load", libTorchLibsDir.resolve("libtorch_cpu.a").absolutePath,
                "-all_load"
            )
        }
    }

    cocoapods {
        ios.deploymentTarget = "13.5"

        homepage = "https://github.com/voize-gmbh/pytorch-lite-multiplatform"
        summary = "Kotlin Multiplatform wrapper for PyTorch Lite"

        pod("PLMLibTorchWrapper") {
            version = "0.5.1"
            headers = "LibTorchWrapper.h"
            source = path(project.file("ios/LibTorchWrapper"))
        }

        useLibraries()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.suparnatural.kotlin:fs:1.1.0")
            }
        }
        val androidMain by getting {
            dependencies {
                rootProject
                implementation("org.pytorch:pytorch_android_lite:1.12.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
    }
}

fun createFrameworkFromStaticLib(platform: String) {
    val basePath = project.file("build/cocoapods/synthetic/IOS/build/Release-$platform/PLMLibTorchWrapper")
    val frameworkPath = basePath.resolve("PLMLibTorchWrapper.framework")
    frameworkPath.mkdir()
    val frameworkLibPath = frameworkPath.resolve("PLMLibTorchWrapper")
    basePath.resolve("libPLMLibTorchWrapper.a").copyTo(frameworkLibPath, overwrite = true)
}
tasks.named("linkPodDebugFrameworkIosArm64").configure {
    doFirst {
        createFrameworkFromStaticLib("iphoneos")
    }
}
tasks.named("linkPodDebugFrameworkIosX64").configure {
    doFirst {
        createFrameworkFromStaticLib("iphonesimulator")
    }
}

// inspired by: https://diamantidis.github.io/2019/08/25/kotlin-multiplatform-project-unit-tests-for-ios-and-android
task("iosSimulatorX64Test") {
    val device = "iPhone 12"
    val target = (kotlin.targets.getByName("iosX64") as KotlinNativeTarget)

    dependsOn(target.binaries.getTest("DEBUG").linkTaskName)
    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Runs iOS tests on a simulator"

    doLast {
        val binary = target.binaries.getTest("DEBUG").outputFile
        println(binary)
        exec {
            commandLine("xcrun", "simctl", "spawn", "--standalone", device, binary.absolutePath)
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
