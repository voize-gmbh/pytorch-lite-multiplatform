#import <Foundation/Foundation.h>
#import "IValueWrapper.h"


NS_ASSUME_NONNULL_BEGIN

struct TensorContainer;

@interface TorchModule : NSObject

- (nullable instancetype)initWithFileAtPath:(NSString*)filePath;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;


- (nullable IValueWrapper*)runMethod:(NSString*)methodName
                                inputs:(NSArray<IValueWrapper*>*)inputs;

@end

NS_ASSUME_NONNULL_END
