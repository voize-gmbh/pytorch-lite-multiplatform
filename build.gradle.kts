plugins {
    kotlin("multiplatform") version "1.6.20"
    id("com.android.application")
}

group = "de.voize"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

kotlin {
    /*
    ios {
        binaries {
            framework {
                baseName = "pytorch-lite-multiplatform"
            }
        }
    }

     */

    iosArm64 {
        val frameworkPath = "/Users/voize/Library/Developer/Xcode/DerivedData/LibTorchWrapper-frrwmpkqiqjnafefetsiauquiyhs/Build/Products/Debug-iphoneos"

        compilations.getByName("main") {
            val LibTorchWrapper by cinterops.creating {
                // Path to .def file
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
            // Tell the linker where the framework is located.
            linkerOpts("-framework", "LibTorchWrapper", "-F${frameworkPath}")
        }
    }

    android()
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

android {
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        applicationId = "de.voize.pytorch_lite_multiplatform"
        minSdkVersion(24)
        targetSdkVersion(29)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

