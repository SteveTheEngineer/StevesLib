package me.ste.library.lookup.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

fun interface BlockStateLookupProvider<T> {
    fun get(level: Level, pos: BlockPos, state: BlockState): T?
}