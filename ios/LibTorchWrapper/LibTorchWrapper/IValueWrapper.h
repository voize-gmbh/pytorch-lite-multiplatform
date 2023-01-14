#import <Foundation/Foundation.h>
#import "Tensor.h"


NS_ASSUME_NONNULL_BEGIN

@interface IValueWrapper : NSObject

- (nullable instancetype)init;
- (nullable instancetype)initWithNativeIValue:(void*)nativeIValue;
- (nullable instancetype)initWithTensor:(Tensor*)tensor;
- (nullable instancetype)initWithBool:(bool)value;
- (nullable instancetype)initWithLong:(int64_t)value;
- (nullable instancetype)initWithDouble:(double)value;
- (nullable instancetype)initWithString:(NSString*)value;
- (nullable instancetype)initWithBoolList:(bool*)data length:(size_t)length;
- (nullable instancetype)initWithDoubleList:(double*)data length:(size_t)length;
- (nullable instancetype)initWithFloatList:(float*)data length:(size_t)length;
- (nullable instancetype)initWithLongList:(int64_t*)data length:(size_t)length;
- (nullable instancetype)initWithTuple:(NSArray<IValueWrapper*>*)nativeIValues;
- (nullable instancetype)initWithList:(NSArray<IValueWrapper*>*)nativeIValues;
- (nullable instancetype)initWithTensors:(NSArray<Tensor*>*)tensors;
- (nullable instancetype)initWithDictStringKey:(NSDictionary<NSString*, IValueWrapper*>*)dict;
- (nullable instancetype)initWithDictLongKey:(NSDictionary<NSNumber*, IValueWrapper*>*)dict;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;

- (NSString*)toStr;
- (Tensor*)toTensor;
- (bool)toBool;
- (int64_t)toInt;
- (double)toDouble;
- (NSArray<IValueWrapper*>*)toList;
- (NSArray<IValueWrapper*>*)toTuple;
- (NSDictionary*)toDictStringKey;
- (NSDictionary*)toDictLongKey;

- (bool)isNone;
- (bool)isTensor;
- (bool)isBool;
- (bool)isInt;
- (bool)isDouble;
- (bool)isString;
- (bool)isTuple;
- (bool)isBoolList;
- (bool)isIntList;
- (bool)isDoubleList;
- (bool)isTensorList;
- (bool)isList;
- (bool)isDictStringKey;
- (bool)isDictLongKey;

- (void*)getIValue;

@end

NS_ASSUME_NONNULL_END
