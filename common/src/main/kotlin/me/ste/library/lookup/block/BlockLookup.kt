package me.ste.library.lookup.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

fun interface BlockLookup<T> {
    fun get(level: Level, pos: BlockPos): T?
    fun get(entity: BlockEntity) = this.get(entity.level!!, entity.blockPos)
}