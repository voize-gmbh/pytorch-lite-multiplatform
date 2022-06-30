#import "TorchModule.h"
#import "Tensor.h"
#import "IValueWrapper.h"
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

- (nullable IValueWrapper*)runMethod:(NSString*)methodName
                            inputs:(NSArray<IValueWrapper*>*)inputs {
    try {
        std::vector<at::IValue> iValues;
        for (IValueWrapper* input in inputs) {
            iValues.push_back(*(at::IValue*)(input.getIValue));
        }

        at::IValue output = _impl.get_method(std::string(methodName.UTF8String))(std::move(iValues));
        
        return [[IValueWrapper alloc] initWithNativeIValue: &output];
    } catch (const std::exception& exception) {
        NSLog(@"%s", exception.what());
    }
    
    return nil;
}

@end
