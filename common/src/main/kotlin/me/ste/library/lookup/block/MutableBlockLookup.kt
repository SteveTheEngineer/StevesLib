package me.ste.library.lookup.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

interface MutableBlockLookup<T> {
    fun register(provider: BlockLookupProvider<T, BlockEntity>)
    fun registerBlock(block: Block, provider: BlockLookupProvider<T, BlockEntity>)
    fun <E : BlockEntity> registerBlockEntity(type: BlockEntityType<E>, provider: BlockLookupProvider<T, E>)
}