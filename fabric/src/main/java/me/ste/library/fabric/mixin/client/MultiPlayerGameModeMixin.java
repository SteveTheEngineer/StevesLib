package me.ste.library.fabric.mixin.client;

import me.ste.library.fabric.BlockExtensionsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(method = "continueDestroyBlock", ordinal = 0, name = "soundType", at = @At("LOAD"))
    public SoundType continueDestroyBlock(SoundType old, BlockPos pos) {
        var level = this.minecraft.level;
        var state = level.getBlockState(pos);

        return BlockExtensionsHelper.INSTANCE.applySoundType(old, level, pos, state, this.minecraft.player);
    }
}
