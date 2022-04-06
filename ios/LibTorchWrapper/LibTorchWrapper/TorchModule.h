#import <Foundation/Foundation.h>
#import "ModelOutput.h"


NS_ASSUME_NONNULL_BEGIN

struct TensorContainer;

@interface TorchModule : NSObject

- (nullable instancetype)initWithFileAtPath:(NSString*)filePath
NS_SWIFT_NAME(init(fileAtPath:))NS_DESIGNATED_INITIALIZER;
+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;

- (nullable ModelOutput*)inferenceLongInput:(long*)longInput
                               inputShape:(long long*)inputShape
                         inputShapeLength:(size_t)inputShapeLength NS_SWIFT_NAME(inference(longInput:inputShape:inputShapeLength:));

- (nullable ModelOutput*)inferenceFloatInput:(float*)floatInput
                                inputShape:(long long*)inputShape
                          inputShapeLength:(size_t)inputShapeLength NS_SWIFT_NAME(inference(floatInput:inputShape:inputShapeLength:));

- (nullable ModelOutput*)inferenceInputA:(float*)inputA
                           inputAShape:(long long*)inputAShape
                     inputAShapeLength:(size_t)inputAShapeLength
                                inputB:(float*)inputB
                           inputBShape:(long long*)inputBShape
                     inputBShapeLength:(size_t)inputBShapeLength NS_SWIFT_NAME(inference(inputA:inputAShape:inputAShapeLength:inputB:inputBShape:inputBShapeLength:));

- (ModelOutput*)processOutputTensor:(struct TensorContainer*)tensor;

@end

NS_ASSUME_NONNULL_END
