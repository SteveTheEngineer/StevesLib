package me.ste.library.lookup.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.*

interface BlockLookup<T> {
    fun get(level: Level, pos: BlockPos, side: Direction?, state: Optional<BlockState>? = null, entity: Optional<BlockEntity>? = null): T?
}