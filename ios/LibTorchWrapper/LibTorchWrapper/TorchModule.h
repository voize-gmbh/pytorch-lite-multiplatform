#import <Foundation/Foundation.h>
#import "ModelOutput.h"
#import "Tensor.h"


NS_ASSUME_NONNULL_BEGIN

struct TensorContainer;

@interface TorchModule : NSObject

- (nullable instancetype)initWithFileAtPath:(NSString*)filePath;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;


- (nullable ModelOutput*)forward:(NSArray<Tensor*>*)inputs;

- (nullable ModelOutput*)forwardMap:(NSDictionary<NSString*, Tensor*>*)inputs;

- (ModelOutput*)processOutputTensor:(struct TensorContainer*)tensor;

@end

NS_ASSUME_NONNULL_END
