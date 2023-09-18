package me.ste.library.mixin.client;

import me.ste.library.client.extension.BlockClientExtensions;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {
    @Inject(method = "getParticleIcon", at = @At("HEAD"), cancellable = true)
    public void getParticleIcon(BlockState state, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        var extensions = BlockClientExtensions.Companion.getExtensions(state);
        if (extensions == null) {
            return;
        }

        var icon = extensions.getParticleIcon(state, null, null);
        if (icon == null) {
            return;
        }

        cir.setReturnValue(icon);
    }
}
