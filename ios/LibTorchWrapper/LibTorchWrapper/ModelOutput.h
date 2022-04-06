#import <Foundation/Foundation.h>

@interface ModelOutput : NSObject

@property (readonly) NSArray<NSNumber*>* data;
@property (readonly) NSArray<NSNumber*>* shape;

- (instancetype)initWithData:(NSArray<NSNumber*>*)data
                       shape:(NSArray<NSNumber*>*)shape;

@end
