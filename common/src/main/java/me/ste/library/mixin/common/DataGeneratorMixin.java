package me.ste.library.mixin.common;

import me.ste.library.datagen.DataGeneratorExtensions;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(DataGenerator.class)
public class DataGeneratorMixin implements DataGeneratorExtensions {
    @Nullable
    @Unique
    private Path outputFolderOverride;

    @Nullable
    @Override
    public Path getSteveslib_outputFolder() {
        return this.outputFolderOverride;
    }

    @Override
    public void setSteveslib_outputFolder(@Nullable Path path) {
        this.outputFolderOverride = path;
    }

    @Inject(method = "getOutputFolder()Ljava/nio/file/Path;", at = @At("HEAD"), cancellable = true)
    public void getOutputFolder(CallbackInfoReturnable<Path> cir) {
        if (this.outputFolderOverride != null) {
            cir.setReturnValue(this.outputFolderOverride);
        }
    }
}
