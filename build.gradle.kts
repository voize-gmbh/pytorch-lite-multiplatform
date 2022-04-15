import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.6.20"
    id("com.android.library")
    id("com.adarshr.test-logger") version "3.1.0"
    id("maven-publish")
}

group = "de.voize"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

kotlin {
    android {
        publishLibraryVariants("debug", "release")
    }

    val frameworkBasePath = "/Users/voize/voize/pytorch-lite-multiplatform/ios/LibTorchWrapper/build/LibTorchWrapper.xcframework"

    ios()

    iosArm64 {
        val frameworkPath = "$frameworkBasePath/ios-arm64"

        compilations.getByName("main") {
            val LibTorchWrapper by cinterops.creating {
                defFile("src/nativeInterop/cinterop/LibTorchWrapper.def")
                compilerOpts("-framework", "LibTorchWrapper", "-F${frameworkPath}")
            }
        }

        binaries {
            framework {
                embedBitcode("disable")
            }
        }

        binaries.all {
            linkerOpts("-framework", "LibTorchWrapper", "-F${frameworkPath}")
        }
    }

    iosX64 {
        val frameworkPath = "$frameworkBasePath/ios-x86_64-simulator"

        compilations.getByName("main") {
            val LibTorchWrapper by cinterops.creating {
                defFile("src/nativeInterop/cinterop/LibTorchWrapper.def")
                compilerOpts("-framework", "LibTorchWrapper", "-F${frameworkPath}")
            }
        }

        binaries {
            framework {
                embedBitcode("disable")
            }
        }

        binaries.all {
            linkerOpts("-framework", "LibTorchWrapper", "-F${frameworkPath}")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.suparnatural.kotlin:fs:1.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                rootProject
                implementation("org.pytorch:pytorch_android_lite:1.11")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
    }
}

task("buildLibTorchWrapperXCFramework") {
    exec {
        workingDir("ios/LibTorchWrapper")
        commandLine("rm", "-rf", "build")
    }

    exec {
        workingDir("ios/LibTorchWrapper")
        commandLine(
            "xcodebuild", "archive",
            "-workspace", "LibTorchWrapper.xcworkspace",
            "-scheme", "LibTorchWrapper",
            "-archivePath", "build/LibTorchWrapper-iphoneos.xcarchive",
            "-sdk", "iphoneos",
            "SKIP_INSTALL=NO"
        )
    }

    exec {
        workingDir("ios/LibTorchWrapper")
        commandLine(
            "xcodebuild", "archive",
            "-workspace", "LibTorchWrapper.xcworkspace",
            "-scheme", "LibTorchWrapper",
            "-archivePath", "build/LibTorchWrapper-iphonesimulator.xcarchive",
            "-sdk", "iphonesimulator",
            "VALID_ARCHS=\"x86_64\"",
            "SKIP_INSTALL=NO"
        )
    }

    exec {
        workingDir("ios/LibTorchWrapper")
        commandLine(
            "xcodebuild", "-create-xcframework",
            "-framework", "build/LibTorchWrapper-iphonesimulator.xcarchive/Products/Library/Frameworks/LibTorchWrapper.framework",
            "-framework", "build/LibTorchWrapper-iphoneos.xcarchive/Products/Library/Frameworks/LibTorchWrapper.framework",
            "-output", "build/LibTorchWrapper.xcframework"
        )
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
            commandLine("xcrun", "simctl", "spawn", device, binary.absolutePath)
        }
    }
}

android {
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
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