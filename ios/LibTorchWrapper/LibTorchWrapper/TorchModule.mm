#import "TorchModule.h"
#import "ModelOutput.h"
#import "Tensor.h"
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
        } catch (const std::exception& exception) {
            NSLog(@"%s", exception.what());
            return nil;
        }
    }
    return self;
}

- (nullable ModelOutput*)forward:(NSArray<Tensor*>*)inputs
                       numInputs:(size_t)numInputs {
    try {
        std::vector<at::IValue> iValues;
        for (Tensor* tensor in inputs) {
            iValues.push_back(at::IValue(*((at::Tensor*)(tensor.getTensor))));
        }
        at::Tensor outputTensor = _impl.forward(iValues).toTensor();
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
