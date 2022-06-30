package de.voize.pytorch_lite_multiplatform

import org.pytorch.IValue as NativeIValue

actual class IValue internal constructor(internal val nativeIValue: NativeIValue) {
    actual fun isNull() = nativeIValue.isNull
    actual fun isTensor() = nativeIValue.isTensor
    actual fun isBool() = nativeIValue.isBool
    actual fun isLong() = nativeIValue.isLong
    actual fun isDouble() = nativeIValue.isDouble
    actual fun isString() = nativeIValue.isString
    actual fun isTuple() = nativeIValue.isTuple
    actual fun isBoolList() = nativeIValue.isBoolList
    actual fun isLongList() = nativeIValue.isLongList
    actual fun isDoubleList() = nativeIValue.isDoubleList
    actual fun isTensorList() = nativeIValue.isTensorList
    actual fun isList() = nativeIValue.isList
    actual fun isDictStringKey() = nativeIValue.isDictStringKey
    actual fun isDictLongKey() = nativeIValue.isDictLongKey

    actual fun toTensor() = Tensor(nativeIValue.toTensor())
    actual fun toTensorList() = nativeIValue.toTensorList().map { Tensor(it) }.toList()

    actual fun toBool() = nativeIValue.toBool()
    actual fun toLong() = nativeIValue.toLong()
    actual fun toDouble() = nativeIValue.toDouble()
    actual fun toStr() = nativeIValue.toStr()
    actual fun toBoolList() = nativeIValue.toBoolList().toList()
    actual fun toLongList() = nativeIValue.toLongList().toList()
    actual fun toDoubleList() = nativeIValue.toDoubleList().toList()
    actual fun toList() = nativeIValue.toList().map { IValue(it) }
    actual fun toTuple() = nativeIValue.toTuple().map { IValue(it) }
    actual fun toDictStringKey() = nativeIValue.toDictStringKey().map { it.key to IValue(it.value) }.toMap()
    actual fun toDictLongKey() = nativeIValue.toDictLongKey().map { it.key to IValue(it.value) }.toMap()

    actual companion object {
        actual fun optionalNull(): IValue {
            return IValue(NativeIValue.optionalNull())
        }

        actual fun from(tensor: Tensor): IValue {
            return IValue(NativeIValue.from(tensor.nativeTensor))
        }

        actual fun from(value: Boolean): IValue {
            return IValue(NativeIValue.from(value))
        }

        actual fun from(value: Long): IValue {
            return IValue(NativeIValue.from(value))
        }

        actual fun from(value: Double): IValue {
            return IValue(NativeIValue.from(value))
        }

        actual fun from(value: String): IValue {
            return IValue(NativeIValue.from(value))
        }

        actual fun listFrom(vararg values: IValue): IValue {
            return IValue(NativeIValue.listFrom(*values.map { it.nativeIValue }.toTypedArray()))
        }

        actual fun listFrom(vararg tensors: Tensor): IValue {
            return IValue(NativeIValue.listFrom(*tensors.map { it.nativeTensor }.toTypedArray()))
        }

        actual fun listFrom(vararg list: Boolean, scope: PLMScope): IValue {
            return IValue(NativeIValue.listFrom(*list))
        }

        actual fun listFrom(vararg list: Long, scope: PLMScope): IValue {
            return IValue(NativeIValue.listFrom(*list))
        }

        actual fun listFrom(vararg list: Double, scope: PLMScope): IValue {
            return IValue(NativeIValue.listFrom(*list))
        }

        actual fun tupleFrom(vararg values: IValue): IValue {
            return IValue(NativeIValue.tupleFrom(*values.map { it.nativeIValue }.toTypedArray()))
        }

        actual fun dictStringKeyFrom(map: Map<String, IValue>): IValue {
            return IValue(NativeIValue.dictStringKeyFrom(map.map { it.key to it.value.nativeIValue }.toMap()))
        }

        actual fun dictLongKeyFrom(map: Map<Long, IValue>): IValue {
            return IValue(NativeIValue.dictLongKeyFrom(map.map { it.key to it.value.nativeIValue }.toMap()))
        }
    }
}