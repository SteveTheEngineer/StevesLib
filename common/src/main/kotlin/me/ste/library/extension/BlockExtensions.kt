package me.ste.library.extension

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState

interface BlockExtensions {
    fun steveslib_getSoundType(state: BlockState, level: LevelReader, pos: BlockPos, entity: Entity?): SoundType? = null
}