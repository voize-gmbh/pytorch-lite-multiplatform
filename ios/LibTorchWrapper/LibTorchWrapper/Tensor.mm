#import <VoizeLibTorch-Lite/LibTorch-Lite.h>
#import <Foundation/Foundation.h>
#import "Tensor.h"

@implementation Tensor {
    at::Tensor _tensor;
}

- (nullable instancetype)initWithTensor:(void*)tensor {
    self = [super init];
    if (self) {
        _tensor = *((at::Tensor*)tensor);
    }
    return self;
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

- (nullable instancetype)initWithDoubleData:(double*)data
                                shape:(long long*)shape
                          shapeLength:(size_t)shapeLength {
    self = [super init];
    if (self) {
        at::IntArrayRef inShape = at::ArrayRef<int64_t>((int64_t*)shape, shapeLength);
        _tensor = torch::from_blob(data, inShape, at::kDouble);
    }
    return self;
}

- (nullable instancetype)initWithIntData:(int32_t*)data
                                shape:(long long*)shape
                          shapeLength:(size_t)shapeLength {
    self = [super init];
    if (self) {
        at::IntArrayRef inShape = at::ArrayRef<int64_t>((int64_t*)shape, shapeLength);
        _tensor = torch::from_blob(data, inShape, at::kInt);
    }
    return self;
}

- (void*)getTensor {
    return &self->_tensor;
}

- (NSArray<NSNumber*>*)getDataAsDoubleArray {
    double* outputData = _tensor.data_ptr<double>();
    at::IntArrayRef outputShape = _tensor.sizes();

    size_t dataLen = 1;
    for (int i = 0; i < outputShape.size(); i++) {
        dataLen *= *(outputShape.data() + i);
    }

    NSMutableArray* data = [NSMutableArray array];
    for (int i = 0; i < dataLen; i++) {
        [data addObject:@(outputData[i])];
    }

    return data;
}


- (NSArray<NSNumber*>*)getDataAsIntArray {
    int32_t* outputData = _tensor.data_ptr<int32_t>();
    at::IntArrayRef outputShape = _tensor.sizes();

    size_t dataLen = 1;
    for (int i = 0; i < outputShape.size(); i++) {
        dataLen *= *(outputShape.data() + i);
    }

    NSMutableArray* data = [NSMutableArray array];
    for (int i = 0; i < dataLen; i++) {
        [data addObject:@(outputData[i])];
    }

    return data;
}

- (NSArray<NSNumber*>*)getDataAsFloatArray {
    float* outputData = _tensor.data_ptr<float>();
    at::IntArrayRef outputShape = _tensor.sizes();

    size_t dataLen = 1;
    for (int i = 0; i < outputShape.size(); i++) {
        dataLen *= *(outputShape.data() + i);
    }

    NSMutableArray* data = [NSMutableArray array];
    for (int i = 0; i < dataLen; i++) {
        [data addObject:@(outputData[i])];
    }

    return data;
}

- (NSArray<NSNumber*>*)getDataAsLongArray {
    long long* outputData = _tensor.data_ptr<long long>();
    at::IntArrayRef outputShape = _tensor.sizes();

    size_t dataLen = 1;
    for (int i = 0; i < outputShape.size(); i++) {
        dataLen *= *(outputShape.data() + i);
    }

    NSMutableArray* data = [NSMutableArray array];
    for (int i = 0; i < dataLen; i++) {
        [data addObject:@(outputData[i])];
    }

    return data;
}

- (NSArray<NSNumber*>*)shape {
    auto v = _tensor.sizes().vec();
    NSMutableArray* shape = [NSMutableArray array];
    for (int i = 0; i < v.size(); i++) {
        [shape addObject:@(v[i])];
    }
    return shape;
}

@end
