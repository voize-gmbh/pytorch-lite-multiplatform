# pytorch-lite-multiplatform

![CI](https://github.com/voize-gmbh/pytorch-lite-multiplatform/actions/workflows/test.yml/badge.svg)

A Kotlin multi-platform wrapper around the PyTorch Lite libraries on [Android](https://pytorch.org/mobile/android/) and [iOS](https://pytorch.org/mobile/ios/).
You can use this library in your Kotlin multi-platform project to write mobile inference code for PyTorch Lite models. The API is very close to the Android API of PyTorch Lite.

## Installation

Add the following to your `shared/build.gradle.kts` as a `commonMain` dependency.

```kotlin
implementation("de.voize:pytorch-lite-multiplatform:<version>")
```

Add the `PLMLibTorchWrapper` pod to your `cocoapods` plugin block in `shared/build.gradle.kts` and add `useLibraries()` because the `PLMLibTorchWrapper` pod has a dependency on the `LibTorch-Lite` pod which contains static libraries.

```kotlin
cocoapods {
    ...

    pod("PLMLibTorchWrapper") {
        version = "<version>"
    }

    useLibraries()
}
```

Then, add the following to your `shared/build.gradle.kts` as a workaround until `PLMLibTorchWrapper` pod has a modulemap or [this issue](https://youtrack.jetbrains.com/issue/KT-44155/Cocoapods-doesnt-support-pods-without-module-map-file-inside) is resolved.

```kotlin
tasks.named<org.jetbrains.kotlin.gradle.tasks.DefFileTask>("generateDefPLMLibTorchWrapper").configure {
    doLast {
        outputFile.writeText("""
            language = Objective-C
            headers = LibTorchWrapper.h
        """.trimIndent())
    }
}
```

Additional steps:

- make sure bitcode is disabled in your iOS XCode project
- make sure that your iOS app's Podfile does **not** include `use_frameworks!`

## Usage

First, [export your PyTorch model for the lite interpreter](https://pytorch.org/tutorials/recipes/mobile_interpreter.html).
Manage in your application how the exported model file is stored on device, e.g. bundled with your app, downloaded from a server during app initialization or something else.
Then you can initialize the `TorchModule` with the path to the model file.

```kotlin
import de.voize.pytorch_lite_multiplatform.TorchModule

val module = TorchModule(path = "<path/to/model.ptl>")
```

Once you initialized the model you are ready to run inference.

Just like in the Android API of PyTorch Lite, you can use `IValue` and `Tensor` to pass input data into your model and to process the model output. To manage the memory allocated for your tensors you need to use `plmScoped` to specify up to which point you need to keep the memory allocated.

```kotlin
import de.voize.pytorch_lite_multiplatform.*

plmScoped {
    val inputTensor = Tensor.fromBlob(
        data = floatArrayOf(...),
        shape = longArrayOf(...),
        scope = this
    )

    val inputIValue = IValue.fromTensor(inputTensor)

    val output = module.forward(inputIValue)
    // you could also use
    // module.runMethod("forward", inputIValue)

    val outputTensor = output.toTensor()
    val outputData = outputTensor.getDataAsFloatArray()

    ...
}
```

`IValue`s are very flexible to construct the input you need for your model, e.g. tensors, scalars, flags, dicts, tuples etc. Refer to the [`IValue`]() interface for all available options and browse [PyTorch's Android Demo](https://github.com/pytorch/android-demo-app) for examples on inferences using `IValue`.

## Memory Management

To make management of resources allocated for your inference across Android and iOS simpler we introduced the `PLMScope` and the `plmScoped` util. On Android, the JVM garbage collection and PyTorch Lite manage the allocated memory nicely so `plmScoped` is a noop. But on iOS, memory is allocated in Kotlin and exchanged with native Objective-C code and vice-versa without automatic deallocation of resources. This is where `plmScoped` comes in and frees the memory allocated for your inference. So it is important that you properly define the scope in which resources need to stay allocated to avoid memory leaks or memory being lost that is needed later.

## Running tests

To run the tests, first create the dummy torchscript module using:

```
python build_dummy_model.py
```

Use a Python environment where the torch dependency is available.

The tests will run inference against this module on iOS and Android using the multiplatform implementation.

### iOS

Copy the created `dummy_model.ptl` into your simulator documents directory and run the iOS tests using

```
./gradlew iosSimulatorX64Test
```
