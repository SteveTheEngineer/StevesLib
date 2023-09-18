package me.ste.library.hooks.fabric

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState

object BlockHooksImpl {
    @JvmStatic
    fun getSoundType(state: BlockState, level: LevelReader, pos: BlockPos, entity: Entity?): SoundType {
        return state.soundType
    }
}