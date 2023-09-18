package me.ste.library.hooks

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState

object BlockHooks {
    @JvmStatic
    @ExpectPlatform
    fun getSoundType(state: BlockState, level: LevelReader, pos: BlockPos, entity: Entity?): SoundType {
        throw UnsupportedOperationException()
    }
}