package me.ste.library.fabric.mixin.client;

import me.ste.library.client.extension.fabric.PlatformClientExtensionsImpl;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "getArmPose", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getArmPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        var stack = player.getItemInHand(hand);

        var extensions = PlatformClientExtensionsImpl.INSTANCE.getEXTENSIONS().get(stack.getItem());
        if (extensions == null) {
            return;
        }

        var pose = extensions.getArmPose(player, hand, stack);
        if (pose == null) {
            return;
        }

        cir.setReturnValue(pose);
    }
}
