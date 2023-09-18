package me.ste.library.fabric.mixin.common.level;

import me.ste.library.extension.BlockEntityExtensions;
import me.ste.library.fabric.LevelMixinExtension;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Level.class)
public class LevelMixin implements LevelMixinExtension {
    @Unique
    public List<BlockEntityExtensions> newBlockEntities = new ArrayList<>();

    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    public void tickBlockEntities(CallbackInfo ci) {
        List<BlockEntityExtensions> entities = new ArrayList<>(this.newBlockEntities);
        this.newBlockEntities.clear();

        for (var entity : entities) {
            entity.steveslib_onLoad();
        }
    }

    @Override
    public List<BlockEntityExtensions> getNewBlockEntities() {
        return this.newBlockEntities;
    }
}
