package me.ste.library.mixin.common;

import net.minecraft.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class DataMainMixin {
    @Inject(method = "main", at = @At("TAIL"), remap = false)
    private static void main(String[] args, CallbackInfo ci) {
        if (!System.getProperty("steveslib.exitWhenComplete").equals("true")) {
            return;
        }

        System.exit(0);
    }
}
