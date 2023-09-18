package me.ste.library.fabric.mixin.client;

import me.ste.library.fabric.BlockExtensionsHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow private @Nullable ClientLevel level;

    @ModifyVariable(method = "levelEvent", ordinal = 0, name = "soundType", at = @At("LOAD"))
    public SoundType levelEvent(SoundType old, int type, BlockPos pos, int data) {
        var level = this.level;
        var state = Block.stateById(data);

        return BlockExtensionsHelper.INSTANCE.applySoundType(old, level, pos, state, null);
    }
}
