package me.ste.library.forge.mixin.common;

import me.ste.library.extension.BlockEntityExtensions;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntityExtensions.class)
public interface BlockEntityExtensionsMixin extends BlockEntityExtensions, IForgeBlockEntity {
    @Override
    default void onLoad() {
        this.steveslib_onLoad();
    }
}
