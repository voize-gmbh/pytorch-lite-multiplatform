# pytorch-lite-multiplatform

![CI](https://github.com/voize-gmbh/pytorch-lite-multiplatform/actions/workflows/test.yml/badge.svg)

A Kotlin multi-platform wrapper around the PyTorch Lite libraries on [Android](https://pytorch.org/mobile/android/) and [iOS](https://pytorch.org/mobile/ios/).
You can use this library in your Kotlin multi-platform project to write mobile inference code for PyTorch Lite models.

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

To setup the input tensors for your model use the `FloatTensor` and `LongTensor` classes.

```kotlin
import de.voize.pytorch_lite_multiplatform.Tensor

val inputTensor = FloatTensor(
    data = floatArrayOf(...),
    shape = longArrayOf(...),
)
```

Now you can run inference either via `module.forward` or `module.runMethod`.

```kotlin
val output = module.forward(listOf(inputTensor))
```

```kotlin
val output = module.runMethod("mymethod", listOf(inputTensor))
```

The list of given input tensors will be flattened to your module's method arguments.

If your inference method requires a dictionary as input you can use

```kotlin
val output = module.forward(mapOf("x" to inputTensor))
```

The return type of your inference is `ModelOutput` which contains the flat `data` (`FloatArray`) and `shape` (`LongArray`) property.

### Current inference limitations

- the input to your inference method can either be a list of plain tensors (is flatted to arguments) or dictionary with string keys and tensor values
- only float and long tensors are supported
- the return type of your model's `forward` must be a plain tensor of data type float

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
