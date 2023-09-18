package me.ste.library.fabric.mixin.common.level;

import me.ste.library.extension.BlockEntityExtensions;
import me.ste.library.fabric.LevelMixinExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    @Shadow @Final
    Level level;

    @Shadow public abstract Map<BlockPos, BlockEntity> getBlockEntities();

    @Inject(method = "registerAllBlockEntitiesAfterLevelLoad", at = @At("HEAD"))
    public void registerAllBlockEntitiesAfterLevelLoad(CallbackInfo ci) {
        var extension = (LevelMixinExtension) this.level;

        this.getBlockEntities().values().forEach(entity -> {
            if (!(entity instanceof BlockEntityExtensions entityExtensions)) {
                return;
            }

            extension.getNewBlockEntities().add(entityExtensions);
        });
    }

    @Inject(method = "addAndRegisterBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", shift = At.Shift.AFTER))
    public void addAndRegisterBlockEntity(BlockEntity entity, CallbackInfo ci) {
        if (!(entity instanceof BlockEntityExtensions entityExtensions)) {
            return;
        }

        entityExtensions.steveslib_onLoad();
    }
}
