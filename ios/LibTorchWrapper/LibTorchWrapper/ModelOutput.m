#import "ModelOutput.h"

@implementation ModelOutput

- (instancetype)initWithData:(NSArray<NSNumber*>*)data
                       shape:(NSArray<NSNumber*>*)shape {
    self = [super init];
    if (self) {
        _data = data;
        _shape = shape;
    }
    return self;
}

@end
