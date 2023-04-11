import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.ByteArrayOutputStream

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

    ios()

    cocoapods {
        ios.deploymentTarget = "13.5"

        homepage = "https://github.com/voize-gmbh/pytorch-lite-multiplatform"
        summary = "Kotlin Multiplatform wrapper for PyTorch Lite"

        framework {
            isStatic = true
        }

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
                implementation("junit:junit:4.13.2")
                implementation("androidx.test:core:1.5.0")
                implementation("androidx.test:rules:1.5.0")
                implementation("androidx.test:runner:1.5.2")
                implementation("androidx.test.ext:junit:1.1.5")
            }
        }
    }
}

tasks.named("linkDebugTestIosX64").configure {
    doFirst {
        val target = (kotlin.targets.getByName("iosX64") as KotlinNativeTarget)

        target.binaries.all {
            val syntheticIOSProjectDir = project.file("build/cocoapods/synthetic/IOS")
            val libTorchPodDir = syntheticIOSProjectDir.resolve("Pods/LibTorch-Lite")
            val libTorchLibsDir = libTorchPodDir.resolve("install/lib")
            val podBuildDir = syntheticIOSProjectDir.resolve("build/Release-iphonesimulator")

            linkerOpts(
                "-L${libTorchLibsDir.absolutePath}",
                "-lc10", "-ltorch", "-ltorch_cpu", "-lXNNPACK",
                "-lclog", "-lcpuinfo", "-leigen_blas", "-lpthreadpool", "-lpytorch_qnnpack",
                "-force_load", libTorchLibsDir.resolve("libtorch.a").absolutePath,
                "-force_load", libTorchLibsDir.resolve("libtorch_cpu.a").absolutePath,
                "-all_load",
                "-L${podBuildDir.resolve("PLMLibTorchWrapper").absolutePath}",
                "-lPLMLibTorchWrapper"
            )
        }
    }
}

// inspired by: https://diamantidis.github.io/2019/08/25/kotlin-multiplatform-project-unit-tests-for-ios-and-android
task("iosSimulatorX64Test") {
    val target = (kotlin.targets.getByName("iosX64") as KotlinNativeTarget)

    dependsOn(target.binaries.getTest("DEBUG").linkTaskName)
    group = JavaBasePlugin.VERIFICATION_GROUP
    description = "Runs iOS tests on a simulator"

    doLast {
        println("Retrieving runtime for iOS simulator")
        val iOSRuntimesOutput = ByteArrayOutputStream()
        exec {
            commandLine("xcrun", "simctl", "list", "--json", "runtimes", "iOS")
            standardOutput = iOSRuntimesOutput
        }

        val iOSRuntimesData = groovy.json.JsonSlurper().parseText(iOSRuntimesOutput.toString()) as Map<String, List<Map<String, Any>>>
        val runtimesIdentifiers = iOSRuntimesData["runtimes"]!!.map { it["identifier"]!! } as List<String>
        val latestRuntimeIdentifier = runtimesIdentifiers.maxOrNull()!!
        println("Latest iOS runtime: $latestRuntimeIdentifier")

        println("Retrieving device for iOS simulator")
        val devicesOutput = ByteArrayOutputStream()
        exec {
            commandLine("xcrun", "simctl", "list", "--json", "devices")
            standardOutput = devicesOutput
        }
        val devicesData = groovy.json.JsonSlurper().parseText(devicesOutput.toString()) as Map<String, Map<String, List<Map<String, String>>>>
        val devices = devicesData["devices"]!!
        val deviceName = "iPhone 12"
        val device = devices[latestRuntimeIdentifier]!!.find { it["name"] == deviceName }
        val udid = device!!["udid"]
        println("Using device: $deviceName ($udid)")

        exec {
            println("Building test model")
            commandLine("python3", "build_dummy_model.py")
        }

        val simulatorFilesPath = "/Users/${System.getProperty("user.name")}/Library/Developer/CoreSimulator/Devices/$udid/data/Documents"

        exec {
            println("Setting up iOS simulator documents directory")
            commandLine("mkdir", "-p", simulatorFilesPath)
        }

        exec {
            println("Copying model to iOS simulator files ($simulatorFilesPath)")
            commandLine("cp", "dummy_module.ptl", simulatorFilesPath)
        }

        exec {
            println("Running simulator tests")
            val binary = target.binaries.getTest("DEBUG").outputFile
            commandLine("xcrun", "simctl", "spawn", "--standalone", udid, binary.absolutePath)
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
