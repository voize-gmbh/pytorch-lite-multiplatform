#import <Foundation/Foundation.h>
#import "ModelOutput.h"


NS_ASSUME_NONNULL_BEGIN

@interface Tensor : NSObject

- (nullable instancetype)initWithLongData:(long*)data
                                shape:(long long*)shape
                              shapeLength:(size_t)shapeLength;

- (nullable instancetype)initWithFloatData:(float*)data
                                shape:(long long*)shape
                               shapeLength:(size_t)shapeLength;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;

- (void*)getTensor;

@end

NS_ASSUME_NONNULL_END
