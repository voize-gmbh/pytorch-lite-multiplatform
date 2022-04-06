#import "TorchModule.h"
#import "ModelOutput.h"
#import <LibTorch-Lite/LibTorch-Lite.h>

struct TensorContainer {
    at::Tensor tensor;
};

@implementation TorchModule {
@protected
    torch::jit::mobile::Module _impl;
}

- (nullable instancetype)initWithFileAtPath:(NSString*)filePath {
    self = [super init];
    if (self) {
        try {
            _impl = torch::jit::_load_for_mobile(filePath.UTF8String);
            torch::autograd::AutoGradMode guard(false);
            at::AutoNonVariableTypeMode non_var_type_mode(true);
        } catch (const std::exception& exception) {
            NSLog(@"%s", exception.what());
            return nil;
        }
    }
    return self;
}

- (nullable ModelOutput*)inferenceInputA:(float*)inputA
                           inputAShape:(long long*)inputAShape
                     inputAShapeLength:(size_t)inputAShapeLength
                                inputB:(float*)inputB
                           inputBShape:(long long*)inputBShape
                     inputBShapeLength:(size_t)inputBShapeLength {
    try {
        at::IntArrayRef shapeA = at::ArrayRef<int64_t>((int64_t*)inputAShape, inputAShapeLength);
        at::IntArrayRef shapeB = at::ArrayRef<int64_t>((int64_t*)inputBShape, inputBShapeLength);
        
        at::Tensor tensorA = torch::from_blob(inputA, shapeA, at::kFloat);
        at::Tensor tensorB = torch::from_blob(inputB, shapeB, at::kFloat);
        
        auto outputTensor = _impl.forward({tensorA, tensorB}).toTensor();
        TensorContainer container = { .tensor = outputTensor };
        return [self processOutputTensor:(&container)];
    } catch (const std::exception& exception) {
        NSLog(@"%s", exception.what());
    }
    
    return nil;
}

- (nullable ModelOutput*)inferenceLongInput:(long*)input
                               inputShape:(long long*)inputShape
                         inputShapeLength:(size_t)inputShapeLength {
    try {
        at::IntArrayRef inShape = at::ArrayRef<int64_t>((int64_t*)inputShape, inputShapeLength);
        at::Tensor tensor = torch::from_blob(input, inShape, at::kLong);
        
        auto outputTensor = _impl.forward({tensor}).toTensor();
        TensorContainer container = { .tensor = outputTensor };
        return [self processOutputTensor:(&container)];
    } catch (const std::exception& exception) {
        NSLog(@"%s", exception.what());
    }
    
    return nil;
}

- (nullable ModelOutput*)inferenceFloatInput:(float*)input
                                inputShape:(long long*)inputShape
                          inputShapeLength:(size_t)inputShapeLength {
    try {
        at::IntArrayRef inShape = at::ArrayRef<int64_t>((int64_t*)inputShape, inputShapeLength);
        at::Tensor inTensor = torch::from_blob(input, inShape, at::kFloat);
        
        auto outputTensor = _impl.forward({inTensor}).toTensor();
        TensorContainer container = { .tensor = outputTensor };
        return [self processOutputTensor:(&container)];
    } catch (const std::exception& exception) {
        NSLog(@"%s", exception.what());
    }
    
    return nil;
}

- (ModelOutput*)processOutputTensor:(TensorContainer*)tensor {
    const float* outputData = tensor->tensor.data_ptr<float>();
    at::IntArrayRef outputShape = tensor->tensor.sizes();
    
    size_t dataLen = 1;
    for (int i = 0; i < outputShape.size(); i++) {
        dataLen *= *(outputShape.data() + i);
    }
    std::vector<float> outputDataVec {outputData, outputData + dataLen};
    
    NSMutableArray* data = [NSMutableArray array];
    for (int i = 0; i < dataLen; i++) {
        [data addObject:@(outputData[i])];
    }
    
    NSMutableArray* shape = [NSMutableArray array];
    for (int i = 0; i < outputShape.size(); i++) {
        [shape addObject:@(outputShape[i])];
    }
    
    ModelOutput *output = [[ModelOutput alloc] initWithData:[data copy]
                                                     shape:[shape copy]];
    
    return output;
}

@end
