package me.ste.library.util

import me.ste.library.serialization.NBTSerializable
import net.minecraft.nbt.CompoundTag

object StevesLibNBTUtils {
    fun save(compound: CompoundTag, serializable: NBTSerializable, key: String) {
        val childTag = CompoundTag()
        serializable.save(childTag)
        compound.put(key, childTag)
    }

    fun load(compound: CompoundTag, serializable: NBTSerializable, key: String) {
        val childTag = compound.getCompound(key)
        serializable.load(childTag)
    }
}