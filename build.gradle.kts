import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.ByteArrayOutputStream

plugins {
    kotlin("multiplatform") version "1.9.10"
    kotlin("native.cocoapods") version "1.9.10"
    id("com.android.library")
    id("com.adarshr.test-logger") version "3.1.0"
    id("convention.publication")
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    targetHierarchy.default()

    androidTarget {
        publishLibraryVariants("release")
    }

    ios()
    iosSimulatorArm64()

    cocoapods {
        ios.deploymentTarget = "13.5"

        homepage = "https://github.com/voize-gmbh/pytorch-lite-multiplatform"
        summary = "Kotlin Multiplatform wrapper for PyTorch Lite"

        framework {
            isStatic = true
        }

        pod("PLMLibTorchWrapper") {
            version = "0.7.0"
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
                implementation("com.squareup.okio:okio:3.3.0")
            }
        }
        val androidMain by getting {
            dependencies {
                rootProject
                implementation("de.voize.pytorch:pytorch_android_lite:2.8.2")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}

configureIosSimulatorLinkTask("linkDebugTestIosX64", "iosX64")
configureIosSimulatorLinkTask("linkDebugTestIosSimulatorArm64", "iosSimulatorArm64")

createIosSimulatorTestTask("iosInstrumentedSimulatorX64Test", "iosX64")
createIosSimulatorTestTask("iosInstrumentedSimulatorArm64Test", "iosSimulatorArm64")

android {
    namespace = "de.voize.pytorch_lite_multiplatform"

    compileSdkVersion(31)

    defaultConfig {
        minSdkVersion(26)
        targetSdkVersion(31)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
}

fun configureIosSimulatorLinkTask(taskName: String, targetName: String) {
    tasks.named(taskName).configure {
        doFirst {
            val target = (kotlin.targets.getByName(targetName) as KotlinNativeTarget)

            target.binaries.all {
                val syntheticIOSProjectDir = project.file("build/cocoapods/synthetic/ios")
                val libTorchPodDir = syntheticIOSProjectDir.resolve("Pods/VoizeLibTorch-Lite")
                val frameworkLibsDir = libTorchPodDir.resolve("LibTorchLite.xcframework/ios-arm64_x86_64-simulator")
                val buildDir = syntheticIOSProjectDir.resolve("build/Release-iphonesimulator")

                linkerOpts(
                    "-L${frameworkLibsDir.absolutePath}",
                    "-L${buildDir.resolve("PLMLibTorchWrapper").absolutePath}",
                    "-lPLMLibTorchWrapper",
                    "-framework", "Accelerate",
                    "-ltorch_lite",
                    "-all_load",
                )
            }
        }
    }
}

// inspired by: https://diamantidis.github.io/2019/08/25/kotlin-multiplatform-project-unit-tests-for-ios-and-android
fun createIosSimulatorTestTask(taskName: String, targetName: String) {
    task(taskName) {
        val target = (kotlin.targets.getByName(targetName) as KotlinNativeTarget)
        dependsOn(target.binaries.getTest("DEBUG").linkTaskName)
        group = JavaBasePlugin.VERIFICATION_GROUP
        description = "Runs iOS tests on a simulator for target $targetName"

        val deviceName = "iPhone 15 Pro"

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
}
