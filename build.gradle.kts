import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.6.21"
    kotlin("native.cocoapods") version "1.6.21"
    id("com.android.library")
    id("com.adarshr.test-logger") version "3.1.0"
    id("maven-publish")
}

group = "de.voize"
version = "0.1.0"

repositories {
    google()
    mavenCentral()
}

kotlin {
    android {
        publishLibraryVariants("debug", "release")
    }

    ios()

    cocoapods {
        ios.deploymentTarget = "13.5"

        homepage = "voize.de"
        summary = "test"

        pod("LibTorchWrapper") {
            version = "0.1.0"
            source = path(project.file("ios/LibTorchWrapper"))
        }

        useLibraries()
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

tasks.named<org.jetbrains.kotlin.gradle.tasks.DefFileTask>("generateDefLibTorchWrapper").configure {
    doLast {
        outputFile.writeText("""
            language = Objective-C
            headers = LibTorchWrapper.h
        """.trimIndent())
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

publishing {
    repositories {
        maven {
            url = uri("https://gitlab.com/api/v4/projects/22316489/packages/maven")
            name = "GitLab"
            credentials(HttpHeaderCredentials::class) {
                if (System.getenv("CI") != null) {
                    name = "Job-Token"
                    value = System.getenv("CI_JOB_TOKEN")
                } else {
                    name = "Private-Token"
                    value = project.properties.getOrDefault("gitLabPrivateToken", "") as String
                }
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}