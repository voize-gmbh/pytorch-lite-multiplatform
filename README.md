# pytorch-lite-multiplatform

A Kotlin multi-platform wrapper around the PyTorch Lite libraries on [Android](https://pytorch.org/mobile/android/) and [iOS](https://pytorch.org/mobile/ios/).
You can use this library in your Kotlin multi-platform project to write mobile inference code for PyTorch Lite models.

## Installation

Add the following to your `build.gradle` as a `commonMain` dependency.

```
implementation("de.voize:pytorch-lite-multiplatform:<version>")
```

## Usage

First, [export your PyTorch model for the lite interpreter](https://pytorch.org/tutorials/recipes/mobile_interpreter.html).
Manage in your application how the exported model file is stored on device, e.g. bundled with your app, downloaded from a server during app initialization or something else.
Then you can initialize the `TorchModule` with the path to the model file.

```kotlin
import de.voize.pytorch_lite_multiplatform.TorchModule

val module = TorchModule(path = "<path/to/model.ptl>")
```

Once you initialized the model you are ready to run inference.

**Current inference limitations:**

- only the `forward` method is supported, `runMethod` is not yet supported
- all inputs of your `forward` method have to be plain tensors, no Python dictionaries, scalars, boolean flags etc.
- only float and long tensors are supported
- the return type of your model's `forward` must be a plain tensor of data type float

To setup the input tensors for your model use the `FloatTensor` and `LongTensor` classes.

```kotlin
import de.voize.pytorch_lite_multiplatform.Tensor

val inputTensor = FloatTensor(
    data = floatArrayOf(...),
    shape = longArrayOf(...),
)
val output = module.forward(listOf(inputTensor))

// process output.data with shape output.shape
```

The return type of your inference is `ModelOutput` which contains the flat `data` (`FloatArray`) and `shape` (`LongArray`) property.
