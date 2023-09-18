package me.ste.library.mixin.client;

import me.ste.library.client.extension.BlockClientExtensions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TerrainParticle.class)
public abstract class TerrainParticleMixin extends TextureSheetParticle {
    @Shadow @Final private BlockPos pos;

    private TerrainParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
    public void init(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
        var extensions = BlockClientExtensions.Companion.getExtensions(blockState);
        if (extensions == null) {
            return;
        }

        var icon = extensions.getParticleIcon(blockState, clientLevel, pos);
        if (icon == null) {
            return;
        }

        this.setSprite(icon);
    }
}
