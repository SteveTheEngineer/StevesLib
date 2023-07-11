package me.ste.library.lookup.block

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.function.Function

interface MutableBlockEntityLookup<T> {
    fun registerBlockEntity(provider: Function<BlockEntity, T?>)
    fun <E : BlockEntity> registerBlockEntity(type: BlockEntityType<E>, provider: Function<E, T?>)
}