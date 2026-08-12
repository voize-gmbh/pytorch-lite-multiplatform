#import <Foundation/Foundation.h>
#import "IValueWrapper.h"


NS_ASSUME_NONNULL_BEGIN

struct TensorContainer;

@interface TorchModule : NSObject

- (nullable instancetype)initWithFileAtPath:(NSString*)filePath;

/**
 * Loads a module, optionally memory-mapping the model file.
 *
 * With useMmap set, weight storages point directly into a read-only mapping of
 * the file instead of being copied onto the heap. Those pages stay clean and
 * file-backed, so the OS can evict them under memory pressure rather than
 * counting them as resident app memory. Only records stored uncompressed and
 * aligned can be mapped; anything else is copied as usual, so this is always
 * safe to enable, but the benefit depends on how the model was serialized.
 */
- (nullable instancetype)initWithFileAtPath:(NSString*)filePath
                                    useMmap:(BOOL)useMmap;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;


- (nullable IValueWrapper*)runMethod:(NSString*)methodName
                                inputs:(NSArray<IValueWrapper*>*)inputs;

@end

NS_ASSUME_NONNULL_END
