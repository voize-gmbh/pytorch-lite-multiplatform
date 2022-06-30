package de.voize.pytorch_lite_multiplatform

expect class IValue {
    fun isNull(): Boolean
    fun isTensor(): Boolean
    fun isBool(): Boolean
    fun isLong(): Boolean
    fun isDouble(): Boolean
    fun isString(): Boolean
    fun isTuple(): Boolean
    fun isBoolList(): Boolean
    fun isLongList(): Boolean
    fun isDoubleList(): Boolean
    fun isTensorList(): Boolean
    fun isList(): Boolean
    fun isDictStringKey(): Boolean
    fun isDictLongKey(): Boolean

    fun toTensor(): Tensor
    fun toTensorList(): List<Tensor>

    fun toBool(): Boolean
    fun toLong(): Long
    fun toDouble(): Double
    fun toStr(): String
    fun toBoolList(): List<Boolean>
    fun toLongList(): List<Long>
    fun toDoubleList(): List<Double>
    fun toList(): List<IValue>
    fun toTuple(): List<IValue>
    fun toDictStringKey(): Map<String, IValue>
    fun toDictLongKey(): Map<Long, IValue>

    companion object {
        fun optionalNull(): IValue
        fun from(tensor: Tensor): IValue
        fun from(value: Boolean): IValue
        fun from(value: Long): IValue
        fun from(value: Double): IValue
        fun from(value: String): IValue
        fun listFrom(vararg values: IValue): IValue
        fun listFrom(vararg tensors: Tensor): IValue
        fun listFrom(vararg list: Boolean, scope: PLMScope): IValue
        fun listFrom(vararg list: Long, scope: PLMScope): IValue
        fun listFrom(vararg list: Double, scope: PLMScope): IValue
        fun tupleFrom(vararg values: IValue): IValue
        fun dictStringKeyFrom(map: Map<String, IValue>): IValue
        fun dictLongKeyFrom(map: Map<Long, IValue>): IValue
    }
}