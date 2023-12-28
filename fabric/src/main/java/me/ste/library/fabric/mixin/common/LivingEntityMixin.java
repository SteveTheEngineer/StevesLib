package me.ste.library.fabric.mixin.common;

import me.ste.library.fabric.BlockExtensionsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "playBlockFallSound", name = "soundType", ordinal = 0, at = @At("LOAD"))
    public SoundType playStepSound(SoundType old) {
        var pos = new BlockPos(
                Mth.floor(this.getX()),
                Mth.floor(this.getY() - 0.2),
                Mth.floor(this.getZ())
        );

        var state = this.level().getBlockState(pos);

        return BlockExtensionsHelper.INSTANCE.applySoundType(old, this.level(), pos, state, this);
    }
}
