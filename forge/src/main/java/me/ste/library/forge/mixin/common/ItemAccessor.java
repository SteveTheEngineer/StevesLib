package me.ste.library.forge.mixin.common;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor(value = "renderProperties", remap = false)
    void setRenderProperties(Object renderProperties);
}
