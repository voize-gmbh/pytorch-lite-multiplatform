#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * Process memory counters, exposed so that tests can assert on the effect of
 * memory-mapped model loading rather than taking it on faith.
 */
@interface PLMMemoryInfo : NSObject

/**
 * The process's physical footprint in bytes -- the same figure iOS compares
 * against the per-process memory limit when deciding what to terminate.
 *
 * Dirty and compressed pages count towards it; clean file-backed pages, such as
 * the weight pages of an mmapped model, do not. That is exactly the difference
 * mmapped loading is meant to make, so this is the number to watch.
 *
 * Returns 0 if the counter could not be read.
 */
+ (uint64_t)physFootprintBytes;

/** Resident size in bytes, including clean file-backed pages. Returns 0 on failure. */
+ (uint64_t)residentSizeBytes;

@end

NS_ASSUME_NONNULL_END
