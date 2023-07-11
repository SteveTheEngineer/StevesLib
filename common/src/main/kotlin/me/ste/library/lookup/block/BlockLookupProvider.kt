package me.ste.library.lookup.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

fun interface BlockLookupProvider<T> {
    fun get(level: Level, pos: BlockPos): T?
}