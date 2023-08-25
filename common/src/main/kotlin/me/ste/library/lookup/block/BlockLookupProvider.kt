package me.ste.library.lookup.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier

fun interface BlockLookupProvider<T, E : BlockEntity> {
    fun get(level: Level, pos: BlockPos, side: Direction?, state: Supplier<BlockState>, entity: Supplier<out E?>): T?
}