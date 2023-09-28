package me.ste.library.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
interface MinecraftAccessor {
    @Invoker("runTick")
    void invokeRunTick(boolean renderLevel);

    @Invoker("updateScreenAndTick")
    void invokeUpdateScreenAndTick(Screen screen);
}