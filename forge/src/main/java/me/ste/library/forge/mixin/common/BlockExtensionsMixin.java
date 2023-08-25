package me.ste.library.forge.mixin.common;

import me.ste.library.extension.BlockExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockExtensions.class)
public interface BlockExtensionsMixin extends BlockExtensions, IForgeBlock {
    @Override
    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        var soundType = this.steveslib_getSoundType(state, level, pos, entity);

        if (soundType == null) {
            return IForgeBlock.super.getSoundType(state, level, pos, entity);
        }

        return soundType;
    }
}
