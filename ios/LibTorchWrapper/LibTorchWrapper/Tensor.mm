#import <LibTorch-Lite/LibTorch-Lite.h>
#import <Foundation/Foundation.h>
#import "Tensor.h"

@implementation Tensor {
    at::Tensor _tensor;
}

- (nullable instancetype)initWithLongData:(long*)data
                                shape:(long long*)shape
                          shapeLength:(size_t)shapeLength {
    self = [super init];
    if (self) {
        at::IntArrayRef inShape = at::ArrayRef<int64_t>((int64_t*)shape, shapeLength);
        _tensor = torch::from_blob(data, inShape, at::kLong);
    }
    return self;
}

- (nullable instancetype)initWithFloatData:(float*)data
                                shape:(long long*)shape
                          shapeLength:(size_t)shapeLength {
    self = [super init];
    if (self) {
        at::IntArrayRef inShape = at::ArrayRef<int64_t>((int64_t*)shape, shapeLength);
        _tensor = torch::from_blob(data, inShape, at::kFloat);
    }
    return self;
}

- (void*)getTensor {
    return &self->_tensor;
}

@end
