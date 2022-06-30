#import <Foundation/Foundation.h>


NS_ASSUME_NONNULL_BEGIN

@interface Tensor : NSObject

- (nullable instancetype)initWithTensor:(void*)tensor;

- (nullable instancetype)initWithLongData:(long*)data
                                shape:(long long*)shape
                              shapeLength:(size_t)shapeLength;

- (nullable instancetype)initWithFloatData:(float*)data
                                shape:(long long*)shape
                               shapeLength:(size_t)shapeLength;

- (nullable instancetype)initWithDoubleData:(double*)data
                                shape:(long long*)shape
                               shapeLength:(size_t)shapeLength;

- (nullable instancetype)initWithIntData:(int32_t*)data
                                shape:(long long*)shape
                               shapeLength:(size_t)shapeLength;


+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;

- (void*)getTensor;

- (NSArray<NSNumber*>*)getDataAsIntArray;

- (NSArray<NSNumber*>*)getDataAsDoubleArray;

- (NSArray<NSNumber*>*)getDataAsFloatArray;

- (NSArray<NSNumber*>*)getDataAsLongArray;

- (NSArray<NSNumber*>*)shape;

@end

NS_ASSUME_NONNULL_END
