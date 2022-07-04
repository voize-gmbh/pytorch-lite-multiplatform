# pytorch-lite-multiplatform example project

This example project demonstrates the setup of pytorch-lite-multiplatform in a standard Kotlin Multiplatform Mobile project.

The Android and iOS app run a basic inference against the `dummy_module.ptl`
which can be created using the `build_dummy_model.py` in the root of the repository.
Please check the console logs to see if the inference ran successfully.

Once the module file was created the iOS app will pick it up automatically and copy it into the app.  
But on Android, the app requires that the `dummy_module.ptl` is present in `androidApp/src/main/assets/`, for this copy the file to this folder:

```bash
cp ../dummy_module.ptl androidApp/src/main/assets/
```
