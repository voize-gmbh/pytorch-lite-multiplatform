package de.voize.pytorch_lite_multiplatform

import cocoapods.PLMLibTorchWrapper.IValueWrapper
import kotlinx.cinterop.*

actual class IValue internal constructor(val nativeIValue: IValueWrapper) {
    actual fun isNull() = nativeIValue.isNone()
    actual fun isTensor() = nativeIValue.isTensor()
    actual fun isBool() = nativeIValue.isBool()
    actual fun isLong() = nativeIValue.isInt()
    actual fun isDouble() = nativeIValue.isDouble()
    actual fun isString() = nativeIValue.isString()
    actual fun isTuple() = nativeIValue.isTuple()
    actual fun isBoolList() = nativeIValue.isBoolList()
    actual fun isLongList() = nativeIValue.isIntList()
    actual fun isDoubleList() = nativeIValue.isDoubleList()
    actual fun isTensorList() = nativeIValue.isTensorList()
    actual fun isList() = nativeIValue.isList()
    actual fun isDictStringKey() = nativeIValue.isDictStringKey()
    actual fun isDictLongKey() = nativeIValue.isDictLongKey()

    actual fun toTensor() = Tensor(nativeIValue.toTensor())

    actual fun toBool() = nativeIValue.toBool()
    actual fun toLong() = nativeIValue.toInt()
    actual fun toDouble() = nativeIValue.toDouble()
    actual fun toStr() = nativeIValue.toStr()

    actual fun toList(): List<IValue> {
        return (nativeIValue.toList() as List<IValueWrapper>).map { IValue(it) }
    }

    actual fun toTuple(): List<IValue> {
        return (nativeIValue.toTuple() as List<IValueWrapper>).map { IValue(it) }
    }

    actual fun toBoolList() = toList().map { it.toBool() }
    actual fun toLongList() = toList().map { it.toLong() }
    actual fun toDoubleList() = toList().map { it.toDouble() }
    actual fun toTensorList() = toList().map { it.toTensor() }

    actual fun toDictStringKey(): Map<String, IValue> {
        return (nativeIValue.toDictStringKey() as Map<String, IValueWrapper>).mapValues { IValue(it.value) }
    }

    actual fun toDictLongKey(): Map<Long, IValue> {
        return (nativeIValue.toDictLongKey() as Map<Long, IValueWrapper>).mapValues { IValue(it.value) }
    }

    actual companion object {
        actual fun optionalNull(): IValue {
            return IValue(IValueWrapper())
        }

        actual fun from(tensor: Tensor): IValue {
            return IValue(IValueWrapper(tensor.nativeTensor))
        }

        actual fun from(value: Boolean): IValue {
            return IValue(IValueWrapper(value))
        }

        actual fun from(value: Long): IValue {
            return IValue(IValueWrapper(value))
        }

        actual fun from(value: Double): IValue {
            return IValue(IValueWrapper(value))
        }

        actual fun from(value: String): IValue {
            return IValue(IValueWrapper(value))
        }

        actual fun tupleFrom(vararg values: IValue): IValue {
            val nativeIValues = values.map { it.nativeIValue }
            return IValue(IValueWrapper(tuple = nativeIValues))
        }

        actual fun listFrom(vararg values: IValue): IValue {
            val nativeIValues = values.map { it.nativeIValue }
            return IValue(IValueWrapper(list = nativeIValues))
        }

        actual fun listFrom(vararg tensors: Tensor): IValue {
            val nativeTensors = tensors.map { it.nativeTensor }
            return IValue(IValueWrapper(tensors = nativeTensors))
        }

        actual fun listFrom(vararg list: Boolean, scope: PLMScope): IValue = with(scope.nativePlacement) {
            IValue(
                IValueWrapper(
                    allocArray<BooleanVar>(list.size) { value = list[it] },
                    list.size.toULong()
                )
            )
        }

        actual fun listFrom(vararg list: Long, scope: PLMScope): IValue = with(scope.nativePlacement) {
            IValue(
                IValueWrapper(
                    allocArray<LongVar>(list.size) { value = list[it] },
                    list.size.toULong()
                )
            )
        }

        actual fun listFrom(vararg list: Double, scope: PLMScope): IValue = with(scope.nativePlacement) {
            IValue(
                IValueWrapper(
                    allocArray<DoubleVar>(list.size) { value = list[it] },
                    list.size.toULong()
                )
            )
        }

        actual fun dictStringKeyFrom(map: Map<String, IValue>): IValue {
            val nativeIValues = map.mapValues { it.value.nativeIValue }
            return IValue(IValueWrapper(dictStringKey = nativeIValues as Map<Any?, *>))
        }

        actual fun dictLongKeyFrom(map: Map<Long, IValue>): IValue {
            val nativeIValues = map.mapValues { it.value.nativeIValue }
            return IValue(IValueWrapper(dictLongKey = nativeIValues as Map<Any?, *>))
        }
    }
}
