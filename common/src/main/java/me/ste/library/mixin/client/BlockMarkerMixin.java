package me.ste.library.mixin.client;

import me.ste.library.client.extension.BlockClientExtensions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BlockMarker;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockMarker.class)
public abstract class BlockMarkerMixin extends TextureSheetParticle {
    private BlockMarkerMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ClientLevel clientLevel, double d, double e, double f, BlockState blockState, CallbackInfo ci) {
        var extensions = BlockClientExtensions.Companion.getExtensions(blockState);
        if (extensions == null) {
            return;
        }

        var icon = extensions.getParticleIcon(blockState, clientLevel, null);
        if (icon == null) {
            return;
        }

        this.setSprite(icon);
    }
}
