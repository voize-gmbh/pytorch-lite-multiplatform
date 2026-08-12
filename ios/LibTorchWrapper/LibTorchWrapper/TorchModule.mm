#import "TorchModule.h"
#import "Tensor.h"
#import "IValueWrapper.h"
#import <VoizeLibTorch-Lite/LibTorch-Lite.h>

#include <type_traits>

namespace {

// USE_MMAP is added by voize's LibTorch-Lite patch, so naming the enumerator
// directly would fail to compile against an unpatched build -- including the
// currently published pod. Detect it instead: the wrapper builds either way, and
// where the option is absent it resolves to 0, which leaves the load options
// untouched and simply means mmapped loading is unavailable.
template <typename T, typename = void>
struct MmapLoadOption {
    static constexpr uint64_t value = 0;
};

template <typename T>
struct MmapLoadOption<T, std::void_t<decltype(T::USE_MMAP)>> {
    static constexpr uint64_t value = static_cast<uint64_t>(T::USE_MMAP);
};

constexpr uint64_t kMmapLoadOption =
    MmapLoadOption<torch::jit::MobileModuleLoadOptions>::value;

} // namespace

@implementation TorchModule {
@protected
    torch::jit::mobile::Module _impl;
}

- (nullable instancetype)initWithFileAtPath:(NSString*)filePath {
    return [self initWithFileAtPath:filePath useMmap:NO];
}

- (nullable instancetype)initWithFileAtPath:(NSString*)filePath
                                    useMmap:(BOOL)useMmap {
    self = [super init];
    if (self) {
        try {
            uint64_t options = torch::jit::kDefaultMobileLoadOptions;
            if (useMmap) {
                if (kMmapLoadOption == 0) {
                    // Say so rather than silently loading the copying way, so a
                    // caller measuring memory is not misled by a no-op flag.
                    NSLog(@"TorchModule: useMmap requested but this LibTorch-Lite "
                           "build does not support it; loading without mmap");
                }
                options |= kMmapLoadOption;
            }
            torch::jit::ExtraFilesMap extraFiles;
            _impl = torch::jit::_load_for_mobile(
                filePath.UTF8String, c10::nullopt, extraFiles, options);
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
