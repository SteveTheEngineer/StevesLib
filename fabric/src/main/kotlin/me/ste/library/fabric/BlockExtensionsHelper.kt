package me.ste.library.fabric

import me.ste.library.extension.BlockExtensions
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState

object BlockExtensionsHelper {
    fun applySoundType(
        old: SoundType,
        level: Level,
        pos: BlockPos,
        state: BlockState,
        entity: Entity?
    ): SoundType {
        val extensions = state.block as? BlockExtensions ?: return old
        return extensions.steveslib_getSoundType(state, level, pos, entity) ?: return old
    }
}