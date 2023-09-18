package me.ste.library.fabric.mixin.common;

import me.ste.library.fabric.BlockExtensionsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow public Level level;

    @ModifyVariable(method = "playStepSound", name = "soundType", ordinal = 0, at = @At("LOAD"))
    public SoundType playStepSound(SoundType old, BlockPos pos, BlockState state) {
        return BlockExtensionsHelper.INSTANCE.applySoundType(old, this.level, pos, state, (Entity) (Object) this);
    }
}
