package me.ste.library.lookup.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.function.Function

interface MutableBlockLookup<T> {
    fun register(provider: BlockLookupProvider<T>)

    fun registerBlockState(provider: BlockStateLookupProvider<T>)
    fun registerBlockState(block: Block, provider: BlockStateLookupProvider<T>)
}