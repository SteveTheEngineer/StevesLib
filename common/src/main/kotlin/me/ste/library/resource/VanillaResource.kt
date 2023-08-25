package me.ste.library.resource

import net.minecraft.nbt.CompoundTag
import java.util.function.Consumer

interface VanillaResource<T> : ResourceType {
    val obj: T
    val tag: CompoundTag?

    fun isSameObject(other: VanillaResource<T>) = this.obj == other.obj
    fun isSameTag(other: VanillaResource<T>) = this.tag == other.tag
    fun isSame(other: VanillaResource<T>) = this.isSameObject(other) && this.isSameTag(other)

    fun withTag(tagConsumer: Consumer<CompoundTag>): VanillaResource<T>
}