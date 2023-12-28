package me.ste.library.serialization

import net.minecraft.nbt.CompoundTag

interface NBTSerializable {
    fun save(tag: CompoundTag)
    fun load(tag: CompoundTag)
}