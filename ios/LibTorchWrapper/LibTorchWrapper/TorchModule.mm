#import "TorchModule.h"
#import "ModelOutput.h"
#import "Tensor.h"
#import <LibTorch-Lite/LibTorch-Lite.h>

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

- (nullable ModelOutput*)runMethod:(NSString*)methodName
                            inputs:(NSArray<Tensor*>*)inputs {
    try {
        std::vector<at::IValue> iValues;
        for (Tensor* tensor in inputs) {
            iValues.push_back(at::IValue(*((at::Tensor*)(tensor.getTensor))));
        }

        at::Tensor outputTensor = _impl.get_method(std::string(methodName.UTF8String))(std::move(iValues)).toTensor();

        return [self processOutputTensor:outputTensor];
    } catch (const std::exception& exception) {
        NSLog(@"%s", exception.what());
    }
    
    return nil;
}

- (nullable ModelOutput*)runMethodMap:(NSString*)methodName
                               inputs:(NSDictionary<NSString*, Tensor*>*)inputs {
    try {
        at::IValue iValue{torch::rand({1})};
        c10::impl::GenericDict inputDict{c10::StringType::get(), iValue.type()};

        for (NSString* key in inputs) {
            at::Tensor* tensor = (at::Tensor*)[inputs objectForKey:key].getTensor;
            inputDict.insert(key.UTF8String, at::IValue(*tensor));
        }

        at::Tensor outputTensor = _impl.run_method(std::string(methodName.UTF8String), inputDict).toTensor();

        return [self processOutputTensor:outputTensor];
    } catch (const std::exception& exception) {
        NSLog(@"%s", exception.what());
    }

    return nil;
}

- (ModelOutput*)processOutputTensor:(at::Tensor&)tensor {
    const float* outputData = tensor.data_ptr<float>();
    at::IntArrayRef outputShape = tensor.sizes();
    
    size_t dataLen = 1;
    for (int i = 0; i < outputShape.size(); i++) {
        dataLen *= *(outputShape.data() + i);
    }
    
    NSMutableArray* data = [NSMutableArray array];
    for (int i = 0; i < dataLen; i++) {
        [data addObject:@(outputData[i])];
    }
    
    NSMutableArray* shape = [NSMutableArray array];
    for (int i = 0; i < outputShape.size(); i++) {
        [shape addObject:@(outputShape[i])];
    }
    
    ModelOutput *output = [[ModelOutput alloc] initWithData:data
                                                     shape:shape];
    
    return output;
}

@end
